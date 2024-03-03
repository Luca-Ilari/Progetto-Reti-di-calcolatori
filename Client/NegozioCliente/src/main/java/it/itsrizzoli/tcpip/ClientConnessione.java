package it.itsrizzoli.tcpip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.itsrizzoli.tools.CodiciStatoServer;

import java.io.*;
import java.net.*;

public class ClientConnessione {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;


    public ClientConnessione(String serverAddress, int serverPort) {
        clientSocket = getClientSocket(serverAddress, serverPort);
        in = reader(clientSocket);
        out = writer(clientSocket);

        startResponseListener();
    }


    private Socket getClientSocket(String serverAddress, int serverPort) {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientSocket;
    }

    private static PrintWriter writer(Socket clientSocket) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    private static BufferedReader reader(Socket clientSocket) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }


    private void startResponseListener() {
        Thread responseThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println("-Server: " + response);
                    handleResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        responseThread.start();
    }

    private void handleResponse(String jsonResponse) {
        JsonNode jsonNode = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            System.out.println(" Attenzione: non è un json");
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
                break;
            case CodiciStatoServer.SUCCESSO_TRANSAZIONE:
                System.out.println("Client riceve codice Status 5 : Successo nella Transazione");
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



    public void closeResources() {
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
}
