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
int tryToRemoveProduct(int productId, int nToRemove){
    if (nToRemove < 1)
        return -1;
    int index = findProductToModify(productId);
    if(index == -1)
        return -1;
    customEnterCriticalSection();
    if (serverProductList[index].quantity >= nToRemove){
        serverProductList[index].quantity -= nToRemove;
        updateAllClients = 1;
        customLeaveCriticalSection();
        return 0;
    }else{
        customLeaveCriticalSection();
        return -1;
    }
}
int tryToAddProduct(int productId, int nToAdd){
    int index = findProductToModify(productId);
    if(index == -1)
        return -1;
    if (nToAdd < 1)
        return -1;
    customEnterCriticalSection();
    serverProductList[index].quantity += nToAdd;
    updateAllClients = 1;
    customLeaveCriticalSection();
    return 0;
}
int handleStatusCode2(int sock, char *buffer){
    struct jsonTransaction *transaction;
    transaction = getJsonTransaction(buffer);

    char prepString[strlen("{\"codiceStato\":xxxx,\"idTransazione\":xxxxxxxxx}\n")];
    if (transaction == NULL) {
        //If json is not formatted correctly
        timestamp();
        printf("X Socket %d sent an incorrect JSON", sock);
        return -1;
    }
    if(tryToRemoveProduct(transaction->productId, transaction->quantityToRemove) == 0){
        //JSON to send if modification is successful
        timestamp();
        printf("- Socket %d modified product %d", sock, transaction->productId);
        sprintf(prepString, "{\"codiceStato\":5,\"idTransazione\":%d}\n", transaction->transactionId);
    }else{
        //JSON to send if modification is NOT successful
        timestamp();
        printf("X Socket %d did not modify products", sock);
        sprintf(prepString, "{\"codiceStato\":-2,\"idTransazione\":%d}\n", transaction->transactionId);
    }

    free(transaction);
    transaction = NULL;

    memset(buffer, 0, BUFFER_SIZE);
    strcpy(buffer, prepString);

    if (sendToClient(sock, buffer) == -1){
        timestamp();
        printf("X Error sending to socket %d", sock);
    }
    return 0;
}
int handleStatusCode3(int sock, char *buffer){
    struct jsonTransaction *transaction;
    transaction = getJsonTransaction(buffer);

    char prepString[strlen("{\"codiceStato\":xxxx,\"idTransazione\":xxxxxxxxx}\n")];
    if (transaction == NULL) {
        //If json is not formatted correctly
        timestamp();
        printf("X Socket %d sent an incorrect JSON", sock);
        return -1;
    }
    if(tryToAddProduct(transaction->productId, transaction->quantityToRemove) == 0){
        //JSON to send if modification is successful
        timestamp();
        printf("- Socket %d modified product %d", sock, transaction->productId);
        sprintf(prepString, "{\"codiceStato\":6,\"idTransazione\":%d}\n", transaction->transactionId);
    }else{
        //JSON to send if modification is NOT successful
        timestamp();
        printf("X Socket %d did not modify products", sock);
        sprintf(prepString, "{\"codiceStato\":-3,\"idTransazione\":%d}\n", transaction->transactionId);
    }

    free(transaction);
    transaction = NULL;

    memset(buffer, 0, BUFFER_SIZE);
    strcpy(buffer, prepString);

    if (sendToClient(sock, buffer) == -1){
        timestamp();
        printf("X Error sending to socket %d", sock);
    }
    return 0;
}

int handleClient(int sock){
    char buffer[BUFFER_SIZE];
    char tmp[BUFFER_SIZE];
    int jsonRead = 0;
    int jsonlen=0;
    memset(buffer, 0, BUFFER_SIZE);

    //Receive from client
    while(jsonRead==0){
        int n = recv(sock, buffer, BUFFER_SIZE-1, 0);
        if (n <= 0){
            return -1;
        }

        for (int i = 0; i < n; ++i) {
            if (buffer[i] == '\r' && buffer[i+1] == '\n') {
                usleep(100*1000);//Sleep 0,1 second every transaction
                memset(tmp, 0, BUFFER_SIZE);
                strncpy(tmp,buffer+(i-jsonlen),jsonlen);
                jsonlen = 0;
                timestamp();
                printf("<- Handling json received from socket %d: %s",sock ,tmp);
                int jsonStatusCode = -1;
                int found = getJsonStatusCode(tmp, &jsonStatusCode);
                if (found == 0){
                    switch (jsonStatusCode) {
                        case 2://Modify a product
                            handleStatusCode2(sock,tmp);
                            break;
                        case 3:
                            handleStatusCode3(sock, tmp);
                            break;
                    }
                }else{
                    timestamp();
                    printf("X Socket %d sent an incorrect JSON", sock);
                }
                jsonRead = 1;
            }
            jsonlen++;
        }
    }
    return 0;
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

    setupNewClient(newsock);

    int res = 0;
    while(res >= 0){
        res = handleClient(newsock);
    }
    timestamp();
    printf("Closing socket %d\n", newsock);
    closeSocket(newsock);
    return 0;
}
