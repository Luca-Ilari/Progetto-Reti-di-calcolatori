package it.itsrizzoli.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

class ModelloClientNegozioTest {

    @Test
    void aggiungiListaTransazioneAcquisto() {

    }

    @Test
    void aggiungiListaTransazioneVendita() {
    }

    @Test
    void testAggiungiTransazioneVendita() {
        var tmp = new Transazione(1, 1);
        var modelTest = new ModelloClientNegozio();
        modelTest.aggiungiTransazioneVendita(tmp);
        assertEquals(modelTest.getListaTransazioneVendita().size(), 1);
    }
    @Test
    void testrimuoviTransazioneVendita() {
        var tmp = new Transazione(1, 1);
        var modelTest = new ModelloClientNegozio();
        modelTest.aggiungiTransazioneVendita(tmp);
        assertEquals(modelTest.getListaTransazioneVendita().size(), 1);
        modelTest.rimuoviTransazioneVendita(tmp);
        assertEquals(modelTest.getListaTransazioneVendita().size(), 0);

    }
    @Test
    void aggiungiTransazioneAcquisto() {
        var tmp = new Transazione(1, 1);
        var modelTest = new ModelloClientNegozio();
        modelTest.aggiungiTransazioneVendita(tmp);
        assertEquals(modelTest.getListaTransazioneVendita().size(), 1);
    }
    @Test
    void testRimuoviTransazioneAcquito() {
        var tmp = new Transazione(1, 1);
        var modelTest = new ModelloClientNegozio();
        modelTest.aggiungiTransazioneAcquisto(tmp);
        assertEquals(modelTest.getListaTransazioneAcquisto().size(), 1);
        modelTest.rimuoviTransazioneAcquisto(tmp);
        assertEquals(modelTest.getListaTransazioneAcquisto().size(), 0);
    }

    @Test
    void testTrovaTransazione1() {
        var modelTest = new ModelloClientNegozio();
        var transazione = modelTest.trovaTransazione(21);
        assertNull(transazione);
    }
    @Test
    void testTrovaTransazione2() {
        var modelTest = new ModelloClientNegozio();
        var transazione = modelTest.trovaTransazione(-21);
        assertNull(transazione);
    }
    @Test
    void testTrovaTransazione3() {
        var modelTest = new ModelloClientNegozio();
        var transaction = new Transazione(1,1);
        modelTest.aggiungiTransazioneAcquisto(transaction);
        var foundTransaction = modelTest.trovaTransazione(1);
        assertSame(foundTransaction, transaction);
        assertEquals(foundTransaction, transaction);
    }
    @Test
    void trovaProdotto() {

    }

    @Test
    void azzeraDatiNegozio() {

    }
}