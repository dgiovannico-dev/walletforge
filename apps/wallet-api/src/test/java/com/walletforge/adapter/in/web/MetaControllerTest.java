// Il pacchetto riflette l'architettura esagonale/clean. 
// Si trova negli 'adapter in web': i componenti d'ingresso che ricevono le richieste dal mondo esterno (HTTP).
package com.walletforge.adapter.in.web;

// Importa la classe principale dell'applicazione Spring Boot per dire al test da dove partire per caricare il contesto.
import com.walletforge.WalletApiApplication;

// Importa le annotazioni e i componenti principali di JUnit 5 e Spring Boot Test
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

// Importazioni statiche fondamentali per costruire le richieste HTTP simulate (GET, POST, ecc.)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// Importazioni statiche fondamentali per effettuare le verifiche (asserzioni) sulle risposte HTTP ricevute
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @SpringBootTest: Dice a Spring di avviare l'intero contesto dell'applicazione. 
 * Passando 'classes = WalletApiApplication.class', specifichiamo l'esatta classe di partenza da scansionare.
 * 
 * @AutoConfigureMockMvc: Configura e istanzia automaticamente l'oggetto MockMvc, mettendolo a disposizione del test.
 */
@SpringBootTest(classes = WalletApiApplication.class)
@AutoConfigureMockMvc
class MetaControllerTest {

    /**
     * @Autowired: Dice a Spring di fare "Dependency Injection". 
     * Prende l'istanza di MockMvc configurata dall'annotazione sulla classe e la inserisce in questa variabile.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Il metodo lancia 'Exception' perché la simulazione delle chiamate di rete con MockMvc 
     * potrebbe generare errori di IO o di parsing che JUnit deve intercettare in caso di fallimento.
     */
    @Test
    void shouldReturnServiceMeta() throws Exception {
        
        // --- ACT & ASSERT (Azione e Verifica fluide concatenate tramite il pattern Fluent Builder) ---
        
        // 1. .perform(): Dice a MockMvc di eseguire un'azione simulata.
        // 2. get("/api/v1/meta"): Configura una richiesta HTTP di tipo GET verso quell'URL specifico.
        // 3. .accept(MediaType.APPLICATION_JSON): Dice al server che il test si aspetta una risposta in formato JSON.
        mockMvc.perform(get("/api/v1/meta").accept(MediaType.APPLICATION_JSON))
            
            // 4. .andExpect(): Inizia la fase di verifica dei risultati sulla risposta HTTP ricevuta.
            // Controlla che il server risponda con uno Status Code 200 OK.
            .andExpect(status().isOk())
            
            // 5. jsonPath("$.service"): Entra nella radice ($) del JSON e cerca la chiave "service".
            // .value("wallet-api"): Verifica che il valore associato a quella chiave sia esattamente "wallet-api".
            .andExpect(jsonPath("$.service").value("wallet-api"))
            
            // 6. Cerca la chiave "version" nella radice del JSON e verifica che corrisponda alla versione snapshot.
            .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"));
    }
}
