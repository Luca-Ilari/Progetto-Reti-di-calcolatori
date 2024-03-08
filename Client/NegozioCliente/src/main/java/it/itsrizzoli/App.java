package it.itsrizzoli;

import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.ui.NegozioClientUI;

import javax.swing.SwingUtilities;


public class App {


    public static void main(String[] args) {
        System.out.println("Start Client...");
        NegozioClientUI negozioClientUI1 = new NegozioClientUI("Negozio Online - Interfaccia Cliente");

        SwingUtilities.invokeLater(() -> {
            negozioClientUI1.inizzalizza();
            negozioClientUI1.setVisible(true);
            System.out.println(" --- FINE THREAD_SWING_EDT ---");
        });

        // Start Thread - connessioni al server  ---> Recupero Prodotti negozio --> aggiornamento UI
        ClientConnessione clientConnessione = new ClientConnessione("173.212.203.208", 5555, negozioClientUI1);


    }
}
