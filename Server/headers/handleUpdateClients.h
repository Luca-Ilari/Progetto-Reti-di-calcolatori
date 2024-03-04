//
// Created by LucaIlari on 3/4/2024.
//

#ifndef SERVER_HANDLEUPDATECLIENTS_H
#define SERVER_HANDLEUPDATECLIENTS_H

#ifdef WIN32
DWORD WINAPI handleUpdateClients(void *updateClients);
#else
void *handleUpdateClients(void *updateClients);
#endif

#endif //SERVER_HANDLEUPDATECLIENTS_H
