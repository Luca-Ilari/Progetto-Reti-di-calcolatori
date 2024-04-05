package it.itsrizzoli;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.ModelloClientNegozio;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.view.ClientNegozioInterfaccia;

import javax.swing.*;
import java.util.Random;


public class App {

    public static void main(String[] args) {

        try {
            // Imposta il look and feel di sistema (di default)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println("Starting Client...");

            int idClientRandom = generateRandomId();

            // Inizializza l'interfaccia client
            ClientNegozioInterfaccia clientInterfaccia = initializeClientInterface(idClientRandom);

            // Inizializza il modello client
            ModelloClientNegozio modelloClient = initializeClientModel();

            // Inizializza la connessione client
            ClientConnessione clientConnessione = initializeClientConnection();

            // Inizializza il controller client
            ControllerClientNegozio clientController = initializeClientController(modelloClient, clientConnessione,
                    clientInterfaccia);
        } catch (Exception e) {
            // Gestione delle eccezioni
            System.err.println("An error occurred during initialization: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private static int generateRandomId() {
        return new Random().nextInt(0, 10000);
    }

    private static ClientNegozioInterfaccia initializeClientInterface(int idClientRandom) {
        return new ClientNegozioInterfaccia("Negozio Online - Interfaccia Client " + idClientRandom);
    }

    private static ModelloClientNegozio initializeClientModel() {
        return new ModelloClientNegozio();
    }

    private static ClientConnessione initializeClientConnection() {
        return new ClientConnessione();
    }

    private static ControllerClientNegozio initializeClientController(ModelloClientNegozio modelloClient,
                                                                      ClientConnessione clientConnessione,
                                                                      ClientNegozioInterfaccia clientInterfaccia) {
        return new ControllerClientNegozio(modelloClient, clientConnessione, clientInterfaccia);
    }


}
