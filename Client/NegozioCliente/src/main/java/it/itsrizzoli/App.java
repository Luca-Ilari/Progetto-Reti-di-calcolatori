package it.itsrizzoli;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.ModelloClientNegozio;
import it.itsrizzoli.tcpip.ClientConnessione;
import it.itsrizzoli.view.ClientNegozioInterfaccia;

import java.util.Random;


public class App {

    public static void main(String[] args) {
        System.out.println("Start Client...");
        int idRandom = new Random().nextInt(0, 10000);

        ClientNegozioInterfaccia clientNegozioInterfaccia = new ClientNegozioInterfaccia("Negozio Online - " +
                "Interfaccia Client %d".formatted(idRandom));

        ModelloClientNegozio modelloClientNegozio = new ModelloClientNegozio();

        ClientConnessione clientConnessione = new ClientConnessione();

        ControllerClientNegozio controllerClientNegozio = new ControllerClientNegozio(modelloClientNegozio,
                clientNegozioInterfaccia, clientConnessione);

    }
}
