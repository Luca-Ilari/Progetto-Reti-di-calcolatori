#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <winsock2.h>
#include <windows.h>
#include <ws2tcpip.h>
#include "threadParameter.h"
extern CRITICAL_SECTION CriticalSection;
#define BUFFER_SIZE 256

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
    WSADATA info;
    if (WSAStartup(MAKEWORD(1, 1), &info) == SOCKET_ERROR) {
        timestamp();
        perror("ERROR, can't start socket\n");
    }
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0){
        timestamp();
        perror("ERROR opening socket\n");
    }
    return sockfd;
}

int acceptNewConnection(int sockfd){
    struct sockaddr_in cli_addr;
    listen(sockfd, 5);
    socklen_t clientLenght = sizeof(cli_addr);
    int newsocket = accept(sockfd, (struct sockaddr*)&cli_addr, &clientLenght);

    if (newsocket < 0){
        return -1;
    }
    char *sockIp= inet_ntoa(cli_addr.sin_addr);
    timestamp();
    printf("New socket %d connected from: %s\n", newsocket, sockIp);
    return newsocket;
}
int sendToClient(int sock, char *buffer) {
    int n = send(sock, buffer, strlen(buffer), 0);
    if (n < 0){
        perror("ERROR writing to socket");
        return -1;
    }
    return 0;
}
int handleClient(int sock, int *varToIncrement){
    char buffer[BUFFER_SIZE];
    memset(buffer, 0, BUFFER_SIZE);
    int n = recv(sock, buffer, BUFFER_SIZE-1, 0);
    if (n <= 0){
        return -1;
    }
    timestamp();

    printf("Socket %d sent: %s",sock ,buffer);
    //memset(buffer,0,BUFFER_SIZE);

    EnterCriticalSection(&CriticalSection);

    sendToClient(sock, buffer);

   // n = recv(sock, buffer, BUFFER_SIZE-1, 0);
   // if(n <= 0){ // if client disconnected
   //     LeaveCriticalSection(&CriticalSection);
  //      return -1;
  //  }

    LeaveCriticalSection(&CriticalSection);
    return 0;
}

DWORD WINAPI ThreadFunc(void *threadParam) {
    struct threadParamStruct params = *(struct threadParamStruct*)threadParam;
    int newsock = params.newsockfd; // get socket id from pointer to int socketnumber
    int *globalVar = params.globalVar;

    int res = 0;

    while(res >= 0){
        res = handleClient(newsock, globalVar);
    }
    timestamp();
    printf("Closing socket %d\n", newsock);
    closesocket(newsock);
    return 0;
}