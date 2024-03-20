package it.itsrizzoli.tcpip;


import it.itsrizzoli.controller.ControllerClientNegozio;

import java.util.Random;

import static it.itsrizzoli.tools.TypeThread.*;

public class ThreadClient extends Thread {
    public static ClientConnessione clientConnessione;
    private final int typeThread;
    private boolean newConnessione = false;

    public ThreadClient(ControllerClientNegozio controllerClientNegozio, int typeThread) {
        setGlobalClientConnessione(controllerClientNegozio.getClientConnessione());
        this.typeThread = typeThread;
    }

    public ThreadClient(int typeThread) {
        this.typeThread = typeThread;
    }

    public void setNewConnessione(boolean newConnessione) {
        this.newConnessione = newConnessione;
    }

    public void setGlobalClientConnessione(ClientConnessione clientConnessione) {
        ThreadClient.clientConnessione = clientConnessione;
    }


    @Override
    public void run() {
        String message = "";
        switch (typeThread) {
            case THREAD_CONNESSIONE_READ:
                System.out.println("Thread di tentativo connessione avviato.");
                while (!newConnessione) {

                    clientConnessione.tentaConnessione(); // Tentativo di connessione

                    clientConnessione.aggiornaStato(true);
                    setGlobalClientConnessione(clientConnessione);


                    clientConnessione.readLoop(); // Avvio del loop di lettura


                    clientConnessione.aggiornaStato(false);

                }
                newConnessione = false;
                break;
            case THREAD_COMPRA_PRODOTTI:
                System.out.println("Thread di scrittura avviato. - COMPRA");
                //clientConnessione.writeTransazioniJson(); // Avvio del loop di scrittura
                message = "Thread di scrittura completato.";
                while (true) {
                    clientConnessione.inviaTransazioniAcquistaProdotti();
                    try {
                        Thread.sleep(new Random().nextInt(10, 10000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
            case THREAD_VENDI_PRODOTTI:
                System.out.println("Thread di scrittura avviato. - VENDI");
                while (true) {
                    clientConnessione.inviaTransazioniVendiProdotti();
                    try {
                        Thread.sleep(new Random().nextInt(10, 10000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            default:
                System.err.println("Errore: Tipo di thread non valido.");
                break;
        }

        if (!message.isEmpty()) System.err.println("Messaggio: " + message);
    }

}
