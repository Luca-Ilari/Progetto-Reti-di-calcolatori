//
// Created by LucaIlari on 3/4/2024.
//
#include <stdio.h>
#ifdef WIN32
#include <windows.h>
#endif

#include "./socketFunctions.h"
#include "./handleUpdateClients.h"
#include "./socketFunctions.h"
#include "../define.h"
#include "../utils/timeStamp.h"
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
            printf("-> Sending updated list to all client");
            for (int i = 0; i < nConnectedClient; ++i){
                sendProductListToClient(connectedSockets[i]);
            }
            updateAllClients = 0;
        }
        customLeaveCriticalSection();
    }
}
