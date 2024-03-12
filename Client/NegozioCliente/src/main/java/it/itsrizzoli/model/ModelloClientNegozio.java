package it.itsrizzoli.model;

import java.util.ArrayList;
import java.util.List;

public class ModelloClientNegozio {
    private boolean statoNegozioOnline = false;

    private List<Prodotto> prodottiCarrello;
    private List<Prodotto> prodottiNegozio;
    private List<Transazione> listaTransazione;


    public ModelloClientNegozio() {
        prodottiNegozio = new ArrayList<>();
        prodottiCarrello = new ArrayList<>();
        listaTransazione = new ArrayList<>();
    }

    public boolean isStatoNegozioOnline() {
        return statoNegozioOnline;
    }

    public void setStatoNegozioOnline(boolean statoNegozioOnline) {
        this.statoNegozioOnline = statoNegozioOnline;
    }

    public List<Prodotto> getProdottiCarrello() {
        return prodottiCarrello;
    }

    public void setProdottiCarrello(List<Prodotto> prodottiCarrello) {
        this.prodottiCarrello = prodottiCarrello;
    }

    public List<Prodotto> getProdottiNegozio() {
        return prodottiNegozio;
    }

    public void setProdottiNegozio(List<Prodotto> prodottiNegozio) {
        this.prodottiNegozio = prodottiNegozio;
    }

    public List<Transazione> getListaTransazione() {
        return listaTransazione;
    }

    public void setListaTransazione(List<Transazione> listaTransazione) {
        this.listaTransazione = listaTransazione;
    }

    public void aggiungiProdottoNegozio(Prodotto prodotto) {
        prodottiNegozio.add(prodotto);
    }

    public void rimuoviProdottoNegozio(Prodotto prodotto) {
        prodottiNegozio.remove(prodotto);
    }

    public void aggiungiProdottoCarrello(Prodotto prodotto) {
        prodottiCarrello.add(prodotto);
    }

    public void rimuoviProdottoCarrello(Prodotto prodotto) {
        prodottiCarrello.remove(prodotto);
    }

    public void aggiungiTransazione(Transazione transazione) {
        listaTransazione.add(transazione);
    }

    public void aggiungiListaTransazione(List<Transazione> listaTransazione) {
        this.listaTransazione.addAll(listaTransazione);
    }

    public void rimuoviTransazione(Transazione transazione) {
        listaTransazione.remove(transazione);
    }

    private Prodotto trovaProdottoLista(int idProdotto, List<Prodotto> prodotti) {
        for (Prodotto prodotto : prodotti) {
            if (prodotto.getIdProdotto() == idProdotto) {
                return prodotto;
            }
        }
        return null;
    }

    public Transazione trovaTransazione(int idTransazione) {
        for (Transazione transazione : listaTransazione) {
            if (transazione.getIdTransazione() == idTransazione) {
                return transazione;
            }
        }
        return null;
    }


}
