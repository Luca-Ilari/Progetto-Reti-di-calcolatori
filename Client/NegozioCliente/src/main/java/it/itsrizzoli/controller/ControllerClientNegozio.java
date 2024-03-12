package it.itsrizzoli.controller;

import it.itsrizzoli.model.ModelloClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.tcpip.ThreadClient;
import it.itsrizzoli.view.ClientNegozioInterfaccia;

import java.util.List;

import static it.itsrizzoli.tools.TypeThread.THREAD_WRITE_TRANSAZIONI;

public class ControllerClientNegozio {
    private final ModelloClientNegozio modelloClientNegozio;
    private final ClientNegozioInterfaccia clientNegozioInterfaccia;
    private final ClientConnessione clientConnessione;

    public ClientConnessione getClientConnessione() {
        return clientConnessione;
    }

    public ControllerClientNegozio(ModelloClientNegozio modelloClientNegozio,
                                   ClientNegozioInterfaccia clientNegozioInterfaccia) {
        this.modelloClientNegozio = modelloClientNegozio;
        this.clientNegozioInterfaccia = clientNegozioInterfaccia;
        this.clientConnessione = new ClientConnessione(this);

    }

    public ClientNegozioInterfaccia getClientNegozioGui() {
        return clientNegozioInterfaccia;
    }

    public ModelloClientNegozio getModelloClientNegozio() {
        return modelloClientNegozio;
    }

    public void startInterfacciaClient() {
        clientNegozioInterfaccia.inizializza();
        clientNegozioInterfaccia.setVisible(true);
    }

    public synchronized void aggiornaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        modelloClientNegozio.setProdottiNegozio(newProdottiNegozio);
        clientNegozioInterfaccia.aggiornaTabellaProdottiNegozio(newProdottiNegozio);
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


    public List<Prodotto> getProdottiCarrello() {
        return modelloClientNegozio.getProdottiCarrello();
    }

    public List<Prodotto> getProdottiNegozio() {
        return modelloClientNegozio.getProdottiNegozio();
    }

    public List<Transazione> getListaTransazioni() {
        return modelloClientNegozio.getListaTransazione();
    }

    public void setProdottiCarrello(List<Prodotto> prodottiCarrello) {
        modelloClientNegozio.setProdottiCarrello(prodottiCarrello);
    }

    public void setProdottiNegozio(List<Prodotto> prodottiNegozio) {
        modelloClientNegozio.setProdottiNegozio(prodottiNegozio);
    }

    public void aggiungiListaTransazione(List<Transazione> listaTransazioni) {
        modelloClientNegozio.aggiungiListaTransazione(listaTransazioni);
    }

    public void addAllTransazioneAwait(List<Transazione> listaTransazione) {
        if (listaTransazione == null) {
            System.err.println(" - Errore: Lista transazione non trovata");
            return;
        }
        for (Transazione transazione : listaTransazione) {
            clientNegozioInterfaccia.addSingleTransazioneAwait(transazione, getProdottiNegozio());
        }
    }

    public void startThreadTransazioni() {
        ThreadClient threadWriting = new ThreadClient(THREAD_WRITE_TRANSAZIONI);
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
