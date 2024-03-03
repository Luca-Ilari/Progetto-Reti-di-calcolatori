package it.itsrizzoli;

import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.ui.NegozioClientUI;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 */
public class App {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5555;
    public static NegozioClientUI negozioClientUI;

    public static void main(String[] args) {
        System.out.println("Start Client...");

        SwingUtilities.invokeLater(() -> {
            App.negozioClientUI = new NegozioClientUI();
        });


        // connessioni al server
        ClientConnessione clientConnessione = new ClientConnessione(SERVER_ADDRESS, SERVER_PORT);

        // Recupero gli articoli Negozio


        // Aggiorno UI


        clientConnessione.closeResources();
    }
}
