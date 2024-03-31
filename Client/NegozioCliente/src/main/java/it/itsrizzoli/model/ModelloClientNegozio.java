package it.itsrizzoli.model;

import java.util.ArrayList;
import java.util.List;

public class ModelloClientNegozio {
    private List<Prodotto> prodottiCarrello = new ArrayList<>();
    private List<Prodotto> prodottiNegozio = new ArrayList<>();
    private List<Transazione> listaTransazioneAcquisto = new ArrayList<>();
    private List<Transazione> listaTransazioneVendita = new ArrayList<>();

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

    public void aggiungiTransazioneAcquisto(Transazione transazione) {
        this.listaTransazioneAcquisto.add(transazione);
    }

    public void aggiungiTransazioneVendita(Transazione transazione) {
        this.listaTransazioneVendita.add(transazione);
    }

    public void rimuoviTransazioneAcquisto(Transazione transazione) {
        listaTransazioneAcquisto.remove(transazione);
    }
    public void rimuoviTransazioneVendita(Transazione transazione) {
        listaTransazioneVendita.remove(transazione);
    }

    public void rimuoviProdottoCarrello(String nomeProdotto) {
        Prodotto prodotto = trovaProdotto(nomeProdotto, prodottiCarrello);
        prodottiCarrello.remove(prodotto);
    }

    public Transazione trovaTransazione(int idTransazione) {
        for (Transazione transazione : listaTransazioneAcquisto) {
            if (transazione.getIdTransazione() == idTransazione) {
                return transazione;
            }
        }
        for (Transazione transazione : listaTransazioneVendita) {
            if (transazione.getIdTransazione() == idTransazione) {
                return transazione;
            }
        }
        return null;
    }

    public Prodotto trovaProdotto(String nomeProdotto, List<Prodotto> lista) {
        for (Prodotto prodotto : lista) {
            if (prodotto.getNome().equals(nomeProdotto)) {
                return prodotto;
            }
        }
        return null;
    }

    public void azzeraDati() {
        svuotaProdottiCarrello();
        svuotaProdottiNegozio();
        svuotaListaTransazioneVendita();
        svuotaListaTransazioneAcquisto();
    }

    public void azzeraDatiNegozio() {
        svuotaProdottiNegozio();
    }

    private void svuotaProdottiCarrello() {
        prodottiCarrello.clear();
    }

    private void svuotaProdottiNegozio() {
        prodottiNegozio.clear();
    }

    private void svuotaListaTransazioneVendita() {
        listaTransazioneVendita.clear();
    }

    private void svuotaListaTransazioneAcquisto() {
        listaTransazioneAcquisto.clear();
    }

}
