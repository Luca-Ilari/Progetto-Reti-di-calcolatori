#include <stdio.h>
#include <stdlib.h>

#ifdef win32
#include <winsock2.h>
#include <windows.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#endif

#include "headers/server.h"
#include "headers/define.h"
#include "headers/product.h"
#include "threadParameter.h"

struct product serverProductList[PRODUCT_NUMBER] = {
        {0, "Pane", 10, 2.99},
        {1, "Acqua", 5, 1}
};

#ifdef win32
CRITICAL_SECTION CriticalSection;
#else
pthread_mutex_t CriticalSection;
#endif

int main(int argc, char* argv[]){
    timestamp();
    printf("Starting sever on port %s\n", argv[1]);
    
    int sockfd, portno;
    struct sockaddr_in serv_addr;
    portno = atoi(argv[1]);
    
    sockfd = setupSocket(argc);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);
    
#ifdef win32
    InitializeCriticalSection(&CriticalSection);
#else
    
#endif
    
    if (bind(sockfd, (struct sockaddr*)&serv_addr,sizeof(serv_addr)) < 0){
        timestamp();
        perror("ERROR on binding");
        return 0;
    }
    timestamp();
    printf("Server started\n");
    
    while(1) {
        memset(&serv_addr, 0, sizeof(serv_addr));
        int newsockfd = acceptNewConnection(sockfd);
        
        if (newsockfd > 0) {	
            struct threadParamStruct params;
            params.newsockfd = newsockfd;
            
#ifdef win32
            CreateThread(NULL, 0, ThreadFunc, &params, 0, NULL);
#else
            pthread_t thread_id = 0;
            pthread_create(&thread_id,NULL,ThreadFunc, &params);
#endif
        }else{
            timestamp();
            perror("ERROR while accepting new client");
        }
    }
#ifdef win32
    closesocket(sockfd);
#else
    close(sockfd);
#endif
}