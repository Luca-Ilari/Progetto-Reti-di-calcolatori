package it.itsrizzoli;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.modelli.ModelloClientNegozio;
import it.itsrizzoli.ui.ClientNegozioInterfaccia;

import java.util.Random;


public class App {


    public static void main(String[] args) {
        System.out.println("Start Client...");
        int idRandom = new Random().nextInt(0, 10000);

        ClientNegozioInterfaccia clientNegozioInterfaccia1 = new ClientNegozioInterfaccia("Negozio Online - " +
                "Interfaccia Cliente %d".formatted(idRandom));

        ModelloClientNegozio modelloClientNegozio = new ModelloClientNegozio();


        // Start Thread - connessioni al server  ---> Recupero Prodotti negozio --> aggiornamento UI
        //ClientConnessione clientConnessione = new ClientConnessione("173.212.203.208", 5555,
        // clientNegozioInterfaccia1);

        ControllerClientNegozio controllerClientNegozio = new ControllerClientNegozio(modelloClientNegozio,
                clientNegozioInterfaccia1);

        clientNegozioInterfaccia1.setControllerClientNegozio(controllerClientNegozio);

        controllerClientNegozio.startInterfacciaClient();
    }
}
