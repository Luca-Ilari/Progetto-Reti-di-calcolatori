package it.itsrizzoli;

import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.ui.NegozioClientUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class App {

    public static NegozioClientUI negozioClientUI;
    public static ClientConnessione clientConnessione;

    public static void main(String[] args) {
        System.out.println("Start Client...");

        clientConnessione = new ClientConnessione();

        SwingUtilities.invokeLater(() -> {
            App.negozioClientUI = new NegozioClientUI();
        });


        // connessioni al server  ---> Creazione di una lista di transazioni --> aggiornamento UI

        // Aggiorno UI

    }
}
