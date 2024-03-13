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

#include "include/sockets/server.h"
#include "include/define.h"
#include "include/product.h"
#include "include/sockets/handleUpdateClients.h"

int updateAllClients = 0;
int nConnectedClient = 0;
int connectedSockets[MAX_CLIENT];

struct product serverProductList[PRODUCT_NUMBER] = {
        {0, "Pane", 100000, (float)2.99},
        {1, "Acqua", 50000, (float)1},
        {2, "Vino", 50000, (float)20},
        {3, "Birra", 900, (float)2},
        {4, "Patatine", 7000, (float)2}
};

#ifdef WIN32
CRITICAL_SECTION CriticalSection;
#else
pthread_mutex_t CriticalSection;
#endif

int main(int argc, char* argv[]){
    if (argc < 2){
        timestamp();
        printf("Can't start server. Please specify port number\n");
        return -1;
    }
    timestamp();
    printf("Starting sever on port %s\n", argv[1]);
    
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
        perror("ERROR on binding");
        return 0;
    }
    timestamp();
    printf("Server started\n");

    #ifdef WIN32
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
    #ifdef WIN32
    closesocket(sockfd);
    #else
    close(sockfd);
    #endif
}
