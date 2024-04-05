package it.itsrizzoli.tools;

public enum EStato {
    IN_ATTESA_DI_CONFERMA("Attesa"),
    COMPLETATO("Completata"),
    ERRORE_DEL_SERVER("Errore"),
    PRODOTTO_ESAURITO_NEGOZIO("NonDisp."),
    PRODOTTO_ESAURITO_CLIENTE("Insuff."),
    QUANTITA_MASSIMA_RAGGIUNTA("Max raggiunto"),
    QUANTITA_MINIMA_RAGGIUNTA("Min raggiunto");


    private final String value;

    EStato(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

