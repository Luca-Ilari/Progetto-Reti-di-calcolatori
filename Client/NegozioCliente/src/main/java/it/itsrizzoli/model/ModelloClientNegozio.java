package it.itsrizzoli.model;

import java.util.ArrayList;
import java.util.List;

public class ModelloClientNegozio {
    private boolean statoNegozioOnline = false;

    private List<Prodotto> prodottiCarrello;
    private List<Prodotto> prodottiNegozio;
    private List<Transazione> listaTransazioneAcquisto;
    private List<Transazione> listaTransazioneVendita;


    public ModelloClientNegozio() {
        prodottiNegozio = new ArrayList<>();
        prodottiCarrello = new ArrayList<>();
        listaTransazioneAcquisto = new ArrayList<>();
        listaTransazioneVendita = new ArrayList<>();
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

    public List<Transazione> getListaTransazioneAcquisto() {
        return listaTransazioneAcquisto;
    }

    public void setListaTransazioneAcquisto(List<Transazione> listaTransazioneAcquisto) {
        this.listaTransazioneAcquisto = listaTransazioneAcquisto;
    }

    public List<Transazione> getListaTransazioneVendita() {
        return listaTransazioneVendita;
    }

    public void setListaTransazioneVendita(List<Transazione> listaTransazioneVendita) {
        this.listaTransazioneVendita = listaTransazioneVendita;
    }


    public void aggiungiListaTransazioneAcquisto(List<Transazione> listaTransazione) {
        this.listaTransazioneAcquisto.addAll(listaTransazione);
    }

    public void aggiungiListaTransazioneVendita(List<Transazione> listaTransazione) {
        this.listaTransazioneVendita.addAll(listaTransazione);
    }

    public void rimuoviTransazione(Transazione transazione) {
        listaTransazioneAcquisto.remove(transazione);
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
        for (Transazione transazione : listaTransazioneAcquisto) {
            if (transazione.getIdTransazione() == idTransazione) {
                return transazione;
            }
        }
        return null;
    }


}
