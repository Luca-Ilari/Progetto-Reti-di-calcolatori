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
#include "../define.h"
#include "../utils/timeStamp.h"
#include "../utils/handleJson.h"

int startWebServer(int *portno, int *sockfd, struct sockaddr_in *serv_addr){

    timestamp();
    printf("Web server starting on port:%d", *portno);

#ifdef WIN32
    WSADATA info;
    if (WSAStartup(MAKEWORD(1, 1), &info) == SOCKET_ERROR) {
        timestamp();
        perror("ERROR, can't start webserver socket\n");
    }
#endif
    *sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (*sockfd < 0){
        timestamp();
        perror("ERROR opening webserver socket\n");
        return -1;
    }
    serv_addr->sin_family = AF_INET;
    serv_addr->sin_addr.s_addr = INADDR_ANY;
    serv_addr->sin_port = htons(*portno);
    return 0;
}

char *buildHtmlResponse(FILE *htmlFile){
    int htmlLen = 0;

    while(fgetc(htmlFile) != EOF){htmlLen++;}//Get file length

    fseek(htmlFile, 0, SEEK_SET); //reset file pointer to the start of the file

    char *html = calloc(htmlLen, (htmlLen+1) * sizeof(char));
    //memset(html, '\0', sizeof(html));

    for (int i = 0; i < htmlLen; ++i) {
        html[i] = (char)fgetc(htmlFile);
        if (html[i] == NULL){
            printf("a");
        }
    }
    html[htmlLen] = '\0';
    return html;
}

#ifdef WIN32
DWORD WINAPI webServer(){
#else
void *webServer(){
#endif
    int sockfd, portno = 8080;
    char buffer[BUFFER_SIZE];
    struct sockaddr_in serv_addr;
    FILE *htmlFile;

    int status = startWebServer(&portno, &sockfd, &serv_addr);
    if(status < 0){
        timestamp();
        printf("Can't start webserver");
        return -1;
    }

    if (bind(sockfd, (struct sockaddr*)&serv_addr,sizeof(serv_addr)) < 0){
        perror("\nERROR on binding webserver socket");
        return -1;
    }

    htmlFile = fopen("./html/index.html", "r");
    if(htmlFile == NULL){
        timestamp();
        printf("Can't find \"index.html\" webserver not started");
        return -1;
    }

    timestamp();
    printf("Web server started http://localhost:%d", portno);

    while(1){
        int newsockfd = acceptNewConnection(sockfd);
        memset(buffer, '\0', sizeof(buffer));
        recv(newsockfd,buffer,BUFFER_SIZE-1,0);
        char *htmlResponse = buildHtmlResponse(htmlFile);

        size_t len = strlen(htmlResponse);
        memset(buffer, '\0', sizeof(buffer));
        sprintf(buffer,"HTTP/1.1 200 OK\r\nContent-Length: %zu\r\nServer: ReallyBadWebServer/1.0\r\nContent-Type: text/html\r\n\r\n%s", len, htmlResponse);

        sendToClient(newsockfd, buffer);//Send html page
        free(htmlResponse);
        htmlResponse = NULL;

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
    return 0;
}
