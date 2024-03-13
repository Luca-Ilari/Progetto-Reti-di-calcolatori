#ifndef SERVER_HANDLECLIENT_H
#define SERVER_HANDLECLIENT_H

#ifdef WIN32
#include <windows.h>
DWORD WINAPI handleNewClient(void *threadParam);
#else
void *handleNewClient(void *threadParam);
#endif

#endif //SERVER_HANDLECLIENT_H
