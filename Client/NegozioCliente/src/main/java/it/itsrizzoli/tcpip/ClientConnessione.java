package it.itsrizzoli.tcpip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.itsrizzoli.App;
import it.itsrizzoli.modelli.Prodotto;
import it.itsrizzoli.modelli.Transazione;
import it.itsrizzoli.tools.CodiciStatoServer;
import it.itsrizzoli.ui.NegozioClientUI;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.itsrizzoli.tools.TypeThread.*;

public class ClientConnessione {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5555;
    final List<Transazione> listaTransazioniRandom = new ArrayList<>();


    public ClientConnessione() {
        ThreadClient threadClientConnessione = new ThreadClient(this, THREAD_CONNESSIONE);
        Thread threadConnessione = new Thread(threadClientConnessione);
        threadConnessione.start();
    }

    protected boolean readLoop() {
        String risposta;
        try {
            while ((risposta = in.readLine()) != null) {
                System.out.println(" -Server: " + risposta);
                gestioneJsonCodiceStato(risposta);

                if (!listaTransazioniRandom.isEmpty()) {
                    ThreadClient threadWriting = new ThreadClient(this, THREAD_WRITE);
                    Thread thread = new Thread(threadWriting);
                    thread.start();
                }

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return true;

    }

    protected void writeTransazioniJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        for (Transazione transazione : listaTransazioniRandom) {
            try {
                String jsonString = getJsonTransazione(transazione, objectMapper);
                out.println(jsonString);// Invia la transazione al server
                System.out.println("Transazione inviata al server.");

            } catch (JsonProcessingException e) {
                System.err.println("Errore durante la conversione in JSON");
            }

            try {
                // Attendere 5 secondi prima di inviare la prossima transazione
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interruzione durante l'attesa", e);
            }
        }
        listaTransazioniRandom.clear();
    }

    protected void writeTransazioniJson(NegozioClientUI negozioClientUI) {
        ObjectMapper objectMapper = new ObjectMapper();

        for (Transazione transazione : listaTransazioniRandom) {
            try {
                String jsonString = getJsonTransazione(transazione, objectMapper);
                out.println(jsonString);// Invia la transazione al server
                System.out.println("Transazione inviata al server.");

                negozioClientUI.addSingleTransazioneAwait(transazione);

            } catch (JsonProcessingException e) {
                System.err.println("Errore durante la conversione in JSON");
            }

            try {
                // Attendere 5 secondi prima di inviare la prossima transazione
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interruzione durante l'attesa", e);
            }
        }
        listaTransazioniRandom.clear();
    }

    private static String getJsonTransazione(Transazione transazione, ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("codiceStato", CodiciStatoServer.AGGIUNGI_PRODOTTO);

        String transazioneNode = objectMapper.writeValueAsString(transazione);

        objectNode.put("transazione", transazioneNode);

        String jsonString = objectMapper.writeValueAsString(objectNode);

        System.out.println("Transazione JSON: " + jsonString);
        return jsonString;
    }

    private void gestioneJsonCodiceStato(String jsonResponse) {
        JsonNode jsonNode = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            System.err.println(" Attenzione: non è un json");
            return;
        }


        int codeStatus = jsonNode.get("codiceStato").asInt();
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

                //aggiorna negozio prodotti UI
                App.negozioClientUI.aggiornaProdottiNegozio(listaProdotti);

                // creazione transazioni in maniera rando
                synchronized (listaTransazioniRandom) {
                    listaTransazioniRandom.addAll(Transazione.creaListaTransazioniRandom(listaProdotti));
                }
                App.negozioClientUI.getListaTransazione().addAll(listaTransazioniRandom);
                //App.negozioClientUI.addTransazioneAwait();

                break;
            case CodiciStatoServer.SUCCESSO_TRANSAZIONE:
                System.out.println("Client riceve codice Status 5 : Successo nella Transazione");
                int idTransazione = jsonNode.get("idTransazione").asInt();
                App.negozioClientUI.aggiornaStateTransazione(idTransazione);
                App.negozioClientUI.allSetResponsiveTable();
                break;
            case CodiciStatoServer.FAIL_SESSION:
                System.out.println("Client riceve codice Status -1: Fallimento sessione");
                break;
            case CodiciStatoServer.FAIL_RIMUOVI_PRODOTTO:
                System.out.println("Client riceve codice Status -2: Fallimento rimozione prodotto dal Negozio");
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
        System.out.println("Client riceve codice Status 4: Lista prodotti aggiornata");
        JsonNode prodottiNode = jsonNode.get("prodotti");
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
            e.printStackTrace();
        }
    }

    void tentaConnessione() {
        boolean connected = false;
        while (!connected) {
            try {
                connessioneAlServer();
                connected = true;
            } catch (IOException e) {
                System.out.println("Errore: Connessione rifiutata!!");
                System.out.println("Tentativo di riconnessione...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    // Gestisci l'eccezione se il thread viene interrotto durante il sonno
                    ex.printStackTrace();
                }
            }
        }
    }

    private void connessioneAlServer() throws IOException {
        clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("Successo: Connessione avvenuta al server con IP: " + SERVER_ADDRESS + " - PORTA: " + SERVER_PORT);

    }
}
