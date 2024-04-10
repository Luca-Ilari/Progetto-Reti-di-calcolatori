package it.itsrizzoli.tcpip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.itsrizzoli.controller.ControllerClientNegozio;
import it.itsrizzoli.model.Prodotto;
import it.itsrizzoli.model.Transazione;
import it.itsrizzoli.tools.CodiciStatoServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GestioneComunicazioneServerTest {

    @Test
    void gestisciJsonCodiceStato() {
        String jsonResponse = null;
        //Entrambi null
        assertDoesNotThrow(() -> GestioneComunicazioneServer.gestisciJsonCodiceStato(null, null));

        //Solamente uno null
        assertDoesNotThrow(() -> GestioneComunicazioneServer.gestisciJsonCodiceStato(" ", null));

        assertDoesNotThrow(() -> {
            ControllerClientNegozio controllerClientNegozio = new ControllerClientNegozio();
            GestioneComunicazioneServer.gestisciJsonCodiceStato(" ", controllerClientNegozio);
        });

    }

    @Test
    void gestisciJsonCodiceStatoJsonErratto() {
        String jsonResponse = "{\"codiceStato\": \"322\", \"prodotti\": [{\"id\": 1, \"nome\": \"Prodotto1\"}]}";

        ControllerClientNegozio controllerClientNegozio = new ControllerClientNegozio();
        assertDoesNotThrow(() -> GestioneComunicazioneServer.gestisciJsonCodiceStato(jsonResponse,
                controllerClientNegozio));

        List<Prodotto> listaProdotti = controllerClientNegozio.getProdottiNegozio();

        assertNotNull(listaProdotti);
        assertEquals(0, listaProdotti.size());
    }

    @Test
    void recuperoListaProdottiJson() {
        List<Prodotto> prodotti = creaListaProdottiTest();
        String jsonResponse = assertDoesNotThrow(() -> serializzaLista(prodotti));
        assertNotNull(jsonResponse);

        List<Prodotto> listaProdotti = assertDoesNotThrow(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return GestioneComunicazioneServer.recuperoListaProdottiJson(jsonNode);
        });

        assertNotNull(listaProdotti);
        assertEquals(prodotti.size(), listaProdotti.size());
    }


    private static List<Prodotto> creaListaProdottiTest() {
        List<Prodotto> prodotti = new ArrayList<>();

        // Aggiunta di prodotti alla lista
        prodotti.add(new Prodotto(0, "Pane", 1.05, 96));
        prodotti.add(new Prodotto(1, "Latte", 1.50, 30));
        prodotti.add(new Prodotto(2, "Uova", 2.20, 20));
        prodotti.add(new Prodotto(3, "Biscotti", 1.80, 50));
        return prodotti;
    }

    private static String serializzaLista(List<Prodotto> prodotti) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("codiceStato", CodiciStatoServer.LISTA_PRODOTTI_AGGIORNATO);
        ArrayNode arrayNode = objectMapper.valueToTree(prodotti);
        objectNode.set("prodotti", arrayNode);

        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
        assertNotNull(jsonResponse);

        return jsonResponse;
    }

    @Test
    void serializzaTransazione() {
        Transazione transazione = new Transazione(-1, -1);
        assertDoesNotThrow(() -> GestioneComunicazioneServer.serializzaTransazione(transazione,
                CodiciStatoServer.AGGIUNGI_PRODOTTO));
    }

    @Test
    void inviaTransazioneSingola() {
        Transazione transazione = new Transazione(-1, -1);
        assertDoesNotThrow(() -> GestioneComunicazioneServer.inviaTransazioneSingola(transazione,
                CodiciStatoServer.AGGIUNGI_PRODOTTO, null));
    }
}