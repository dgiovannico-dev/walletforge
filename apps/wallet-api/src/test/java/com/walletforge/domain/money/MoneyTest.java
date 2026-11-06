// Il pacchetto deve essere identico a quello della classe da testare (Money).
// Questo permette al test di risiedere idealmente nello stesso spazio concettuale della classe.
package com.walletforge.domain.money;

// Importazioni statiche dei metodi di asserzione della libreria JUnit 5.
// L'importazione statica è una best practice: rende il codice più leggibile, permettendo di scrivere 
// direttamente i metodi di verifica senza dover digitare ogni volta 'Assertions.nomeMetodo(...)'.
import static org.junit.jupiter.api.Assertions.assertEquals;    // Verifica che due valori o oggetti siano UGUALI
import static org.junit.jupiter.api.Assertions.assertNotEquals; // Verifica che due valori o oggetti siano DIVERSI
import static org.junit.jupiter.api.Assertions.assertNotNull;    // Verifica che un oggetto NON sia nullo
import static org.junit.jupiter.api.Assertions.assertThrows;   // Verifica che venga lanciato un errore specifico
import static org.junit.jupiter.api.Assertions.assertTrue;     // Verifica che una condizione booleana sia vera

// Importa l'annotazione @Test di JUnit 5, fondamentale per contrassegnare i metodi come test eseguibili.
import org.junit.jupiter.api.Test;

/**
 * Classe di test unitari completa per la classe Money.
 * Non ha bisogno di essere definita 'public' (basta la visibilità di pacchetto) perché viene 
 * eseguita internamente dai plugin del framework di test (come Maven Surefire o l'IDE).
 */
class MoneyTest {

    /**
     * TEST 1: VERIFICA DI BASE (HAPPY PATH)
     * Controlla che i dati passati al costruttore/factory siano esposti correttamente all'esterno.
     */
    @Test 
    void shouldExposeAmountAndCurrency() {
        // --- ARRANGE & ACT (Preparazione e Azione) ---
        // Fabbbrichiamo un oggetto Money da 100 unità minori (1.00 EUR). La 'L' specifica il tipo 'long'.
        Money money = Money.of(100L, "EUR");

        // --- ASSERT (Verifica dei risultati) ---
        // Verifichiamo che i metodi di lettura (getter) restituiscano esattamente ciò che abbiamo inserito.
        assertEquals(100L, money.amountMinor());
        assertEquals("EUR", money.currency());
    }

    /**
     * TEST 2: ADDIZIONE CORRETTA (HAPPY PATH)
     * Verifica che la somma tra due importi della STESSA valuta funzioni matematicamente.
     */
    @Test
    void shouldAddAmountsWhenCurrencyMatches() {
        // --- ARRANGE (Preparazione) ---
        // Creiamo i due addendi. Entrambi hanno rigorosamente la valuta "EUR".
        Money left = Money.of(100L, "EUR");  // 1.00 EUR
        Money right = Money.of(50L, "EUR");  // 0.50 EUR

        // --- ACT (Azione) ---
        // Eseguiamo la somma. Ricorda che Money è immutabile: 'left' non cambia, viene creato un nuovo oggetto 'sum'.
        Money sum = left.add(right);

        // --- ASSERT (Verifica) ---
        // Ci aspettiamo che il nuovo oggetto contenga la somma corretta (150L) e mantenga la valuta "EUR".
        assertEquals(150L, sum.amountMinor());
        assertEquals("EUR", sum.currency());
    }

    /**
     * TEST 3: BLOCCO DI SICUREZZA ADDIZIONE (EDGE CASE / INVARIANTE)
     * Verifica che il sistema BLOCCHI il tentativo di sommare valute diverse (es. Euro + Dollari).
     */
    @Test
    void shouldRejectAddWhenCurrencyDoesNotMatch() {
        // --- ARRANGE (Preparazione) ---
        Money eur = Money.of(100L, "EUR");
        Money usd = Money.of(100L, "USD");

        // --- ACT & ASSERT (Azione e Verifica combinate) ---
        // 'assertThrows' si aspetta che l'azione eseguita dentro la funzione lambda '() -> ...' 
        // lanci tassativamente un'eccezione di tipo 'IllegalArgumentException'.
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> eur.add(usd) // Questa riga DEVE generare un errore.
        );

        // --- VERIFICA DEL MESSAGGIO DI ERRORE ---
        // Verifichiamo che il testo dell'errore contenga la spiegazione corretta per aiutare il debug.
        assertTrue(ex.getMessage().contains("currency mismatch"));
    }

    /**
     * TEST 4: SOTTRAZIONE CORRETTA (HAPPY PATH)
     * Verifica che il calcolo della sottrazione tra due importi della stessa valuta sia preciso.
     */
    @Test
    void shouldSubtractAmountsWhenCurrencyMatches() {
        // --- ARRANGE (Preparazione) ---
        Money left = Money.of(100L, "EUR");  // 1.00 EUR
        Money right = Money.of(40L, "EUR");  // 0.40 EUR

        // --- ACT (Azione) ---
        // Eseguiamo la sottrazione. Viene generato un nuovo oggetto 'result'.
        Money result = left.subtract(right);

        // --- ASSERT (Verifica) ---
        // Il bilancio finale deve essere esattamente di 60 centesimi (60L) in "EUR".
        assertEquals(60L, result.amountMinor());
        assertEquals("EUR", result.currency());
    }

    /**
     * TEST 5: BLOCCO DI SICUREZZA SOTTRAZIONE (EDGE CASE / INVARIANTE)
     * Verifica che il sistema BLOCCHI una sottrazione se il risultato finale dovesse scendere sotto lo zero.
     */
    @Test
    void shouldRejectSubtractWhenResultWouldBeNegative() {
        // --- ARRANGE (Preparazione) ---
        Money left = Money.of(30L, "EUR");  // Hai 0.30 EUR nel portafoglio
        Money right = Money.of(50L, "EUR"); // Provi a spenderne 0.50 EUR

        // --- ACT & ASSERT (Azione e Verifica combinate) ---
        // Verifichiamo che il sistema sollevi uno scudo di protezione impedendo al wallet di andare in rosso.
        assertThrows(IllegalArgumentException.class, () -> left.subtract(right));
    }

    /**
     * TEST 6: CONTRATTO DI UGUAGLIANZA LOGICA (EQUALS E HASHCODE - SUCCESSO)
     * Verifica che due oggetti Money separati in memoria, ma con gli stessi identici dati interni,
     * vengano considerati uguali e producano lo stesso identico codice hash.
     */
    @Test
    void shouldBeEqualWhenAmountAndCurrencyMatch() {
        // --- ARRANGE (Preparazione) ---
        // Creiamo due istanze distinte in memoria ma con lo stesso identico valore logico.
        Money a = Money.of(100L, "EUR");
        Money b = Money.of(100L, "EUR");

        // --- ASSERT (Verifica del contratto) ---
        // 1. Verifica equals(): l'oggetto 'a' deve essere logicamente uguale a 'b' (chiamata interna ad a.equals(b)).
        assertEquals(a, b);
        
        // 2. Verifica hashCode(): la regola d'oro di Java impone che se due oggetti sono uguali,
        // anche la loro impronta digitale numerica (hashCode) deve essere identica per non rompere le HashMap.
        assertEquals(a.hashCode(), b.hashCode());
    }

    /**
     * TEST 7: CONTRATTO DI UGUAGLIANZA LOGICA (EQUALS - CASI DI DISUGUAGLIANZA)
     * Verifica che il metodo equals() sappia distinguere correttamente gli oggetti quando cambiano
     * l'importo, la valuta, oppure se viene confrontato con un oggetto completamente diverso o null.
     */
    @Test
    void shouldNotBeEqualWhenDataDoesNotMatch() {
        // --- ARRANGE (Preparazione) ---
        Money base = Money.of(100L, "EUR");
        Money importoDiverso = Money.of(200L, "EUR");
        Money valutaDiversa = Money.of(100L, "USD");

        // --- ASSERT (Verifica delle disuguaglianze) ---
        // 1. Fallisce se l'importo è differente
        assertNotEquals(base, importoDiverso);

        // 2. Fallisce se la valuta è differente
        assertNotEquals(base, valutaDiversa);

        // 3. Fallisce se confrontato con un valore 'null' (il codice deve gestire il null in sicurezza senza crashare)
        assertNotEquals(null, base);

        // 4. Fallisce se confrontato con un oggetto di un tipo completamente diverso (es. una stringa)
        assertNotEquals("100 EUR", base);
    }

    /**
     * TEST 8: VERIFICA DELLA RAPPRESENTAZIONE TESTUALE (TOSTRING)
     * Assicura che il metodo toString() restituisca una stringa formattata secondo i requisiti del team,
     * garantendo log puliti e leggibili sul server in caso di tracciamento operazioni.
     */
    @Test
    void shouldProvideCleanStringRepresentation() {
        // --- ARRANGE (Preparazione) ---
        Money money = Money.of(2550L, "USD");

        // --- ACT (Azione) ---
        String testoRisultante = money.toString();

        // --- ASSERT (Verifica) ---
        // Verifichiamo che la stringa prodotta coincida esattamente con il formato concordato: "valore valuta"
        assertNotNull(testoRisultante, "Il metodo toString() non dovrebbe mai restituire null");
        assertEquals("2550 USD", testoRisultante);
    }
}
