#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#ifdef WIN32
#include <winsock2.h>
#include <ws2tcpip.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#endif

#include "headers/define.h"
#include "utils/handleJson.h"
#include "utils/customCriticalSection.h"

extern int nConnectedClient;
extern int updateAllClients;
extern int connectedSockets[MAX_CLIENT];

#define BUFFER_SIZE 1024

void timestamp()
{
    time_t now = time(NULL);
    struct tm *tm_struct = localtime(&now);
    printf("[%d:%d:%d] ",tm_struct->tm_hour,tm_struct->tm_min,tm_struct->tm_sec);
}

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
int handleClient(int sock){
    char buffer[BUFFER_SIZE];
    memset(buffer, 0, BUFFER_SIZE);

    //Receive from client
    int n = recv(sock, buffer, BUFFER_SIZE-1, 0);
    if (n <= 0){
        return -1;
    }
    timestamp();
    printf("<- Received from socket %d : %s",sock ,buffer);
    int jsonStatusCode = -1;
    int validate = validateJson(buffer, &jsonStatusCode);
    if (validate == 0){
        //printf("%llu", strlen(buffer));

        customEnterCriticalSection();

        //TODO Modify products
        //read json and modify product list
        //set buffer to status code succefull
        updateAllClients = 1;

        customLeaveCriticalSection();

        if (sendToClient(sock, buffer) == -1){
            timestamp();
            printf("X Error sending to socket %d", sock);
        }
    }
    return 0;
}

int sendProductListToClient(int sock){
    char *json = getProductJson();
    sendToClient(sock, json);
    free(json);
}

#ifdef WIN32
DWORD WINAPI ThreadFunc(void *newSockParam) {
#else
void *ThreadFunc(void *newSockParam){
#endif
    int newsock = *(int*)(newSockParam);

    customEnterCriticalSection();
    connectedSockets[nConnectedClient] = newsock;
    nConnectedClient += 1;
    customLeaveCriticalSection();

    sendToClient(newsock,"{\"codiceStato\":1}\n");

    sendProductListToClient(newsock);
    timestamp();
    printf("-> Sendind item list to socket: %d\n",newsock);

    int res = 0;
    while(res >= 0){
        res = handleClient(newsock);
    }
    timestamp();
    printf("Closing socket %d\n", newsock);

    //TODO remove socket from list of connected sockets
    int indexSocketToRemove;
    for (int i = 0; i < nConnectedClient; i++) {
        if (connectedSockets[i] == newsock) {
            indexSocketToRemove = i;
            break;
        }
    }
    customEnterCriticalSection();
    for(int i = indexSocketToRemove; i < MAX_CLIENT-1 ; i++)
    {
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

