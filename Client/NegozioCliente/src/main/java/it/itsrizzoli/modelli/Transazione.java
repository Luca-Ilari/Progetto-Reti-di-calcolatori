package it.itsrizzoli.modelli;

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

    private static Transazione createTransaction(Prodotto prodotto) {
        // Simuliamo la creazione di una transazione con un prodotto casuale e una quantità casuale
        int idProdotto = prodotto.getIdProdotto(); // Id del prodotto casuale
        int randomQuantita = (int) (Math.random() * 10) + 1; // Quantità casuale tra 1 e 10
        return new Transazione(idProdotto, randomQuantita);
    }
}
