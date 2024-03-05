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


        SwingUtilities.invokeLater(() -> {
            negozioClientUI = new NegozioClientUI();
            // connessioni al server  ---> Creazione di una lista di transazioni --> aggiornamento UI
            clientConnessione = new ClientConnessione("173.212.203.208", 5555);
        });


    }
}
