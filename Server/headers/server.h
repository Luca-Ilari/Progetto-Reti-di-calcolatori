#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H

#ifdef WIN32
#include <windows.h>
DWORD WINAPI ThreadFunc(void *threadParam);
#else
void *ThreadFunc(void *threadParam);
#endif
void timestamp();
int sendProductListToClient(int sock);
int setupSocket(int argc);
int acceptNewConnection(int sockfd);

#endif //SERVER_SERVER_H
