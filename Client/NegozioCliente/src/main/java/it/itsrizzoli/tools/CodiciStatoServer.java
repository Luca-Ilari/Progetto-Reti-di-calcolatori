package it.itsrizzoli.tools;

public class CodiciStatoServer {
    public static final int START_SESSION = 1;  // Avvio sessione
    public static final int RIMUOVI_PRODOTTO = 2;  // Rimozione prodotto dal Negozio
    public static final int AGGIUNGI_PRODOTTO = 3;  // Aggiunta prodotto al Negozio
    public static final int LISTA_PRODOTTI_AGGIORNATO = 4;  // Lista prodotti aggiornata
    public static final int SUCCESSO_TRANSAZIONE_ACQUISTO = 5;  // Successo transazione
    public static final int SUCCESSO_TRANSAZIONE_VENDITA = 6; // Successo transazione
    public static final int FAIL_SESSION = -1;  // Fallimento sessione
    public static final int FAIL_RIMUOVI_PRODOTTO = -2;  // Fallimento rimozione prodotto dal Negozio
    public static final int FAIL_AGGIUNGI_PRODOTTO = -3;  // Fallimento aggiunta prodotto al Negozio
    public static final int PRODOTTO_FINITO = -4;  // Fallimento aggiunta prodotto al Negozio
}
