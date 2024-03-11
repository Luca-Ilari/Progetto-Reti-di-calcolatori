//
// Created by LucaIlari on 3/4/2024.
//
#include <stdio.h>
#ifdef WIN32
#include <windows.h>
#endif

#include "server.h"
#include "../define.h"
#include "handleUpdateClients.h"
#include "../utils/customCriticalSection.h"

extern int updateAllClients;
extern int connectedSockets[MAX_CLIENT];
extern int nConnectedClient;

#ifdef WIN32
DWORD WINAPI handleUpdateClients(void *params) {
#else
void *handleUpdateClients(void *params){
#endif
    while(1){
        customEnterCriticalSection();
        if (updateAllClients == 1){
            timestamp();
            printf("-> Sending updated list to all client\n");
            for (int i = 0; i < nConnectedClient; ++i){
                sendProductListToClient(connectedSockets[i]);
            }
            updateAllClients = 0;
        }
        customLeaveCriticalSection();
    }
}