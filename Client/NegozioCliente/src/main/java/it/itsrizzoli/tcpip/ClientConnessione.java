package it.itsrizzoli.tcpip;

import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tools.GestioneComunicazioneServer;

import java.io.*;
import java.net.*;
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
    private final static Logger logger = Logger.getLogger("AvvisiClientConnessione");
    private boolean isStopThreadConnessione;
    private ControllerClientNegozio controllerClientNegozio;

    public ClientConnessione() {
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
        GestioneComunicazioneServer.setControllerClientNegozio(controllerClientNegozio);
    }

    public void aggiornaStato(boolean stato) {
        this.onConnessione = stato;
        this.controllerClientNegozio.aggiornaStatoConnessione(stato);

    }

    public void startConnessione() {
        isStopThreadConnessione = false;
        Thread threadConnessione = new Thread(() -> {
            while (!isStopThreadConnessione) {
                System.out.println("Thread di tentativo di connessione avviato.");
                try {
                    tentaConnessione();
                    aggiornaStato(true);
                    readLoop();
                    gestisciDisconnessioneServerConErrore();
                } catch (Exception e) {
                    System.err.println("Errore durante la connessione: " + e.getMessage());
                }

                controllerClientNegozio.azzeraDatiNegozio();

            }
        });
        threadConnessione.start();
    }

    public void aggiornaIP(String newServerAddress, int newServerPort) {
        try {
            // Chiudi l'input/output attuale
            chiusuraConnessione();

            // Aggiorna l'indirizzo IP e la porta
            this.serverAddress = newServerAddress;
            this.serverPort = newServerPort;

            logger.info(String.format("Connessione riuscita al nuovo indirizzo IP: %s, porta: %d", newServerAddress,
                    newServerPort));
        } catch (Exception e) {
            logger.severe("Impossibile connettersi al nuovo indirizzo IP e porta: " + newServerAddress + ":" + newServerPort);
            System.out.println(e.getMessage());
        }
    }

    private void chiusuraConnessione() {
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

    private void tentaConnessione() {
        boolean connected = false;
        while (!connected) {
            try {
                connessioneAlServer();
                connected = true;
            } catch (IOException e) {
                logger.severe("Connessione rifiutata!!\n\n");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    // Gestisci l'eccezione se il thread viene interrotto durante il sonno
                    logger.severe(e.getMessage());
                }
                logger.info("Tentativo di riconnessione...");

            }
        }
        logger.warning("Thread di connessione completato.\n---- SESSIONE AVVIATA ----");

    }

    private void connessioneAlServer() throws IOException {
        chiusuraConnessione();
        clientSocket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        logger.info("Successo: Connessione avvenuta al server con IP: " + serverAddress + " - PORTA: " + serverPort);

    }

    private void gestisciDisconnessioneServerConErrore() {
        aggiornaStato(false);
        controllerClientNegozio.aggiornaStatoTransazioneServerError();
        // controllerClientNegozio.azzeraDati();
    }


    private void readLoop() {
        String message;
        logger.info(" Thread connessione diveenta di lettura loop.");
        try {
            String risposta;
            while ((risposta = in.readLine()) != null) {
                logger.info(" - Server: " + risposta);
                GestioneComunicazioneServer.gestisciJsonCodiceStato(risposta, controllerClientNegozio);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        message = "Thread di lettura completato.\n---- SESSIONE TERMINATA ----";
        logger.warning(message);

    }

    public void inviaTransazioneSingola(Transazione transazione, int CODICE_STATO) {
        GestioneComunicazioneServer.inviaTransazioneSingola(transazione, CODICE_STATO, out);
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
