package it.itsrizzoli;

import it.itsrizzoli.modelli.Transazione;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.ui.NegozioClientUI;

import javax.swing.*;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static NegozioClientUI negozioClientUI;

    public static void main(String[] args) {
        System.out.println("Start Client...");

        SwingUtilities.invokeLater(() -> {
            App.negozioClientUI = new NegozioClientUI();
        });


        // connessioni al server  ---> Creazione di una lista di transazioni
        ClientConnessione clientConnessione = new ClientConnessione();

        // Aggiorno UI

    }
}
