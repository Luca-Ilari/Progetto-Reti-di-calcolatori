#include <cstdio>
#include <winsock2.h>
#include <windows.h>
#include <thread>
#include "server.h"

#include "threadParameter.h"
int globalVar = 100000;
CRITICAL_SECTION CriticalSection;


int main(int argc, char* argv[]){
    timestamp();
    printf("Starting sever....\n");
    int sockfd, portno;
    struct sockaddr_in serv_addr;
    portno = atoi(argv[1]);

    sockfd = setupSocket(argc);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);

    InitializeCriticalSection(&CriticalSection);

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
            params.globalVar = &globalVar;
            CreateThread(NULL, 0, ThreadFunc, &params, 0, NULL);
        }else{
            timestamp();
            perror("ERROR while accepting new client");
        }
    }
    closesocket(sockfd);
}