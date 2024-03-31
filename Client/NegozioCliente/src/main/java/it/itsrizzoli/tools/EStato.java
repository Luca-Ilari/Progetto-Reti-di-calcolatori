package it.itsrizzoli.tools;
public enum EStato {
    IN_ATTESA_DI_CONFERMA("In attesa"),
    COMPLETATO("Completata"),
    ERRORE_DEL_SERVER("Errore Server"),
    PRODOTTO_ESAURITO_NEGOZIO("Non disponibile"),
    PRODOTTO_ESAURITO_CLIENTE("Quantità insuff."),
    QUANTITA_MASSIMA_RAGGIUNTA("Max raggiunta"),
    QUANTITA_MINIMA_RAGGIUNTA("Min raggiunta");


    private final String value;

    EStato(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

