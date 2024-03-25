package it.itsrizzoli.model;

import java.util.ArrayList;
import java.util.List;

public class ModelloClientNegozio {
    private boolean statoNegozioOnline = false;
    private List<Prodotto> prodottiCarrello = new ArrayList<>();
    private List<Prodotto> prodottiNegozio = new ArrayList<>();
    private List<Transazione> listaTransazioneAcquisto = new ArrayList<>();
    private List<Transazione> listaTransazioneVendita = new ArrayList<>();


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

    public void aggiungiTransazioneAcquisto(Transazione transazione) {
        this.listaTransazioneAcquisto.add(transazione);
    }

    public void aggiungiTransazioneVendita(Transazione transazione) {
        this.listaTransazioneVendita.add(transazione);
    }

    public void rimuoviTransazione(Transazione transazione) {
        listaTransazioneAcquisto.remove(transazione);
    }

    public void rimuoviProdottoCarrello(String nomeProdotto) {
        Prodotto prodotto = trovaProdotto(nomeProdotto, prodottiCarrello);
        prodottiCarrello.remove(prodotto);
    }

    private Prodotto trovaProdotto(int idProdotto, List<Prodotto> prodotti) {
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

    public int sommaQuantitaCarrello() {
        int somma = 0;

        for (Prodotto prodotto : prodottiCarrello) {
            somma += prodotto.getQuantitaDisponibile();
        }

        return somma;
    }


    public void azzeraDati() {
        prodottiCarrello.clear();
        prodottiNegozio.clear();
        listaTransazioneVendita.clear();
        listaTransazioneAcquisto.clear();
    }
}
