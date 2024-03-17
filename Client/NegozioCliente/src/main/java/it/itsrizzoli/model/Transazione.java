package it.itsrizzoli.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Transazione {
    private final int idTransazione;
    private int idProdotto;
    private int quantita;
    private static int contaTransazione = 0;

    public Transazione(int idProdotto, int quantita) {
        contaTransazione = contaTransazione + 1;

        this.idTransazione = contaTransazione;
        this.idProdotto = idProdotto;
        this.quantita = quantita;

    }

    public int getIdTransazione() {
        return idTransazione;
    }

    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }


    @Override
    public String toString() {
        return "Transazione{" + "idTransazione=" + idTransazione + ", idProdotto=" + idProdotto + ", quantita=" + quantita + '}';
    }

    public static Transazione createTransaction(int idProdotto) {
        int randomQuantita = new Random().nextInt(101, 201); // Quantità casuale tra 101 e 200
        return new Transazione(idProdotto, randomQuantita);
    }

    public static Transazione createTransaction(int idProdotto, int quantitaDisponibile) {
        int randomQuantita = new Random().nextInt(0, quantitaDisponibile); // Quantità casuale tra 101 e 200
        return new Transazione(idProdotto, randomQuantita);
    }

    public static List<Transazione> creaListaTransazioniRandom(List<Prodotto> listaProdotti, boolean isRandomQuantita) {
        List<Transazione> listaRandomTransazioni = new ArrayList<>();

        if (listaProdotti.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int numeroTransazioni = random.nextInt(10, 50);

        for (int i = 0; i < numeroTransazioni; i++) {
            int idProdotto = random.nextInt(0, listaProdotti.size());
            int quantitaDisponibile = listaProdotti.get(idProdotto).getQuantitaDisponibile();

            listaRandomTransazioni.add(isRandomQuantita ? createTransaction(idProdotto) :
                    createTransaction(idProdotto, quantitaDisponibile));
        }

        return listaRandomTransazioni;
    }
}
