package it.itsrizzoli.controller;

import it.itsrizzoli.model.ModelloClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.view.ClientNegozioInterfaccia;

import java.util.List;

public class ControllerClientNegozio {
    private final ModelloClientNegozio modelloClientNegozio;
    private ClientNegozioInterfaccia clientNegozioInterfaccia;
    private ClientConnessione clientConnessione;

    public ClientConnessione getClientConnessione() {
        return clientConnessione;
    }

    public void setClientConnessione(ClientConnessione clientConnessione) {
        this.clientConnessione = clientConnessione;
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


    public void setClientNegozioInterfaccia(ClientNegozioInterfaccia clientNegozioInterfaccia) {
        this.clientNegozioInterfaccia = clientNegozioInterfaccia;
        this.clientNegozioInterfaccia.setControllerClientNegozio(this);

    }



    public ClientNegozioInterfaccia getClientNegozioInterfaccia() {
        return clientNegozioInterfaccia;
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

    public void aggiornaProdottiNegozio(List<Prodotto> newProdottiNegozio) {
        modelloClientNegozio.setProdottiNegozio(newProdottiNegozio);
        clientNegozioInterfaccia.aggiornaTabellaProdottiNegozio(newProdottiNegozio);
    }

    // Intuile
    public void aggiornaProdottiCarrello(int idTransazione) {
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

    public void aggiornaStateTransazioneId(int idTransazione, boolean istTipoVendita) {
        Transazione transazione = modelloClientNegozio.trovaTransazione(idTransazione);
        if (transazione == null) {
            System.err.println(" - Errore: Transazione non trovata");
            return;
        }

        if (istTipoVendita) {
            clientNegozioInterfaccia.aggiornaStatoTransazioneInTabellaVendita(transazione, getProdottiCarrello());
        } else {
            clientNegozioInterfaccia.aggiornaStatoTransazioneInTabellaAcquisto(transazione, getProdottiNegozio(),
                    getProdottiCarrello());
        }

        System.out.println(" --> UI: Lista transazione aggiornato!!");


    }

    public void aggiornaStateTransazioneFail(int idTransazione, boolean isTipoVendita) {
        Transazione transazione = modelloClientNegozio.trovaTransazione(idTransazione);

        if (transazione == null) {
            System.err.println(" - Errore: Transazione non trovata");
            return;
        }
        if (isTipoVendita) {
            clientNegozioInterfaccia.aggiornaStateTransazioneFail(transazione, isTipoVendita);
        } else {
            clientNegozioInterfaccia.aggiornaStateTransazioneFail(transazione, isTipoVendita);
        }
    }

    public void aggiungiListaTransazioneAcquisto(List<Transazione> listaTransazioni) {
        modelloClientNegozio.aggiungiListaTransazioneAcquisto(listaTransazioni);
    }

    public void aggiungiListaTransazioneVendita(List<Transazione> listaTransazioni) {
        modelloClientNegozio.aggiungiListaTransazioneVendita(listaTransazioni);
    }

    public void addAllTransazioneAcquistoAwait(List<Transazione> listaTransazione) {
        if (listaTransazione == null) {
            System.err.println(" - Errore: Lista transazione non trovata");
            return;
        }
        clientNegozioInterfaccia.addAllTransazioneCompraAwait(listaTransazione, getProdottiNegozio());
    }

    public void addSingleTransazioneCompraAwait(Transazione transazione) {
        if (transazione == null) {
            System.err.println(" - Errore: Transazione non trovata");
            return;
        }
        modelloClientNegozio.aggiungiTransazioneAcquisto(transazione);
        clientNegozioInterfaccia.addSingleTransazioneCompraAwait(transazione, getProdottiNegozio());

    }

    public void addSingleTransazioneVendiAwait(Transazione transazione) {
        if (transazione == null) {
            System.err.println(" - Errore: Transazione non trovata");
            return;
        }
        modelloClientNegozio.aggiungiTransazioneVendita(transazione);
        clientNegozioInterfaccia.addSingleTransazioneVenditaAwait(transazione, getProdottiCarrello());
    }

    public synchronized void addAllTransazioneVenditaAwait(List<Transazione> listaTransazione) {
        if (listaTransazione == null) {
            System.err.println(" - Errore: Lista transazione non trovata");
            return;
        }
        clientNegozioInterfaccia.addAllTransazioneVenditaAwait(listaTransazione, getProdottiCarrello());
    }


    public void aggiornaStatoTransazioneInTabella(Transazione transazione) {
        clientNegozioInterfaccia.aggiornaStatoTransazioneInTabellaAcquisto(transazione, getProdottiNegozio(),
                getProdottiCarrello());
    }

    public int sommaQuantitaProdottoCarrello() {
        return modelloClientNegozio.sommaQuantitaCarrello();
    }

}
