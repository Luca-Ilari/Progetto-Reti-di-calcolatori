#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H

#ifdef win32
#include <windows.h>
#else

#endif

void timestamp();
int setupSocket(int argc);
int acceptNewConnection(int sockfd);
#ifdef win32
DWORD WINAPI ThreadFunc(void *threadParam);
#else
void *ThreadFunc(void *threadParam);
#endif

#endif //SERVER_SERVER_H
