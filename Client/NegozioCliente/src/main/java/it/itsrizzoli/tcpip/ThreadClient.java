package it.itsrizzoli.tcpip;


import it.itsrizzoli.App;

import static it.itsrizzoli.tools.TypeThread.*;

public class ThreadClient extends Thread {
    public ClientConnessione clientConnessione;
    private final int typeThread;

    public ThreadClient(ClientConnessione clientConnessione, int typeThread) {
        this.clientConnessione = clientConnessione;
        this.typeThread = typeThread;
    }


    @Override
    public void run() {
        String message = "";
        switch (typeThread) {
            case THREAD_CONNESSIONE_READ:
                System.out.println("Thread di connessione avviato.");
                while (!clientConnessione.onConnessione) {

                    clientConnessione.tentaConnessione(); // Tentativo di connessione
                    message = "Thread di connessione completato.\n---- SESSIONE AVVIATA ----";
                    System.err.println("Messaggio: " + message);

                    clientConnessione.onConnessione = true;

                    System.out.println(" lettura loop avviato.");
                    boolean connessionePersa = clientConnessione.readLoop(); // Avvio del loop di lettura
                    message = "Thread di lettura completato.\n---- SESSIONE TERMINATA ----";

                    clientConnessione.onConnessione = connessionePersa;
                }
                break;
            case THREAD_WRITE:
                System.out.println("Thread di scrittura avviato.");
                //clientConnessione.writeTransazioniJson(); // Avvio del loop di scrittura
                clientConnessione.writeTransazioniJson(App.negozioClientUI);
                message = "Thread di scrittura completato.";
                break;
            default:
                System.err.println("Errore: Tipo di thread non valido.");
                break;
        }

        if (!message.isEmpty()) System.err.println("Messaggio: " + message);
    }

}
