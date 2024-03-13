//
// Created by lucai on 13/03/2024.
//

#ifndef SERVER_SOCKETFUNCTIONS_H
#define SERVER_SOCKETFUNCTIONS_H

int setupSocket(int argc);
int acceptNewConnection(int sockfd);
int sendToClient(int sock, char *buffer);
void sendProductListToClient(int sock);

#endif //SERVER_SOCKETFUNCTIONS_H
