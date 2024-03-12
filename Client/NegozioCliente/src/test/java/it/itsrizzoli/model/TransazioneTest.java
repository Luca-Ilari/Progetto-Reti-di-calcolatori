package it.itsrizzoli.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

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
}