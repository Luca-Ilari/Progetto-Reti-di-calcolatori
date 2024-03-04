package it.itsrizzoli.tcpip;


import it.itsrizzoli.App;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.itsrizzoli.tools.TypeThread.*;

public class ThreadClient implements Runnable {
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
            case THREAD_CONNESSIONE:
                System.out.println("Thread di connessione avviato.");
                clientConnessione.tentaConnessione(); // Tentativo di connessione
                message = "Thread di connessione completato.\n---- SESSIONE AVVIATA ----";

                // Avvio del thread di lettura dopo la connessione
                Thread threadClientConnessione1 = new Thread(new ThreadClient(clientConnessione, THREAD_READ));
                threadClientConnessione1.start();
                break;
            case THREAD_READ:
                System.out.println("Thread di lettura avviato.");
                boolean connessionePersa = clientConnessione.readLoop(); // Avvio del loop di lettura
                message = "Thread di lettura completato.\n---- SESSIONE TERMINATA ----";

                // Ritenta connessione
                if (connessionePersa) {
                    Thread threadClientConnessione2 = new Thread(new ThreadClient(clientConnessione,
                            THREAD_CONNESSIONE));
                    threadClientConnessione2.start();
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
