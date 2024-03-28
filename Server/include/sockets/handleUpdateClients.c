//
// Created by LucaIlari on 3/4/2024.
//
#include <stdio.h>
#ifdef WIN32
#include <windows.h>
#elif __APPLE__
#include <dispatch/dispatch.h>
#else
#include <semaphore.h>
#endif

#include "./socketFunctions.h"
#include "./handleUpdateClients.h"
#include "./socketFunctions.h"
#include "../define.h"
#include "../utils/timeStamp.h"
#include "../utils/customCriticalSection.h"

extern int connectedSockets[MAX_CLIENT];
extern int nConnectedClient;
#ifdef WIN32
extern HANDLE semaphore;
#elif __APPLE__
extern dispatch_semaphore_t semaphore;
#else
extern sem_t semUpdateAllClients;
#endif

#ifdef WIN32
DWORD WINAPI handleUpdateClients(void *params) {
#else
void *handleUpdateClients(void *params){
#endif
    while(1){
        #ifdef WIN32
        WaitForSingleObject(semaphore, INFINITE);
        #elif __APPLE__
        dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
        #else
        sem_wait(&semUpdateAllClients);
        #endif
        customEnterCriticalSection();
        for (int i = 0; i < nConnectedClient; ++i){
            sendProductListToClient(connectedSockets[i]);
        }
        customLeaveCriticalSection();
    }
}
