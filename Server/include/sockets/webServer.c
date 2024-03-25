//
//  WebServer.c
//  Server
//
//  Created by luca on 24/03/24.
//
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

#include "webServer.h"
#include "socketFunctions.h"
#include "../utils/timeStamp.h"
#include "../utils/handleJson.h"

#ifdef WIN32
DWORD WINAPI webServer(){
#else
void *webServer(){
#endif
    int sockfd, portno = 8080;
    char buffer[1024];
    struct sockaddr_in serv_addr;
    timestamp();
    printf("Web server starting on port:%d", portno);

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
    timestamp();
    printf("Web server started http://localhost:%d", portno);
    while(1){
        memset(&serv_addr, 0, sizeof(serv_addr));
        int newsockfd = acceptNewConnection(sockfd);
        memset(&buffer, 0, sizeof(buffer));
        recv(newsockfd,buffer,1024-1,0);
        //printf("%s\n", buffer);
        memset(&buffer, 0, sizeof(buffer));
        char *a = getProductJson();
        int lenght = strlen(a) + strlen("<!DOCTYPE html><script> function autoRefresh() { window.location = window.location.href; } setInterval('autoRefresh()', 10000); </script>");
        sprintf(buffer,"HTTP/1.1 200 OK\r\nContent-Length: %d\r\nServer: ReallyBadWebServer/1.0\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE html><script> function autoRefresh() { window.location = window.location.href; } setInterval('autoRefresh()', 10000); </script>%s", lenght, a);
        sendToClient(newsockfd, buffer);
        free(a);
        a = NULL;
        #ifdef WIN32
        closesocket(newsockfd);
        #else
        close(newsockfd);
        #endif
    }
    #ifdef WIN32
    closesocket(sockfd);
    #else
    close(sockfd);
    #endif
}
