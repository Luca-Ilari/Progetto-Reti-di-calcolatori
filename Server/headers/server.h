#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H

#include <windows.h>

void timestamp();
int setupSocket(int argc);
int acceptNewConnection(int sockfd);
DWORD WINAPI ThreadFunc(void *threadParam);

#endif //SERVER_SERVER_H
