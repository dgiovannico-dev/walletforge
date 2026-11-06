// Appartiene al pacchetto degli adapter d'ingresso Web.
// Riceve le richieste HTTP esterne e le traduce in comandi comprensibili dall'applicazione.
package com.walletforge.adapter.in.web;

// Importa le annotazioni di Spring Web per la gestione delle rotte e dei metodi HTTP
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController: Dice a Spring che questa classe è un componente Web (un Bean).
 * Ogni metodo qui dentro restituirà direttamente dati (JSON) nel corpo della risposta HTTP,
 * anziché cercare file o template grafici.
 * 
 * @RequestMapping("/api/v1"): Imposta il prefisso dell'URL di base per TUTTI gli endpoint 
 * contenuti in questa classe. Ogni rotta partirà quindi da "/api/v1".
 */
@RestController 
@RequestMapping("/api/v1")
public class MetaController {
    
    /**
     * @GetMapping("/meta"): Dice a Spring che questo metodo risponde solo alle richieste HTTP di tipo "GET".
     * L'URL completo per attivare questo metodo si ottiene unendo il prefisso della classe con questo valore:
     * Risultato = "/api/v1" + "/meta" = "/api/v1/meta"
     */
    @GetMapping("/meta")
    public MetaResponse meta() {
        
        // Fabbrica e restituisce una nuova istanza del record di risposta inserendo i metadati del wallet.
        // Spring Boot prenderà questo oggetto e lo convertirà automaticamente nel JSON:
        // { "service": "wallet-api", "version": "0.0.1-SNAPSHOT" }
        return new MetaResponse("wallet-api", "0.0.1-SNAPSHOT");
    }
}
