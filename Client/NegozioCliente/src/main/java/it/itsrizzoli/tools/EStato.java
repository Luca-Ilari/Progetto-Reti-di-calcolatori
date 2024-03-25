package it.itsrizzoli.tools;
public enum EStato {
    IN_ATTESA_DI_CONFERMA("In attesa di conferma"),
    COMPLETATO("Completato"),
    ERRORE_DEL_SERVER("Errore del server"),
    PRODOTTO_ESAURITO_NEGOZIO("Prodotto esaurito (negoziante)"),
    PRODOTTO_ESAURITO_CLIENTE("Prodotto esaurito (cliente)"),
    QUANTITA_MASSIMA_RAGGIUNTA("Errore: quantità massima raggiunta"),
    QUANTITA_MINIMA_RAGGIUNTA("Errore: : quantità mininima quantità");

    private final String value;

    EStato(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

