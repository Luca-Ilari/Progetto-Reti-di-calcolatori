#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H

#ifdef WIN32
#include <windows.h>
#else

#endif

void timestamp();
int setupSocket(int argc);
int acceptNewConnection(int sockfd);
#ifdef WIN32
DWORD WINAPI ThreadFunc(void *threadParam);
#else
void *ThreadFunc(void *threadParam);
#endif

#endif //SERVER_SERVER_H
