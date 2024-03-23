#include <stdio.h>
#include <stdlib.h>
#ifdef WIN32
#include <winsock2.h>
#include <windows.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>
#endif

#include "./include/define.h"
#include "./include/product.h"
#include "./include/utils/timeStamp.h"
#include "./include/utils/loadFile.h"
#include "./include/sockets/handleClient.h"
#include "./include/sockets/socketFunctions.h"
#include "./include/sockets/handleUpdateClients.h"
#include "include/utils/handleJson.h"

#ifdef WIN32
CRITICAL_SECTION CriticalSection;
#else
pthread_mutex_t CriticalSection;
#endif

int updateAllClients = 0;
int nConnectedClient = 0;
int connectedSockets[MAX_CLIENT];

struct product *serverProductList;
int PRODUCT_NUMBER = 0;

DWORD WINAPI webServer(){
    int sockfd, portno;
    char buffer[1024];
    struct sockaddr_in serv_addr;
    portno = 80;

#ifdef WIN32
    WSADATA info;
    if (WSAStartup(MAKEWORD(1, 1), &info) == SOCKET_ERROR) {
        timestamp();
        perror("ERROR, can't start socket\n");
    }
#endif
    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0){
        timestamp();
        perror("ERROR opening socket\n");
    }
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);
    if (bind(sockfd, (struct sockaddr*)&serv_addr,sizeof(serv_addr)) < 0){
        perror("\nERROR on binding");
        return 0;
    }
    for(;;){
    memset(&serv_addr, 0, sizeof(serv_addr));
    int newsockfd = acceptNewConnection(sockfd);
    memset(&buffer, 0, sizeof(buffer));
    recv(newsockfd,buffer,1024-1,0);

    memset(&buffer, 0, sizeof(buffer));
    char *a = getProductJson();
    sprintf(buffer,"\"HTTP/1.1 200 OK\r\nContent-Length: 1024\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE html><script> function autoRefresh() { window.location = window.location.href; } setInterval('autoRefresh()', 2000); </script>%s", a);
    sendToClient(newsockfd, buffer);
    free(a);
    a = NULL;
    closesocket(newsockfd);
    }
    closesocket(sockfd);
}

int main(int argc, char* argv[]){
    if (argc < 2){
        timestamp();
        printf("Can't start server. Please specify port number\n");
        return -1;
    }

    readProductsFromFile("./products.csv");
    
    timestamp();
    printf("Starting sever on port %s", argv[1]);
    
    int sockfd, portno;
    struct sockaddr_in serv_addr;
    portno = atoi(argv[1]);

    sockfd = setupSocket(argc);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);

    #ifdef WIN32
    InitializeCriticalSection(&CriticalSection);
    #endif
    
    if (bind(sockfd, (struct sockaddr*)&serv_addr,sizeof(serv_addr)) < 0){
        perror("\nERROR on binding");
        return 0;
    }
    timestamp();
    printf("Server started\n");

    #ifdef WIN32
    CreateThread(NULL, 0, webServer, NULL, 0, NULL);

    CreateThread(NULL, 0, handleUpdateClients, NULL, 0, NULL);
    #else
    pthread_t thread_id = 0;
    pthread_create(&thread_id,NULL,handleUpdateClients, NULL);
    #endif

    while(1) {
        if (nConnectedClient < MAX_CLIENT){
            memset(&serv_addr, 0, sizeof(serv_addr));
            int newsockfd = acceptNewConnection(sockfd);

            if (newsockfd > 0) {
                int *param = &newsockfd;
                #ifdef WIN32
                CreateThread(NULL, 0, handleNewClient, param, 0, NULL);
                #else
                pthread_create(&thread_id, NULL, handleNewClient, param);
                #endif
            }else{
                timestamp();
                perror("ERROR while accepting new client\n");
            }
        }
    }
    free(serverProductList);
    #ifdef WIN32
    closesocket(sockfd);
    #else
    close(sockfd);
    #endif
}
