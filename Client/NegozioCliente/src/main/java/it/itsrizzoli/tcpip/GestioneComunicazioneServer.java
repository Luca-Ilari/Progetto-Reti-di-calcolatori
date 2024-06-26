package it.itsrizzoli.tcpip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tools.CodiciStatoServer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class GestioneComunicazioneServer {
    private final static Logger logger = Logger.getLogger("AvvisiGestioneServer");
    private static ControllerClientNegozio controllerClientNegozio;

    static {
        attivaColoreLogger();
    }

    public static void setControllerClientNegozio(ControllerClientNegozio controllerClientNegozio) {
        GestioneComunicazioneServer.controllerClientNegozio = controllerClientNegozio;
    }

    public static void gestisciJsonCodiceStato(String jsonResponse, ControllerClientNegozio controllerClientNegozio) {
        if (jsonResponse == null) {
            logger.warning("Errore nella JSON ricevuta!");
            return;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            int codeStatus = jsonNode.has("codiceStato") ? jsonNode.get("codiceStato").asInt() : -1;
            switch (codeStatus) {
                case CodiciStatoServer.LISTA_PRODOTTI_AGGIORNATO:
                    aggiornaListaProdotti(jsonNode, controllerClientNegozio);
                    break;
                case CodiciStatoServer.SUCCESSO_TRANSAZIONE_ACQUISTO:
                    gestioneSuccessoTransazione(jsonNode, false);
                    break;
                case CodiciStatoServer.SUCCESSO_TRANSAZIONE_VENDITA:
                    gestioneSuccessoTransazione(jsonNode, true);
                    break;
                case CodiciStatoServer.FAIL_RIMUOVI_PRODOTTO:
                    gestioneFallimentoTransazione(jsonNode, false);
                    break;
                case CodiciStatoServer.FAIL_AGGIUNGI_PRODOTTO:
                    gestioneFallimentoTransazione(jsonNode, true);
                    break;
                default:
                    logger.warning("Errore: Codice di stato non valido.");
                    break;
            }
        } catch (JsonProcessingException e) {
            System.err.println("Attenzione: non è un json");
        }
    }

    private static void aggiornaListaProdotti(JsonNode jsonNode, ControllerClientNegozio controllerClientNegozio) {
        List<Prodotto> listaProdotti = recuperoListaProdottiJson(jsonNode);

        if (controllerClientNegozio == null || controllerClientNegozio.getClientNegozioInterfaccia() == null) {
            logger.warning("ATTENZIONE: controllerClientNegozio o clientNegozioInterfaccia null");
            return;
        }
        try {
            controllerClientNegozio.aggiornaProdottiNegozio(listaProdotti);
        } catch (NullPointerException e) {
            logger.warning("Errore nell'aggiornamento della lista prodotti: " + e.getMessage());
        }
    }

    public static String serializzaTransazione(Transazione transazione, int CODICE_STATO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("codiceStato", CODICE_STATO);

        // Aggiungi direttamente l'oggetto transazione come nodo
        ObjectNode transazioneNode = objectMapper.convertValue(transazione, ObjectNode.class);
        objectNode.set("transazione", transazioneNode);

        String jsonString = objectMapper.writeValueAsString(objectNode);

        return jsonString + "|";
    }

    public static void inviaTransazioneSingola(Transazione transazione, int CODICE_STATO, PrintWriter out) {
        if (out == null || transazione == null) {
            logger.severe("Impossibile inviare la transazione: out o transazione sono nulli.");
            return;
        }

        String jsonString;
        try {
            jsonString = serializzaTransazione(transazione, CODICE_STATO);
        } catch (JsonProcessingException e) {
            logger.warning("Errore durante la conversione in JSON: " + e.getMessage());
            return;
        }
        out.println(jsonString);
        logger.info("Transazione inviata correttamente al server.");
    }

    private static void gestioneSuccessoTransazione(JsonNode jsonNode, boolean isVendita) {
        String transactionType = isVendita ? "Vendita" : "Acquisto";
        System.out.println("Client riceve codice Status " + (isVendita ? "6" : "5") + " : Successo nella Transazione "
                + "- " + transactionType);
        try {
            int idTransazione = jsonNode.get("idTransazione").asInt();
            controllerClientNegozio.aggiornaStateTransazioneId(idTransazione, isVendita);
        } catch (Exception e) {
            logger.severe("Errore nella transazione");
        }

    }

    private static void gestioneFallimentoTransazione(JsonNode jsonNode, boolean isAggiunta) {
        String transactionType = isAggiunta ? "aggiunta" : "rimozione";
        int idTransazione = jsonNode.has("idTransazione") ? jsonNode.get("idTransazione").asInt() : -1;
        System.out.println("Client riceve codice Status -" + (isAggiunta ? "3" : "2") + ": Fallimento " + transactionType + " prodotto dal Negozio");
        controllerClientNegozio.aggiornaStateTransazioneFail(idTransazione, isAggiunta);
    }

    public static List<Prodotto> recuperoListaProdottiJson(JsonNode jsonNode) {
        logger.info("Client riceve codice Status 4: Lista prodotti aggiornata");
        List<Prodotto> prodotti = new ArrayList<>();
        try {
            JsonNode prodottiNode = jsonNode.get("prodotti");

            for (JsonNode prodottoNode : prodottiNode) {
                int id = prodottoNode.get("id").asInt();
                String nome = prodottoNode.get("nome").asText();
                double prezzo = prodottoNode.get("prezzo").asDouble();
                int quantitaDisponibile = prodottoNode.get("quantitaDisponibile").asInt();
                Prodotto prodotto = new Prodotto(id, nome, prezzo, quantitaDisponibile);
                prodotti.add(prodotto);
                //logger.info(prodotto.toString());
            }
        } catch (NullPointerException e) {
            logger.severe("Formato json non è valido!!\n");
            logger.severe("JSON: " + jsonNode.toString());
        }
        return prodotti;
    }

    private static void attivaColoreLogger() {
        ConsoleHandler handler = new ConsoleHandler();
        logger.setUseParentHandlers(false);
        handler.setFormatter(new Formatter() {
            @Override
            public synchronized String format(LogRecord record) {
                String levelName = record.getLevel().getName();
                String message = record.getMessage();

                // Assegna il colore in base al livello del log
                String color = switch (levelName) {
                    case "INFO" -> "\u001B[34m"; // Blu
                    case "WARNING" -> "\u001B[33m"; // Giallo
                    case "SEVERE" -> "\u001B[31m";
                    default -> ""; // Rosso
                };
                // Resetta il colore dopo il messaggio
                String resetColor = "\u001B[0m";

                // Formatta il messaggio con il colore
                return color + "[" + levelName + "] " + message + resetColor + "\n";
            }


        });
        logger.addHandler(handler);
    }
}
