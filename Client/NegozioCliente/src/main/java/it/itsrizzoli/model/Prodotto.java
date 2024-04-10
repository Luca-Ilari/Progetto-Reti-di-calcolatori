package it.itsrizzoli.model;

public class Prodotto {
    private int id;
    private String nome;
    private double prezzo;
    private int quantitaDisponibile;

    public Prodotto(int id, String nome, double prezzo, int quantitaDisponibile) {
        this.id = id;
        this.nome = nome;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public Prodotto(String nome, double prezzo, int quantitaDisponibile) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "Prodotto{" + "idProdotto=" + id + ", nome='" + nome + '\'' + ", prezzo=" + prezzo + ", " +
                "quantitaDisponibile=" + quantitaDisponibile + '}';
    }
}
