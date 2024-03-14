package it.itsrizzoli.controller;

import it.itsrizzoli.model.ModelloClientNegozio;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.view.ClientNegozioInterfaccia;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ControllerClientNegozioTest {

    private ModelloClientNegozio modelloClientNegozio;
    private ClientNegozioInterfaccia clientNegozioInterfaccia;
    private ClientConnessione clientConnessione;
    private ControllerClientNegozio controllerClientNegozio;

    @Before
    public void inizializza() {

        modelloClientNegozio = new ModelloClientNegozio();
        clientNegozioInterfaccia = new ClientNegozioInterfaccia("Test");
        clientConnessione = new ClientConnessione();

        controllerClientNegozio = new ControllerClientNegozio(modelloClientNegozio, clientNegozioInterfaccia,
                clientConnessione);



    }

    @Test
    void aggiornaProdottiNegozio() {

    }

    @Test
    void aggiornaStatoConnessione() {
    }

    @Test
    void aggiornaStateTransazioneId() {
    }

    @Test
    void aggiornaStateTransazioneFail() {
    }

    @Test
    void aggiungiListaTransazione() {
    }

    @Test
    void addAllTransazioneAwait() {
    }

    @Test
    void startThreadTransazioni() {
    }

    @Test
    void addSingleTransazioneAwait() {
    }

    @Test
    void aggiornaStatoTransazioneInTabella() {
    }
}