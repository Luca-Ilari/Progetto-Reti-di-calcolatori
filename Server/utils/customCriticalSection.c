//
// Created by LucaIlari on 3/5/2024.
//

#ifdef WIN32
#include <windows.h>
extern CRITICAL_SECTION CriticalSection;
#else
#include <pthread.h>
extern pthread_mutex_t CriticalSection;
#endif

#include "customCriticalSection.h"

void customEnterCriticalSection(){
    #ifdef WIN32
    EnterCriticalSection(&CriticalSection);
    #else
    pthread_mutex_lock(&CriticalSection);
    #endif
}

void customLeaveCriticalSection(){
    #ifdef WIN32
    LeaveCriticalSection(&CriticalSection);
    #else
    pthread_mutex_unlock(&CriticalSection);
    #endif
}