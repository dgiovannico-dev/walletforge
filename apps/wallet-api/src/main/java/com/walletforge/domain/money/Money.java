// Il 'package' indica la cartella organizzativa del progetto.
// In architetture pulite (DDD), questo file si trova nel 'domain' (il cuore delle regole di business).
package com.walletforge.domain.money;

// Importa una classe utility standard di Java per effettuare controlli rapidi sugli oggetti (es. validare i null).
import java.util.Objects;
// Importa la classe matematica di sistema di Java, necessaria per usare le funzioni matematiche anti-overflow.
import java.lang.Math;

/**
 * La classe è dichiarata 'final': significa che non può essere estesa da nessun'altra classe.
 * Nessuno può fare 'class FakeMoney extends Money', garantendo la massima sicurezza nel sistema.
 * 
 * Questo componente è un "Value Object" (Oggetto Valore): non ha un ID sul database,
 * ma è definito esclusivamente dai dati che contiene al suo interno.
 */
public final class Money {

    // I campi sono 'private': non sono accessibili direttamente dall'esterno della classe.
    // I campi sono 'final': una volta assegnati nel costruttore, NON possono mai più essere modificati.
    // Questo rende l'oggetto totalmente "Immutabile" (Immutable).
    
    // 'long amountMinor' memorizza l'importo convertito nell'unità più piccola (es. i centesimi).
    // Usare un numero intero (long) elimina alla radice i bug di arrotondamento dei numeri con la virgola.
    private final long amountMinor;
    
    // 'String currency' memorizza il codice della valuta (es. "EUR", "USD").
    private final String currency;

    /**
     * IL COSTRUTTORE È PRIVATO ('private').
     * Blocca l'uso del comando 'new' all'esterno della classe. 
     * Nessun programmatore potrà mai fare 'new Money(-50, "EUR")' da altre parti dell'applicazione.
     */
    private Money(long amountMinor, String currency) {
        this.amountMinor = amountMinor;
        this.currency = currency;
    }

    /**
     * STATIC FACTORY METHOD (Metodo di Fabbrica Statico).
     * È il vero e unico punto di ingresso per creare questo oggetto. Funge da "buttafuori"
     * controllando che i dati inseriti rispettino le regole di sicurezza (Invarianti).
     *
     * @param amountMinor L'importo in unità minori (es. centesimi)
     * @param currency    La stringa della valuta (es. "EUR")
     * @return Un oggetto Money sicuro e validato
     */
    public static Money of(long amountMinor, String currency) {
        
        // CONTROLLO DI SICUREZZA 1: La valuta non deve essere null.
        // Se viene passato null, il programma si blocca immediatamente lanciando una NullPointerException.
        Objects.requireNonNull(currency, "currency must not be null");
        
        // CONTROLLO DI SICUREZZA 2: La valuta non deve essere vuota o contenere solo spazi (es. "" o "   ").
        if (currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
        
        // CONTROLLO DI SICUREZZA 3: L'importo non può essere inferiore a zero.
        // Nel modello di un Wallet, un oggetto "Denaro" base non può nascere con un valore negativo.
        if (amountMinor < 0) {
            throw new IllegalArgumentException("amountMinor must be >= 0");
        }

        // Se tutti i controlli sopra hanno esito positivo, viene invocato il costruttore 
        // privato e viene restituita la nuova istanza di Money.
        return new Money(amountMinor, currency);
    }

    /**
     * METODO GETTER (Stile Record moderno).
     * Restituisce il valore numerico dell'importo in centesimi.
     * Non usa il vecchio prefisso "get" (es. getAmountMinor) per allinearsi alla sintassi pulita dei Record di Java.
     */
    public long amountMinor() {
        return amountMinor;
    }

    /**
     * METODO GETTER (Stile Record moderno).
     * Restituisce la stringa della valuta associata.
     */
    public String currency() {
        return currency;
    }

    /**
     * OPERAZIONE DI ADDIZIONE SICURA.
     * Permette di sommare un altro oggetto Money a quello attuale.
     *
     * @param other L'altro oggetto Money da sommare.
     * @return Un NUOVO oggetto Money che rappresenta il risultato della somma.
     */
    public Money add(Money other) {
        // Controllo preventivo: non possiamo sommare un oggetto che non esiste (null).
        Objects.requireNonNull(other, "other must not be null");
        
        // Applica l'invariante: blocca l'operazione se le valute non coincidono.
        requireSameCurrency(other);
        
        // PROTEZIONE DA OVERFLOW SILENZIOSO:
        // Math.addExact esegue la somma tra interi ma, se il numero supera la capacità massima del 'long',
        // non sballa trasformandosi in un numero negativo, ma lancia immediatamente una 'ArithmeticException'.
        long sum = Math.addExact(this.amountMinor, other.amountMinor);
        
        // Sfrutta la factory 'of' per creare e restituire il nuovo oggetto Money con il risultato.
        return Money.of(sum, this.currency);
    }

    /**
     * METODO DI SUPPORTO PRIVATO (Helper Method).
     * Centralizza il controllo della valuta per evitare duplicazioni di codice sia in 'add' che in 'subtract'.
     */
    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            // Genera l'eccezione esplicita "currency mismatch" cercata dai test di sicurezza.
            throw new IllegalArgumentException("currency mismatch: " + this.currency + " vs " + other.currency);
        }
    }

    /**
     * OPERAZIONE DI SOTTRAZIONE SICURA.
     * Permette di sottrarre un importo (es. pagamento o prelievo) salvaguardando il bilancio.
     *
     * @param other L'importo da sottrarre.
     * @return Un NUOVO oggetto Money che rappresenta la differenza rimasta.
     */
    public Money subtract(Money other) {
        // Controllo preventivo: blocca i valori null in ingresso.
        Objects.requireNonNull(other, "other must not be null");
        
        // Applica l'invariante: non puoi sottrarre dollari da un conto in euro.
        requireSameCurrency(other);
        
        // Math.subtractExact esegue la sottrazione intercettando eventuali overflow matematici estremi.
        long difference = Math.subtractExact(this.amountMinor, other.amountMinor);
        
        // PROTEZIONE DA CONTO IN ROSSO:
        // Se il risultato scende sotto zero, significa che l'utente sta provando a spendere più di quanto possiede.
        if (difference < 0) {
            throw new IllegalArgumentException("insufficient funds");
        }
        
        // Restituisce il nuovo oggetto Money certificato.
        return Money.of(difference, this.currency);
    }

    /**
     * @Override dice al compilatore che stiamo riscrivendo il metodo standard ereditato da java.lang.Object.
     * Questo metodo definisce l'Uguaglianza Logica (basata sul valore) anziché l'uguaglianza fisica (in memoria).
     */
    @Override 
    public boolean equals(Object o) {
        // PASSO 1: Controllo di identità fisica.
        // Se i due oggetti puntano alla stessa identica cella di memoria, sono sicuramente uguali.
        if (this == o) {
            return true;
        }
        
        // PASSO 2: Controllo del tipo (Usa il Pattern Matching di Java moderno).
        // Se l'oggetto 'o' è nullo OPPURE non è un'istanza della classe Money, restituisce false.
        // Se invece È un'istanza di Money, Java crea automaticamente una variabile convertita chiamata 'money'.
        if (!(o instanceof Money money)) {
            return false;
        }
        
        // PASSO 3: Confronto dei singoli attributi interni.
        // Due oggetti Money sono logicamente uguali solo se hanno lo stesso valore numerico (== su tipi primitivi)
        // E la stessa identica stringa di valuta (.equals() sugli oggetti String).
        return amountMinor == money.amountMinor && currency.equals(money.currency);
    }

    /**
     * Calcola il codice hash (impronta digitale numerica) basandosi esclusivamente sui dati dell'oggetto.
     * Questo metodo DEVE essere riscritto insieme a equals per non rompere le HashMap e gli HashSet.
     */
    @Override 
    public int hashCode() {
        // Objects.hash prende i campi selezionati e applica un algoritmo matematico interno
        // per generare un numero intero. Se i campi sono identici, il numero generato sarà identico.
        return Objects.hash(amountMinor, currency);
    }

    /**
     * Converte l'oggetto in una stringa leggibile dagli esseri umani.
     * Evita la stampa del codice di memoria standard (es. Money@3a4f21) facilitando i log e il debug.
     */
    @Override
    public String toString() {
        // Unisce l'importo e la valuta separati da uno spazio (Es: "1550 EUR")
        return amountMinor + " " + currency;
    }
}
