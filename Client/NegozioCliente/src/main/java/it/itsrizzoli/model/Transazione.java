package it.itsrizzoli.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Transazione {
    private int idTransazione;
    private int idProdotto;
    private int quantita;
    private static int contaTransazione = 0;

    public Transazione(int idProdotto, int quantita) {
        this.idTransazione = contaTransazione + 1;
        this.idProdotto = idProdotto;
        this.quantita = quantita;

        contaTransazione = contaTransazione + 1;
    }

    public int getIdTransazione() {
        return idTransazione;
    }

    public void setIdTransazione(int idTransazione) {
        this.idTransazione = idTransazione;
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
        int randomQuantita = (int) (Math.random() * 10) + 1; // Quantità casuale tra 1 e 10
        return new Transazione(idProdotto, randomQuantita);
    }

    public static List<Transazione> creaListaTransazioniRandom(List<Prodotto> listaProdotti) {
        List<Transazione> listaRandomTransazioni = new ArrayList<>();
        Random random = new Random();
        int numeroTransazioni = random.nextInt(5, 20);

        for (int i = 0; i < numeroTransazioni; i++) {
            int idProdotto = random.nextInt(0,listaProdotti.size());
            listaRandomTransazioni.add(createTransaction(idProdotto));
        }

        System.out.println(" --> listaRandomTransazioni.size() = " + listaRandomTransazioni.size());
        return listaRandomTransazioni;
    }
}
