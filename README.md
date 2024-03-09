# Progetto per reti di calcolatori
Creato da:
- [Teshale Cella](https://github.com/TTTT-san)
- [Luca Ilari](https://github.com/Luca-Ilari)
  
> [!NOTE]
> Il progetto non è ancora finito quindi alcune funzionalità spiegate prossimamente non sono ancora presenti.
## Descrizione
Il progetto consite in un server multi-threaded che gestisce parallelamente le connesioni e le richieste dei client.

Tutti i messaggi che il server e il client si scambiano sono json con dentro un codice che identifica il tipo di messaggio che si stà mandando. Per dettagli ulteriori andare alla sezione [Json](#json-e-codici-di-stato).

Il server ha una lista di prodotti con le loro proprietà (nome, prezzo, quantità) che manda a tutti i client che si connettono.

I client una volta che sono connessi e hanno la lista dei prodotti, possono iniziare ad inviare delle richieste al server di "acquisto"; ovvero possono mandare una richiesta al server di descrementare un elemento specifico come se lo avessero acquitasto.

Il server una volta ricevuta la richiesta verificherà la sua validità e quindi decrementerà il prodotto specificato dal client.

Ogni volta che un client connesso al server modifica la lista dei prodotti, il server manderà la lista aggiornata a tutti i client connessi in modo da avere sempre i client aggiornati.

Se la richiesta non è valida, quindi per esempio il client chiede di rimuovere troppi prodotti rispetto a quelli che ci sono nella lista, il server risponderà al client con un json con codiceStato -2.

### Caratteristiche Server
Il server è una applicazione da linea di comando. Quando viene avviata bisogna specificare la porta su cui il server ascolterà nuove connessioni.
Se la porta non verrà specificata il server non si avvierà.


Il server può essere compilato sia per window che per linux, infatti negli esempi che dopo verranno illustrati, il server è fatto girare su una vps Ubuntu.

### Caratteristiche Client
I client sono scritti in java e sono 2:
#### Producer
TODO
#### Consumer
TODO

## Esempi di utilizzo dell'applicazione
```mermaid
sequenceDiagram
Client->>+ Server: Connessione porta: 5555
Server->>+ Client: Risponde json connessione
Server->>+ Client: Manda json prodotti
```
## JSON e codici di stato
| codiceStato | Descrizione |
| ----- | ------------- |
| 1 | START_SESSION |
| 2 | RIMUOVI_PRODOTTO |
| 3 | AGGIUNGI_PRODOTTO |
| 4 | LISTA_PRODOTTI_AGGIORNATO |
| 5 | SUCCESSO_TRANSAZIONE |
| -1 | FAIL_SESSION |
| -2 | FAIL_RIMUOVI_PRODOTTO |
| -3 | FAIL_AGGIUNGI_PRODOTTO |

Esempio json che il server manda ai client quando la lista dei prodotti deve essere aggiornata
```json
{
   "codiceStato":4,
   "prodotti":[
      {
         "Id":0,
         "nome":"”Pane”",
         "prezzo":1.05,
         "quantitaDisponibile":96
      },
      {
        .... 
      }
   ]
}
```
