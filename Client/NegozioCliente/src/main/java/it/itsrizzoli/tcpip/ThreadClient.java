package it.itsrizzoli.tcpip;


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
        String message;
        switch (typeThread) {
            case THREAD_CONNESSIONE:
                clientConnessione.tentaConnessione(); // Metodo per tentare la connessione
                message = "Thread per la connessione\n\n ---- START SESSION ---";

                ExecutorService executor = Executors.newCachedThreadPool();
                ThreadClient threadClientConnessione1 = new ThreadClient(clientConnessione, THREAD_READ);

                executor.submit(threadClientConnessione1);

                break;
            case THREAD_READ:
                clientConnessione.readLoop(); // Metodo per avviare il loop di lettura
                message = "Thread per la lettura";
                break;
            case THREAD_WRITE:
                clientConnessione.writeTransazioniJson(); // Metodo per avviare il loop di scrittura
                message = "Thread per la scrittura";
                break;
            default:
                message = "";
                System.out.println("Errore: Tipo di thread non valido");
                break;
        }

        if (!message.isEmpty()) System.out.println(" Attenzione: chiusura del " + message);
    }
}
