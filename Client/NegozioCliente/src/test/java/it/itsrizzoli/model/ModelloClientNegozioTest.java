package it.itsrizzoli.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

class ModelloClientNegozioTest {

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
        var transazione = modelTest.trovaTransazione(23_003);
        assertNull(transazione);
    }

    @Test
    void testTrovaTransazione2() {
        var modelTest = new ModelloClientNegozio();
        var transazione = modelTest.trovaTransazione(-21_213);
        assertNull(transazione);
    }

    @Test
    void testTrovaTransazione3() {
        var modelTest = new ModelloClientNegozio();
        var transaction = new Transazione(1, 1);
        modelTest.aggiungiTransazioneAcquisto(transaction);
        var foundTransaction = modelTest.trovaTransazione(Transazione.contaTransazione);
        assertSame(foundTransaction, transaction);
        assertEquals(foundTransaction, transaction);
    }

    @Test
    public void testTrovaProdotto() {
        // Creiamo una lista di prodotti di esempio
        List<Prodotto> listaProdotti = new ArrayList<>();
        listaProdotti.add(new Prodotto(1, "Prodotto1", 10.99, 3));
        listaProdotti.add(new Prodotto(2, "Prodotto2", 20.50, 5));
        listaProdotti.add(new Prodotto(3, "Prodotto3", 15.75, 2));

        ModelloClientNegozio modelloClientNegozio = new ModelloClientNegozio();

        Prodotto prodottoTrovato = modelloClientNegozio.trovaProdotto("Prodotto2", listaProdotti);
        assertNotNull(prodottoTrovato, "Il prodotto è stato trovato");
        assertEquals("Prodotto2", prodottoTrovato.getNome());

        Prodotto prodottoNonTrovato = modelloClientNegozio.trovaProdotto("ProdottoNonEsistente", listaProdotti);
        assertNull(prodottoNonTrovato, "Il prodotto non esiste, ma è stato trovato");
    }


    @Test
    public void testAzzeraDati() {
        ModelloClientNegozio modelloClientNegozio = new ModelloClientNegozio();

        // Aggiungere dati di esempio alle liste
        modelloClientNegozio.getProdottiCarrello().add(new Prodotto(1, "Prodotto1", 10.99, 3));
        modelloClientNegozio.getProdottiNegozio().add(new Prodotto(2, "Prodotto2", 20.50, 5));
        modelloClientNegozio.getListaTransazioneVendita().add(new Transazione(1, 2));
        modelloClientNegozio.getListaTransazioneAcquisto().add(new Transazione(2, 3));

        // Eseguire il metodo azzeraDati
        modelloClientNegozio.azzeraDati();

        // Verificare che tutte le liste siano vuote dopo l'azzeraDati
        assertTrue(modelloClientNegozio.getProdottiCarrello().isEmpty());
        assertTrue(modelloClientNegozio.getProdottiNegozio().isEmpty());
        assertTrue(modelloClientNegozio.getListaTransazioneVendita().isEmpty());
        assertTrue(modelloClientNegozio.getListaTransazioneAcquisto().isEmpty());
    }

}