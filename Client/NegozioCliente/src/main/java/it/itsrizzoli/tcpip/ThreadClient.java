package it.itsrizzoli.tcpip;


import static it.itsrizzoli.tools.TypeThread.*;

public class ThreadClient extends Thread {
    public static ClientConnessione clientConnessione;
    private final int typeThread;
    private boolean newConnessione = false;

    public ThreadClient(ClientConnessione clientConnessione, int typeThread) {
        setGlobalClientConnessione(clientConnessione);
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
            case THREAD_WRITE_TRANSAZIONI:
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
