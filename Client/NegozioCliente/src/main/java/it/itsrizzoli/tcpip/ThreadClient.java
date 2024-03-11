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
                System.out.println("Thread di tentativo connessione avviato.");
                for (int i = 0; i < 100; i++) {
                    System.out.println(" Tentativo Connessione: " + (i + 1));
                    clientConnessione.tentaConnessione(); // Tentativo di connessione
                    message = "Thread di connessione completato.\n---- SESSIONE AVVIATA ----";

                    clientConnessione.aggiornaStato(true);
                    setGlobalClientConnessione(clientConnessione);

                    System.err.println("Messaggio: " + message);

                    System.out.println(" Thread connessione diveenta di lettura loop.");

                    clientConnessione.readLoop(); // Avvio del loop di lettura

                    message = "Thread di lettura completato.\n---- SESSIONE TERMINATA ----";
                    System.out.println(message);

                    clientConnessione.aggiornaStato(false);
                }
                break;
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
