package it.itsrizzoli;

import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.ui.NegozioClientUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class App {

    public static NegozioClientUI negozioClientUI;

    public static void main(String[] args) {
        System.out.println("Start Client...");

        SwingUtilities.invokeLater(() -> {
        App.negozioClientUI = new NegozioClientUI();
        });



        // connessioni al server  ---> Creazione di una lista di transazioni --> aggiornamento UI
        ClientConnessione clientConnessione = new ClientConnessione();

        // Aggiorno UI

    }
}
