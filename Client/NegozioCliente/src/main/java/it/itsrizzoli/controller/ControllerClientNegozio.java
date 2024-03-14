package it.itsrizzoli.controller;

import it.itsrizzoli.model.ModelloClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.tcpip.ThreadClient;
import it.itsrizzoli.view.ClientNegozioInterfaccia;

import java.util.List;

import static it.itsrizzoli.tools.TypeThread.THREAD_COMPRA_PRODOTTI;
import static it.itsrizzoli.tools.TypeThread.THREAD_VENDI_PRODOTTI;

public class ControllerClientNegozio {
    private final ModelloClientNegozio modelloClientNegozio;
    private final ClientNegozioInterfaccia clientNegozioInterfaccia;
    private final ClientConnessione clientConnessione;

    public ClientConnessione getClientConnessione() {
        return clientConnessione;
    }

    public ControllerClientNegozio(ModelloClientNegozio modelloClientNegozio,
                                   ClientNegozioInterfaccia clientNegozioInterfaccia,
                                   ClientConnessione clientConnessione) {
        this.modelloClientNegozio = modelloClientNegozio;
        this.clientNegozioInterfaccia = clientNegozioInterfaccia;
        this.clientConnessione = clientConnessione;

        clientConnessione.setControllerClientNegozio(this);
        clientNegozioInterfaccia.setControllerClientNegozio(this);

        clientConnessione.startConnessione();
    }

    public ClientNegozioInterfaccia getClientNegozioGui() {
        return clientNegozioInterfaccia;
    }

    public ModelloClientNegozio getModelloClientNegozio() {
        return modelloClientNegozio;
    }

    public List<Prodotto> getProdottiCarrello() {
        return modelloClientNegozio.getProdottiCarrello();
    }

    public List<Prodotto> getProdottiNegozio() {
        return modelloClientNegozio.getProdottiNegozio();
    }

    public List<Transazione> getListaTransazioni() {
        return modelloClientNegozio.getListaTransazioneAcquisto();
    }

    public void setProdottiCarrello(List<Prodotto> prodottiCarrello) {
        modelloClientNegozio.setProdottiCarrello(prodottiCarrello);
    }

    public void setProdottiNegozio(List<Prodotto> prodottiNegozio) {
        modelloClientNegozio.setProdottiNegozio(prodottiNegozio);
    }

    public synchronized void aggiornaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        modelloClientNegozio.setProdottiNegozio(newProdottiNegozio);
        clientNegozioInterfaccia.aggiornaTabellaProdottiNegozio(newProdottiNegozio);
    }

    // Intuile
    public synchronized void aggiornaProdottiCarrello(int idTransazione) {
        Transazione transazione = modelloClientNegozio.trovaTransazione(idTransazione);
        if (transazione == null) {
            System.err.println(" - Errore: Transazione non trovata");
            return;
        }
        Prodotto prodottoEdit = null;
        for (Prodotto prodotto : modelloClientNegozio.getProdottiCarrello()) {
            if (prodotto.getIdProdotto() == transazione.getIdProdotto()) {
                int quantitaRimasta = prodotto.getQuantitaDisponibile() - transazione.getQuantita();
                if (quantitaRimasta < 0) {
                    System.out.println(" ATTENZIONE: Prodotto finito con ID " + prodotto.getIdProdotto());
                    break;
                }
                prodotto.setQuantitaDisponibile(quantitaRimasta);
                prodottoEdit = prodotto;
            }
        }

        clientNegozioInterfaccia.aggiornaQuantitaCarrello(prodottoEdit.getIdProdotto(),
                prodottoEdit.getQuantitaDisponibile(), getProdottiNegozio(), getProdottiCarrello(), false);

    }

    public void aggiornaStatoConnessione(boolean statoConnessione) {
        this.clientNegozioInterfaccia.aggiornaStatoNegozio(statoConnessione);
    }

    public void aggiornaStateTransazioneId(int idTransazione) {
        Transazione transazione = modelloClientNegozio.trovaTransazione(idTransazione);
        if (transazione == null) {
            System.err.println(" - Errore: Transazione non trovata");
            return;
        }

        clientNegozioInterfaccia.aggiornaStatoTransazioneInTabella(transazione, getProdottiNegozio(),
                getProdottiCarrello());
        System.out.println(" --> UI: Lista transazione aggiornato!!");


    }

    public void aggiornaStateTransazioneFail(int idTransazione) {
        Transazione transazione = modelloClientNegozio.trovaTransazione(idTransazione);

        if (transazione == null) {
            System.err.println(" - Errore: Transazione non trovata");
            return;
        }
        clientNegozioInterfaccia.aggiornaStateTransazioneFail(transazione);

    }

    public void aggiungiListaTransazioneAcquisto(List<Transazione> listaTransazioni) {
        modelloClientNegozio.aggiungiListaTransazioneAcquisto(listaTransazioni);
    }

    public void aggiungiListaTransazioneVendita(List<Transazione> listaTransazioni) {
        modelloClientNegozio.aggiungiListaTransazioneVendita(listaTransazioni);
    }

    public void addAllTransazioneAwait(List<Transazione> listaTransazione) {
        if (listaTransazione == null) {
            System.err.println(" - Errore: Lista transazione non trovata");
            return;
        }
        clientNegozioInterfaccia.addAllTransazioneAwait(listaTransazione, getProdottiNegozio());
    }

    public void addAllTransazioneVenditaAwait(List<Transazione> listaTransazione) {
        if (listaTransazione == null) {
            System.err.println(" - Errore: Lista transazione non trovata");
            return;
        }
        clientNegozioInterfaccia.addAllTransazioneVenditaAwait(listaTransazione, getProdottiCarrello());
    }

    public void startThreadCompraProdotti() {
        ThreadClient threadWriting = new ThreadClient(THREAD_COMPRA_PRODOTTI);
        threadWriting.start();
    }

    public void startThreadVendiProdotti() {
        ThreadClient threadWriting = new ThreadClient(THREAD_VENDI_PRODOTTI);
        threadWriting.start();
    }

    public void addSingleTransazioneAwait(Transazione transazione) {
        clientNegozioInterfaccia.addSingleTransazioneAwait(transazione, getProdottiNegozio());
    }

    public void aggiornaStatoTransazioneInTabella(Transazione transazione) {
        clientNegozioInterfaccia.aggiornaStatoTransazioneInTabella(transazione, getProdottiNegozio(),
                getProdottiCarrello());
    }
}
