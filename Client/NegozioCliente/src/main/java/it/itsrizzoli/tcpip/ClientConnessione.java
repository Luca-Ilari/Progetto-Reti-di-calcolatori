package it.itsrizzoli.tcpip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tools.CodiciStatoServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static it.itsrizzoli.tools.TypeThread.*;

public class ClientConnessione {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String serverAddress = "localhost";
    private int serverPort = 5555;
    public boolean onConnessione = false;
    private final static Logger logger = Logger.getLogger("Avvisi");

    private final ControllerClientNegozio controllerClientNegozio;

    private ThreadClient threadConnessione;

    public ClientConnessione(ControllerClientNegozio controllerClientNegozio) {
        this.controllerClientNegozio = controllerClientNegozio;

        attivaColoreLogger();
        startConnessione();


    }

    public ClientConnessione(String serverAddress, int serverPort, ControllerClientNegozio controllerClientNegozio) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        this.controllerClientNegozio = controllerClientNegozio;
        attivaColoreLogger();
        startConnessione();

    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }


    private void startConnessione() {
        threadConnessione = new ThreadClient(this, THREAD_CONNESSIONE_READ);
        threadConnessione.start();
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


    protected boolean readLoop() {
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

        return true;

    }

    public synchronized void writeTransazioniJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Transazione> sendTransazioni =
                Transazione.creaListaTransazioniRandom(controllerClientNegozio.getProdottiNegozio());

        controllerClientNegozio.aggiungiListaTransazione(sendTransazioni);

        controllerClientNegozio.addAllTransazioneAwait(sendTransazioni);

        for (Transazione transazione : sendTransazioni) {
            try {
                String jsonString = getJsonTransazione(transazione, objectMapper);
                out.println(jsonString);// Invia la transazione al server
                logger.info("Client: Transazione inviata al server.");

                // clientNegozioInterfaccia.addSingleTransazioneAwait(transazione);
                //controllerClientNegozio.addSingleTransazioneAwait(transazione);
            } catch (JsonProcessingException e) {
                logger.warning("Errore durante la conversione in JSON");
            }
            try {
                // Attendere 5 secondi prima di inviare la prossima transazione
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interruzione durante l'attesa", e);
            }
        }
    }


    private static String getJsonTransazione(Transazione transazione, ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("codiceStato", CodiciStatoServer.RIMUOVI_PRODOTTO);

        // Aggiungi direttamente l'oggetto transazione come nodo
        ObjectNode transazioneNode = objectMapper.convertValue(transazione, ObjectNode.class);
        objectNode.set("transazione", transazioneNode);

        String jsonString = objectMapper.writeValueAsString(objectNode);


        System.out.println("Transazione JSON: " + jsonString);
        return jsonString;
    }

    private void gestioneJsonCodiceStato(String jsonResponse) {
        JsonNode jsonNode;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            System.err.println(" Attenzione: non è un json");
            return;
        }


        int codeStatus;
        try {
            codeStatus = jsonNode.get("codiceStato").asInt();

        } catch (NullPointerException e) {
            logger.info(e.getMessage());
            return;
        }
        switch (codeStatus) {
            case CodiciStatoServer.START_SESSION:
                System.out.println("Client riceve codice Status 1: Avvio sessione");
                break;
            case CodiciStatoServer.RIMUOVI_PRODOTTO:
                System.out.println("Client riceve codice Status 2: Rimozione prodotto dal Negozio");
                break;
            case CodiciStatoServer.AGGIUNGI_PRODOTTO:
                System.out.println("Client riceve codice Status 3: Aggiunta prodotto al Negozio");
                break;
            case CodiciStatoServer.LISTA_PRODOTTI_AGGIORNATO:
                List<Prodotto> listaProdotti = recuperoListaProdottiJson(jsonNode);
                if (listaProdotti == null) {
                    System.out.println(" ATTENZIONE: lista Prodotti null");
                    break;
                }
                //aggiorna negozio prodotti UI
                controllerClientNegozio.aggiornaProdottiNegozio(listaProdotti);

                break;
            case CodiciStatoServer.SUCCESSO_TRANSAZIONE:
                logger.info("Client riceve codice Status 5 : Successo nella Transazione");
                int idTransazione = jsonNode.get("idTransazione").asInt();
                controllerClientNegozio.aggiornaStateTransazioneId(idTransazione);
                break;
            case CodiciStatoServer.FAIL_SESSION:
                System.out.println("Client riceve codice Status -1: Fallimento sessione");
                break;
            case CodiciStatoServer.FAIL_RIMUOVI_PRODOTTO:
                System.out.println("Client riceve codice Status -2: Fallimento rimozione prodotto dal Negozio");
                idTransazione = jsonNode.get("idTransazione").asInt();
                controllerClientNegozio.aggiornaStateTransazioneFail(idTransazione);
                break;
            case CodiciStatoServer.FAIL_AGGIUNGI_PRODOTTO:
                System.out.println("Client riceve codice Status -3: Fallimento aggiunta prodotto al Negozio");
                break;
            default:
                System.out.println("Errore: Codice di stato non valido.");
                break;
        }
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
        if (threadConnessione != null) {
            threadConnessione.setNewConnessione(true);
        }

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
                    Thread.sleep(5000);
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
                String color = "";
                switch (levelName) {
                    case "INFO":
                        color = "\u001B[34m"; // Blu
                        break;
                    case "WARNING":
                        color = "\u001B[33m"; // Giallo
                        break;
                    case "SEVERE":
                        color = "\u001B[31m"; // Rosso
                        break;
                }
                // Resetta il colore dopo il messaggio
                String resetColor = "\u001B[0m";

                // Formatta il messaggio con il colore
                return color + "[" + levelName + "] " + message + resetColor + "\n";
            }


        });
        logger.addHandler(handler);
    }

}
