package it.itsrizzoli.tcpip;


import static it.itsrizzoli.tools.TypeThread.*;

public class ThreadClient extends Thread {
    public static ClientConnessione clientConnessione;
    private final int typeThread;

    public ThreadClient(ClientConnessione clientConnessione, int typeThread) {
        setGlobalClientConnessione(clientConnessione);
        this.typeThread = typeThread;
    }

    public ThreadClient(int typeThread) {
        this.typeThread = typeThread;
    }

    public void setGlobalClientConnessione(ClientConnessione clientConnessione) {
        ThreadClient.clientConnessione = clientConnessione;
    }


    @Override
    public void run() {
        String message = "";
        switch (typeThread) {
            case THREAD_CONNESSIONE_READ:
                System.out.println("Thread di connessione avviato.");
                while (true) {
                    clientConnessione.tentaConnessione(); // Tentativo di connessione
                    message = "Thread di connessione completato.\n---- SESSIONE AVVIATA ----";

                    clientConnessione.aggiornaStatoNegozio(true);
                    setGlobalClientConnessione(clientConnessione);

                    clientConnessione.onConnessione = true;

                    System.err.println("Messaggio: " + message);

                    System.out.println(" lettura loop avviato.");
                    boolean connessionePersa = clientConnessione.readLoop(); // Avvio del loop di lettura

                    message = "Thread di lettura completato.\n---- SESSIONE TERMINATA ----";
                    System.out.println(message);

                    clientConnessione.aggiornaStatoNegozio(false);
                }
            case THREAD_WRITE:
                System.out.println("Thread di scrittura avviato.");
                //clientConnessione.writeTransazioniJson(); // Avvio del loop di scrittura
                clientConnessione.writeTransazioniJson();
                message = "Thread di scrittura completato.";
                break;
            default:
                System.err.println("Errore: Tipo di thread non valido.");
                break;
        }

        if (!message.isEmpty()) System.err.println("Messaggio: " + message);
    }

}
