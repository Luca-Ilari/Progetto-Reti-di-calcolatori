package it.itsrizzoli.tools;

public class TypeThread {
    public static final int THREAD_CONNESSIONE_READ = 0; // Thread che si conette per poi diventare un thread di lettura buffer loop
    public static final int THREAD_COMPRA_PRODOTTI = 2; // Thread che crea transazioni random e invia in formato json al server.
    public static final int THREAD_VENDI_PRODOTTI = 3; // Thread che crea transazioni random e invia in formato json al server.

}
