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
        assert (tmp.getQuantita() > 0);
    }

    @Test
    void creaListaTransazioniRandom() {
        int q = 3;
        float prezzo[] = new float[]{2.2f, 24.2f, 5.99f};
        ArrayList<Prodotto> prodotti = new ArrayList<Prodotto>(3);
        for (int i = 0; i < q; i++) {
            prodotti.add(new Prodotto(i, "prod" + i, prezzo[i], 10)); // corretto "prod" + 1 a "prod" + i
        }

        List<Transazione> transazioni = Transazione.creaListaTransazioniRandom(prodotti, true);

        boolean idProdottoPresente = false;
        for (Transazione transazione : transazioni) {
            for (Prodotto prodotto : prodotti) {
                if (transazione.getIdProdotto() == prodotto.getIdProdotto()) {
                    idProdottoPresente = true;
                    break;
                }
            }
            if (!idProdottoPresente) {
                break;
            }
        }

        assert idProdottoPresente : "Nessun ID prodotto presente nelle transazioni";
    }

    @Test
    void creaListaTransazioneArrayListVuoto() {
        ArrayList<Prodotto> tmp = new ArrayList<Prodotto>(1);
        var transazioni = Transazione.creaListaTransazioniRandom(tmp,true);
        assert (transazioni == null);
    }
}