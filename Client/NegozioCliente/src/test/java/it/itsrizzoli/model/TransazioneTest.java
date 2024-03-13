package it.itsrizzoli.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransazioneTest {
    @Test
    void newTransaction() {
        var tmp = new Transazione(1, 1);
        assertInstanceOf(Transazione.class, tmp);
        assertEquals(1, tmp.getIdProdotto());
        assertEquals(1, tmp.getQuantita());
        assertInstanceOf(Transazione.class, tmp);
    }
    @Test
    void newTransactionNegativeNumber() {
        var tmp = new Transazione(-12, -1);
        assertInstanceOf(Transazione.class, tmp);
        assertEquals(-12, tmp.getIdProdotto());
        assertEquals(-1, tmp.getQuantita());
    }
    @Test
    void createTransacitonMethod() {
        var tmp = Transazione.createTransaction(2);
        assertInstanceOf(Transazione.class, tmp);
        assertEquals(2, tmp.getIdProdotto());
        assert(tmp.getQuantita() <= 20);
        assert(tmp.getQuantita() > 0);
    }
    @Test
    void creaListaTransazioniRandom(){
        int q = 3;
        float prezzo[]=  new float[]{2.2f,24.2f,5.99f};
        ArrayList<Prodotto> tmp = new ArrayList<Prodotto>(3);
        for (int i = 0; i <   q ; i++) {
            tmp.add(new Prodotto(i,"prod"+1, prezzo[i],10));
        }

        var transazioni = Transazione.creaListaTransazioniRandom(tmp);
        for (int i = 0; i < q; i++) {
            assertEquals(tmp.get(i).getNome(), "prod"+1);
            assertEquals(tmp.get(i).getPrezzo(), prezzo[i]);
            assertEquals(tmp.get(i).getQuantitaDisponibile(), 10);
        }
    }
    @Test
    void creaListaTransazioneArrayListVuoto(){
        ArrayList<Prodotto> tmp = new ArrayList<Prodotto>(1);
        var transazioni = Transazione.creaListaTransazioniRandom(tmp);
        assert(transazioni == null);
    }
}