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

char *buildHtmlResponse(char *filePath){
    int htmlLen = 0;
    FILE *htmlFile;
    htmlFile = fopen(filePath, "r");
    if(htmlFile == NULL){
        timestamp();
        printf("Can't find \"%s\"", filePath);
        return NULL;
    }
    while(fgetc(htmlFile) != EOF){htmlLen++;}//Get file length

    fseek(htmlFile, 0, SEEK_SET); //reset file pointer to the start of the file

    char *html = calloc((htmlLen+1), sizeof(char));

    for (int i = 0; i < htmlLen; ++i) {
        html[i] = (char)fgetc(htmlFile);
    }
    html[htmlLen] = '\0';
    fclose(htmlFile);
    return html;
}

int tryToReadFile(char *file){
    FILE *htmlFile;
    htmlFile = fopen(file, "r");
    if(htmlFile == NULL){
        timestamp();
        printf("Can't find \"%s\" webserver not started", file);
        return -1;
    }
    fclose(htmlFile);
    return 0;
}

int getLineLen(char *str){
    int i = 0;
    while(str[i] != '\n'){
        i++;
    }
    return i;
}

/**Copy src to dest for len
 * the last character will be 0*/
int copyString(char *dest,int len,char *src){
    for (int i = 0; i < len; ++i) {
        dest[i] = src[i];
    }
    dest[len]='\0';

    return 0;
}

int findChar(char *arr, char charToFind){
    int index = 0;
    while(arr[index] != charToFind){
        index++;
    }
    return index;
}

char *decodeRequest(char *buffer){
    int lineLen = getLineLen(buffer);
    char *line = calloc(lineLen+1, sizeof(char));

    copyString(line, lineLen, buffer);

    char *res = strstr(line, "GET");
    if (res == NULL)
        return NULL;
    res = res + 4;
    int pathLen = findChar(res, ' ');

    char *pathRequested = calloc(pathLen+1, sizeof(char)); //path len +1 to end the string with \0
    copyString(pathRequested, pathLen, res);
    printf("\n%s", pathRequested);

    free(line);
    line = NULL;
    return pathRequested;
}

int respond(char *pageRequested,int newsockfd){
    char buffer[BUFFER_SIZE];
    memset(buffer, '\0', BUFFER_SIZE);

    if(strcmp(pageRequested, "/") == 0){
        char *htmlResponse = buildHtmlResponse("./html/index.html");
        if(htmlResponse == NULL)
            return -1;
        size_t len = strlen(htmlResponse);
        memset(buffer, '\0', sizeof(buffer));
        sprintf(buffer,"HTTP/1.1 200 OK\r\nContent-Length: %zu\r\nServer: ReallyBadWebServer/1.0\r\nContent-Type: text/html\r\n\r\n%s", len, htmlResponse);

        sendToClient(newsockfd, buffer);//Send html page
        free(htmlResponse);
        htmlResponse = NULL;
    }else if(strcmp(pageRequested, "/prodotti") == 0){
        char *jsonResponse = getProductJson();
        if(jsonResponse == NULL)
            return -1;
        size_t len = strlen(jsonResponse);
        memset(buffer, '\0', sizeof(buffer));
        sprintf(buffer,"HTTP/1.1 200 OK\r\nContent-Length: %zu\r\nServer: ReallyBadWebServer/1.0\r\nContent-Type: application/json\r\n\r\n%s", len, jsonResponse);

        sendToClient(newsockfd, buffer);//Send html page
        free(jsonResponse);
        jsonResponse = NULL;
    }else{
        memset(buffer, '\0', sizeof(buffer));
        sprintf(buffer,"HTTP/1.1 200 OK\r\nContent-Length: 18\r\nServer: ReallyBadWebServer/1.0\r\nContent-Type: text/html\r\n\r\nPagina non trovata");

        sendToClient(newsockfd, buffer);//Send html page
    }
}

#ifdef WIN32
DWORD WINAPI webServer(){
#else
void *webServer(){
#endif
    int sockfd, portno = 8080;
    char buffer[BUFFER_SIZE];
    struct sockaddr_in serv_addr;

    int status = startWebServer(&portno, &sockfd, &serv_addr);
    if(status < 0){
        timestamp();
        printf("Can't start webserver");
        return -1;
    }

    if (bind(sockfd, (struct sockaddr*)&serv_addr,sizeof(serv_addr)) < 0){
        timestamp();
        printf("Can't start webserver");
        perror("\nERROR on binding");
        return -1;
    }

    if(tryToReadFile("./html/index.html") == -1){
        timestamp();
        printf("Can't start webserver");
        return -1;
    }

    timestamp();
    printf("Web server started http://localhost:%d", portno);

    while(1){
        //receive request
        int newsockfd = acceptNewConnection(sockfd);
        memset(buffer, '\0', sizeof(buffer));
        recv(newsockfd,buffer,BUFFER_SIZE-1,0);
 
        char *pageRequested = decodeRequest(buffer);
        respond(pageRequested, newsockfd);
        free(pageRequested);
        pageRequested = NULL;

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
