#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#ifdef WIN32
#include <winsock2.h>
#elif __APPLE__
#include <string.h>
#include <sys/socket.h>
#endif

#include "./socketFunctions.h"
#include "../define.h"
#include "../product.h"
#include "../utils/handleJson.h"
#include "../utils/customCriticalSection.h"
#include "../utils/timeStamp.h"

extern struct product *serverProductList;
extern int PRODUCT_NUMBER;
extern int nConnectedClient;
extern int updateAllClients;
extern int connectedSockets[MAX_CLIENT];


/**@return index of the the array serverProductList where productId == serverProductList.id
 * @return -1 if the product id is not in the array*/
int findProductToModify(int productId){
    for (int i = 0; i < PRODUCT_NUMBER; ++i) {
        if (productId == serverProductList[i].id) {
            return i;
        }
    }
    return -1;
}

int tryToRemoveProduct(int productId, int nToRemove, int *clientOrderedProducts){
    int index = findProductToModify(productId);
    if (nToRemove < 1 || index == -1)
        return -1;
    
    customEnterCriticalSection();
    
    if (serverProductList[index].quantity >= nToRemove){
        serverProductList[index].quantity -= nToRemove;
        clientOrderedProducts[index] += nToRemove;

        updateAllClients = 1;
    }else{
        customLeaveCriticalSection();
        return -1;
    }
    customLeaveCriticalSection();
    return 0;
}

int tryToAddProduct(int productId, int nToAdd, int *clientOrderedProducts){
    int index = findProductToModify(productId);
    if(index == -1 || nToAdd < 1)
        return -1;
    
    customEnterCriticalSection();
    if(clientOrderedProducts[index] >= nToAdd){//if the client is trying to add product that he previusly removed
        serverProductList[index].quantity += nToAdd;
        clientOrderedProducts[index] -= nToAdd;
        updateAllClients = 1;
    }else{
        customLeaveCriticalSection();
        return -1;
    }
    customLeaveCriticalSection();
    return 0;
}
int handleStatusCode2(int sock, char *buffer, int *clientOrderedProducts){
    struct jsonTransaction *transaction;
    char response[strlen("{\"codiceStato\":xxxx,\"idTransazione\":xxxxxxxxx}\n")];
    
    transaction = getJsonTransaction(buffer);
    if (transaction == NULL) {
        //If json is not formatted correctly
        timestamp();
        printf("X Socket %d sent an incorrect JSON", sock);
        return -1;
    }
    if(tryToRemoveProduct(transaction->productId, transaction->quantity, clientOrderedProducts) == 0){
        //JSON to send if modification is successful
        timestamp();
        printf("- Socket %d modified product %d", sock, transaction->productId);
        
        sprintf(response, "{\"codiceStato\":5,\"idTransazione\":%d}\n", transaction->transactionId);
    }else{
        //JSON to send if modification is NOT successful
        timestamp();
        printf("X Socket %d did not modify products", sock);
        sprintf(response, "{\"codiceStato\":-2,\"idTransazione\":%d}\n", transaction->transactionId);
    }

    free(transaction);
    transaction = NULL;

    if (sendToClient(sock, response) == -1){
        timestamp();
        printf("X Error sending to socket %d", sock);
    }
    return 0;
}
int handleStatusCode3(int sock, char *buffer, int *clientOrderedProducts){
    struct jsonTransaction *transaction;
    char response[strlen("{\"codiceStato\":xxxx,\"idTransazione\":xxxxxxxxx}\n")];

    transaction = getJsonTransaction(buffer);
    if (transaction == NULL) {
        //If json is not formatted correctly
        timestamp();
        printf("X Socket %d sent an incorrect JSON", sock);
        return -1;
    }
    if(tryToAddProduct(transaction->productId, transaction->quantity, clientOrderedProducts) == 0){
        //JSON to send if modification is successful
        
        timestamp();
        printf("- Socket %d modified product %d", sock, transaction->productId);
        sprintf(response, "{\"codiceStato\":6,\"idTransazione\":%d}\n", transaction->transactionId);
    }else{
        //JSON to send if modification is NOT successful
        timestamp();
        printf("X Socket %d did not modify products", sock);
        sprintf(response, "{\"codiceStato\":-3,\"idTransazione\":%d}\n", transaction->transactionId);
    }

    free(transaction);
    transaction = NULL;

    if (sendToClient(sock, response) == -1){
        timestamp();
        printf("X Error sending to socket %d", sock);
    }
    return 0;
}

void executeJsonOperation(int jsonStatusCode, int sock, char *json, int *clientOrderedProducts) {
    switch (jsonStatusCode) {//Handle different json status code
        case 2://Modify a product
            handleStatusCode2(sock, json, clientOrderedProducts);
            break;
        case 3:
            handleStatusCode3(sock, json, clientOrderedProducts);
            break;
    }
}

//**This function receives the JSON sent by the client, decodes it and sends the response to the client
// if the client sends more than 1 json this function splits them and handles them one by one
// Every json that the client sends terminate with |  */
void handleClient(int sock, int *clientOrderedProducts){
    char buffer[BUFFER_SIZE];
    char currentJson[BUFFER_SIZE];
    int jsonlen = 0;

    while(1){
        memset(buffer, 0, BUFFER_SIZE);
        int n = recv(sock, buffer, BUFFER_SIZE-1, 0);
        if (n <= 0){
            return;
        }

        int i = 0;
        while (buffer[i] != '\0'){
            char currentChar =  buffer[i];
            if (currentChar == '|'){ // if it's the end of the json
                memset(currentJson, 0, BUFFER_SIZE);
                strncpy(currentJson, buffer + (i - jsonlen), jsonlen);  //Copy the json in the buffer to currentJson. If there is more than a json in the buffer it copies only the current one
                i++;
                for (int j = 0; j < 5; ++j) {
                j = j;
                for (int b = 0; b < 100000000; ++b) {
//
                   }
                }
                timestamp();
                printf("<- Handling json received from socket %d: %s", sock , currentJson);
                int jsonStatusCode = -1;
                int found = getJsonStatusCode(currentJson, &jsonStatusCode);
                if (found == 0){
                    executeJsonOperation(jsonStatusCode, sock, currentJson, clientOrderedProducts);
                }else{
                    timestamp();
                    printf("X Socket %d sent an incorrect JSON", sock);
                }
                jsonlen=0; //reset length to start again with the next json
            }else if(currentChar != '\n'){
                jsonlen++;
            }
            i++;
        }
    }
}

int setupNewClient(int newsock){
    customEnterCriticalSection();
    connectedSockets[nConnectedClient] = newsock;
    nConnectedClient += 1;
    customLeaveCriticalSection();

    int res = sendToClient(newsock,"{\"codiceStato\":1}\n");
    if (res == -1){
        return -1;
    }
    sendProductListToClient(newsock);
    return 0;
}

void closeSocket(int newsock){
    int indexSocketToRemove;

    //Remove the disconnected socket from socket list
    for (int i = 0; i < nConnectedClient; ++i) {
        if (connectedSockets[i] == newsock) {
            indexSocketToRemove = i;
            break;
        }
    }
    customEnterCriticalSection();
    for(int i = indexSocketToRemove; i < MAX_CLIENT-1 ; i++){
        connectedSockets[i] = connectedSockets[i + 1];
    }
    nConnectedClient -= 1;
    customLeaveCriticalSection();
    #ifdef WIN32
    closesocket(newsock);
    #else
    close(newsock);
    #endif
}

#ifdef WIN32
DWORD WINAPI handleNewClient(void *newSockParam) {
#else
void *handleNewClient(void *newSockParam){
#endif
    int newsock = *(int*)(newSockParam);
    //array to track the number of product removed by the client
    int clientOrderedProducts[PRODUCT_NUMBER];
    
    setupNewClient(newsock);

    handleClient(newsock,clientOrderedProducts);

    timestamp();
    printf("Closing socket %d\n", newsock);
    closeSocket(newsock);
    return 0;
}
