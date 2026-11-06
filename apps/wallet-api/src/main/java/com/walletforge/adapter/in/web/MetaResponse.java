// Risiede nello stesso pacchetto web dell'adapter, poiché serve esclusivamente a questo livello per formattare l'uscita delle API.
package com.walletforge.adapter.in.web;

/**
 * Un 'record' è un tipo speciale di classe immutabile introdotto in Java.
 * Sostituisce i vecchi DTO pesanti pieni di codice ripetitivo.
 * 
 * Definendo semplicemente i parametri tra parentesi tonda (String service, String version), 
 * il compilatore Java genera AUTOMATICAMENTE per te dietro le quinte:
 * 1. I campi privati e finali (private final String service; private final String version;)
 * 2. Il costruttore pubblico completo per assegnare i dati.
 * 3. I metodi getter moderni senza prefisso "get" (service() e version()).
 * 4. I metodi equals(), hashCode() e toString() calibrati su questi due campi.
 */
public record MetaResponse(String service, String version) {
    // La classe è volutamente vuota. Non servono metodi o righe di codice aggiuntive!
}
