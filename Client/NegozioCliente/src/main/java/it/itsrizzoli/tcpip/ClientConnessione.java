package it.itsrizzoli.tcpip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tools.CodiciStatoServer;
import it.itsrizzoli.view.ClientNegozioInterfaccia;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ClientConnessione {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String serverAddress = "localhost";
    private int serverPort = 5555;
    public boolean onConnessione = false;
    private final static Logger logger = Logger.getLogger("Avvisi");

    private ControllerClientNegozio controllerClientNegozio;


    public ClientConnessione() {
        attivaColoreLogger();
    }

    public ClientConnessione(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        attivaColoreLogger();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setControllerClientNegozio(ControllerClientNegozio controllerClientNegozio) {
        this.controllerClientNegozio = controllerClientNegozio;
    }

    public void startConnessione() {
        Thread threadConnessione = new Thread(() -> {
            while (true) {
                System.out.println("Thread di tentativo di connessione avviato.");
                try {
                    tentaConnessione();

                    aggiornaStato(true);

                    readLoop();

                    terminaConnessioneConErroreServer();
                } catch (Exception e) {
                    System.err.println("Errore durante la connessione: " + e.getMessage());
                }
            }
        });
        threadConnessione.start();
    }

    private void terminaConnessioneConErroreServer() {
        aggiornaStato(false);
        ClientNegozioInterfaccia clientNegozioInterfaccia = controllerClientNegozio.getClientNegozioInterfaccia();
        clientNegozioInterfaccia.aggiornaStatoTransazioneServerError();
    }


    public void aggiornaIP(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        logger.warning(" --- CHIUSURA SOCKET ---");

        chiusuraConnessione();

        logger.info("Nuovo credenziali SERVER: \n IP --> " + serverAddress + " | PORTA --> " + serverPort);

        startConnessione();

    }

    public void aggiornaStato(boolean stato) {
        this.onConnessione = stato;
        this.controllerClientNegozio.aggiornaStatoConnessione(stato);

    }


    protected void readLoop() {
        String message = "Thread di connessione completato.\n---- SESSIONE AVVIATA ----";
        logger.warning(message);

        logger.info(" Thread connessione diveenta di lettura loop.");
        String risposta;
        try {
            while ((risposta = in.readLine()) != null) {
                logger.info(" - Server: " + risposta);
                gestioneJsonCodiceStato(risposta);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        message = "Thread di lettura completato.\n---- SESSIONE TERMINATA ----";
        logger.warning(message);

    }

    public void inviaSingolaTransazione(Transazione transazione, int CODICE_STATO) {

        if (transazione == null) {
            logger.severe(" Transazione è null ");
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = getJsonTransazione(transazione, objectMapper, CODICE_STATO);
            out.println(jsonString);// Invia la transazione al server
        } catch (JsonProcessingException e) {
            logger.warning("Errore durante la conversione in JSON");
        }
        logger.info(" Fine Transazioni al server.");
    }

    private String getJsonTransazione(Transazione transazione, ObjectMapper objectMapper, int CODICE_STATO) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("codiceStato", CODICE_STATO);

        // Aggiungi direttamente l'oggetto transazione come nodo
        ObjectNode transazioneNode = objectMapper.convertValue(transazione, ObjectNode.class);
        objectNode.set("transazione", transazioneNode);

        String jsonString = objectMapper.writeValueAsString(objectNode);


        return jsonString + "|";
    }

    private String getJsonTransazione(Transazione transazione, Prodotto prodotto, ObjectMapper objectMapper,
                                      int CODICE_STATO) throws JsonProcessingException {
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("codiceStato", CODICE_STATO);

        ObjectNode transazioneNode = rootNode.putObject("transazione");
        transazioneNode.put("idTransazione", 123);

        ObjectNode prodottoNode = transazioneNode.putObject("prodotto");
        prodottoNode.put("idProdotto", prodotto.getIdProdotto());
        prodottoNode.put("nome", prodotto.getNome());
        prodottoNode.put("prezzo", prodotto.getPrezzo());
        prodottoNode.put("quantita", prodotto.getQuantitaDisponibile());

        String jsonOutput = rootNode.toPrettyString();
        System.out.println(jsonOutput);
        return jsonOutput;
    }

    private void gestioneJsonCodiceStato(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            int codeStatus = jsonNode.has("codiceStato") ? jsonNode.get("codiceStato").asInt() : -1;

            switch (codeStatus) {
                case CodiciStatoServer.LISTA_PRODOTTI_AGGIORNATO:
                    List<Prodotto> listaProdotti = recuperoListaProdottiJson(jsonNode);
                    if (listaProdotti == null) {
                        System.out.println("ATTENZIONE: lista Prodotti null");
                        break;
                    }
                    controllerClientNegozio.aggiornaProdottiNegozio(listaProdotti);
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
                    System.out.println("Errore: Codice di stato non valido.");
                    break;
            }
        } catch (JsonProcessingException e) {
            System.err.println("Attenzione: non è un json");
        }
    }

    private void gestioneSuccessoTransazione(JsonNode jsonNode, boolean isVendita) {
        String transactionType = isVendita ? "Vendita" : "Acquisto";
        logger.info("Client riceve codice Status " + (isVendita ? "6" : "5") + " : Successo nella Transazione - " + transactionType);
        int idTransazione = jsonNode.has("idTransazione") ? jsonNode.get("idTransazione").asInt() : -1;
        controllerClientNegozio.aggiornaStateTransazioneId(idTransazione, isVendita);
    }

    private void gestioneFallimentoTransazione(JsonNode jsonNode, boolean isAggiunta) {
        String transactionType = isAggiunta ? "aggiunta" : "rimozione";
        int idTransazione = jsonNode.has("idTransazione") ? jsonNode.get("idTransazione").asInt() : -1;
        System.out.println("Client riceve codice Status -" + (isAggiunta ? "3" : "2") + ": Fallimento " + transactionType + " prodotto dal Negozio");
        controllerClientNegozio.aggiornaStateTransazioneFail(idTransazione, isAggiunta);
    }


    private List<Prodotto> recuperoListaProdottiJson(JsonNode jsonNode) { // Recupero gli articoli Negozio
        logger.info("Client riceve codice Status 4: Lista prodotti aggiornata");
        JsonNode prodottiNode = jsonNode.get("prodotti");
        if (prodottiNode == null) {
            return null;
        }
        // Creare una lista di prodotti
        List<Prodotto> prodotti = new ArrayList<>();

        // Iterare su ogni prodotto nell'array JSON
        for (JsonNode prodottoNode : prodottiNode) {
            int id = prodottoNode.get("id").asInt();
            String nome = prodottoNode.get("nome").asText();
            double prezzo = prodottoNode.get("prezzo").asDouble();
            int quantitaDisponibile = prodottoNode.get("quantitaDisponibile").asInt();
            Prodotto prodotto = new Prodotto(id, nome, prezzo, quantitaDisponibile);
            prodotti.add(prodotto);
            System.out.println(prodotto);
        }

        return prodotti;
    }

    public void chiusuraConnessione() {
        onConnessione = false;

        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }

        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public void tentaConnessione() {
        boolean connected = false;
        while (!connected) {
            try {
                connessioneAlServer();
                connected = true;
            } catch (IOException e) {
                logger.severe("Connessione rifiutata!!\n\n");
                logger.info("Tentativo di riconnessione...");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    // Gestisci l'eccezione se il thread viene interrotto durante il sonno
                    logger.severe(e.getMessage());
                }
            }
        }
    }

    private void connessioneAlServer() throws IOException {
        clientSocket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        logger.info("Successo: Connessione avvenuta al server con IP: " + serverAddress + " - PORTA: " + serverPort);

    }

    private void attivaColoreLogger() {
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
