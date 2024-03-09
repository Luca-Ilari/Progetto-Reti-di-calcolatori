package it.itsrizzoli;

import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.tcpip.ThreadClient;
import it.itsrizzoli.ui.NegozioClientUI;

import javax.swing.SwingUtilities;
import java.util.Random;


public class App {


    public static void main(String[] args) {
        System.out.println("Start Client...");
        NegozioClientUI negozioClientUI1 = new NegozioClientUI("Negozio Online - Interfaccia Cliente %d".formatted(new Random().nextInt(0,1000)));

        SwingUtilities.invokeLater(() -> {
            negozioClientUI1.inizzalizza();
            negozioClientUI1.setVisible(true);
            System.out.println(" --- FINE THREAD_SWING_EDT ---");
        });

        // Start Thread - connessioni al server  ---> Recupero Prodotti negozio --> aggiornamento UI
        new ClientConnessione("173.212.203.208", 5555, negozioClientUI1);

    }
}
