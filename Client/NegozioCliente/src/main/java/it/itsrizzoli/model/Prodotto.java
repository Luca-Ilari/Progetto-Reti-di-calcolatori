package it.itsrizzoli.model;

public class Prodotto {
    private int idProdotto;
    private String nome;
    private double prezzo;
    private int quantitaDisponibile;

    public Prodotto(int idProdotto, String nome, double prezzo, int quantitaDisponibile) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public Prodotto(String nome, double prezzo, int quantitaDisponibile) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    @Override
    public String toString() {
        return "Prodotto{" + "idProdotto=" + idProdotto + ", nome='" + nome + '\'' + ", prezzo=" + prezzo + ", " +
                "quantitaDisponibile=" + quantitaDisponibile + '}';
    }
}
