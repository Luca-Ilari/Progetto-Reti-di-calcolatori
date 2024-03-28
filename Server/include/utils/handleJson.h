//
// Created by LucaIlari on 3/4/2024.
//

#ifndef SERVER_HANDLEJSON_H
#define SERVER_HANDLEJSON_H

char *getProductJson();
char *getClientsJson();
struct jsonTransaction *getJsonTransaction(char *json);
int getJsonStatusCode(char *json, int *jsonStatusCode);

#endif //SERVER_HANDLEJSON_H
