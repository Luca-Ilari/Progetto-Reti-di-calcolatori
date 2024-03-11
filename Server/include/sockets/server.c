#include <stdio.h>
#include <stdlib.h>

#ifdef WIN32
#include <winsock2.h>
#include <ws2tcpip.h>
#include <unistd.h>

#else
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#endif

#include "../define.h"
#include "../product.h"
#include "../utils/handleJson.h"
#include "../utils/customCriticalSection.h"
#include "../utils/timeStamp.h"

extern struct product serverProductList[];
extern int nConnectedClient;
extern int updateAllClients;
extern int connectedSockets[MAX_CLIENT];

#define BUFFER_SIZE 1024

int setupSocket(int argc){
    if (argc < 2) {
        timestamp();
        perror("ERROR, no port provided\n");
    }
#ifdef WIN32
    WSADATA info;
    if (WSAStartup(MAKEWORD(1, 1), &info) == SOCKET_ERROR) {
        timestamp();
        perror("ERROR, can't start socket\n");
    }
#endif
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0){
        timestamp();
        perror("ERROR opening socket\n");
    }
    return sockfd;
}

int acceptNewConnection(int sockfd){
    struct sockaddr_in cli_addr;
    listen(sockfd, 2);
    socklen_t clientLenght = sizeof(cli_addr);
    int newsocket = accept(sockfd, (struct sockaddr*)&cli_addr, &clientLenght);

    if (newsocket < 0){
        return -1;
    }
    char *sockIp= inet_ntoa(cli_addr.sin_addr);

    timestamp();
    printf("+ New socket %d connected from: %s\n", newsocket, sockIp);
    return newsocket;
}
int sendToClient(int sock, char *buffer) {
    int n = send(sock, buffer, strlen(buffer), 0);
    if (n < 0){
        return -1;
    }
//    timestamp();
//    printf("-> Sendind to socket %d : %s",sock ,buffer);

    return 0;
}

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

int handleClient(int sock){
    char buffer[BUFFER_SIZE];
    memset(buffer, 0, BUFFER_SIZE);

    //Receive from client
    int n = recv(sock, buffer, BUFFER_SIZE-1, 0);
    if (n <= 0){
        return -1;
    }
    timestamp();
    printf("<- Received from socket %d: %s",sock ,buffer);

    int jsonStatusCode = -1;
    int validate = getJsonStatusCode(buffer, &jsonStatusCode);
    struct jsonTransaction *transaction;
    if (validate == 0){
        switch (jsonStatusCode) {
            case 2://Modify a product
                transaction = getJsonTransaction(buffer);
                char prepString[strlen("{\"codiceStato\":xxxxx,\"idTransazione\":xxxxxx}\n")];
                if (transaction == NULL) {
                    //JSON to send if modification is NOT successful
                    timestamp();
                    printf("X Socket %d sent a incorrect JSON\n", sock);
                    return 0;
                }
                if(tryToRemoveProduct(transaction->productId, transaction->quantityToRemove) == 0){
                    //JSON to send if modification is successful
                    timestamp();
                    printf("- Socket %d modified product %d\n", sock, transaction->productId);
                    sprintf(prepString, "{\"codiceStato\":5,\"idTransazione\":%d}\n", transaction->transactionId);
                }else{
                    //JSON to send if modification is NOT successful
                    timestamp();
                    printf("X Socket %d did not modify products\n", sock);
                    sprintf(prepString, "{\"codiceStato\":-2,\"idTransazione\":%d}\n", transaction->transactionId);
                }

                free(transaction);

                memset(buffer, 0, BUFFER_SIZE);
                strcpy(buffer, prepString);

                if (sendToClient(sock, buffer) == -1){
                    timestamp();
                    printf("X Error sending to socket %d", sock);
                }
                break;
        }
    }
    return 0;
}

void sendProductListToClient(int sock){
    char *json = getProductJson();
    sendToClient(sock, json);
    free(json);
    json = NULL;
}

#ifdef WIN32
DWORD WINAPI handleNewClient(void *newSockParam) {
#else
void *handleNewClient(void *newSockParam){
#endif
    int newsock = *(int*)(newSockParam);

    customEnterCriticalSection();
    connectedSockets[nConnectedClient] = newsock;
    nConnectedClient += 1;
    customLeaveCriticalSection();

    sendToClient(newsock,"{\"codiceStato\":1}\n");

    sendProductListToClient(newsock);
    timestamp();
    printf("-> Sending item list to socket: %d\n",newsock);

    int res = 0;
    while(res >= 0){
        res = handleClient(newsock);
    }
    timestamp();
    printf("Closing socket %d\n", newsock);

    int indexSocketToRemove;
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
    return 0;
}