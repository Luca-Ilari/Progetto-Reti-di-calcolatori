//
// Created by LucaIlari on 3/4/2024.
//

#include <stdlib.h>
#include <stdio.h>
#include <errno.h>

#ifdef WIN32
#include <windows.h>
#else
#include <string.h>
#endif

#include "./handleJson.h"
#include "../utils/timeStamp.h"
#include "../define.h"
#include "../product.h"

extern struct product *serverProductList;
extern int PRODUCT_NUMBER;
extern int connectedSockets[MAX_CLIENT];
extern int nConnectedClient;

/**The json returned should be freed*/
char *getClientsJson(){
    char *json = calloc(100, sizeof(char));

    strcat(json, "{");
    char tmp[50];
    memset(tmp, '\0', 50);
    sprintf(tmp, "\"connectedClients\":%d", nConnectedClient);
    strcat(json, tmp);
    strcat(json, "}\n");
    
    return json;
}

/**The json returned should be freed*/
char *getProductJson(){
    char json[1024];
    memset(&json, 0, sizeof(json));

    strcat(json, "{\"codiceStato\":4,\"prodotti\":[");
    for (int i=0; i < PRODUCT_NUMBER; ++i){
        char tmp[1024];
        memset(&tmp, 0, sizeof(tmp));

        strcat(json, "{");

        sprintf(tmp, "\"id\":%d,", serverProductList[i].id);
        strcat(json, tmp);

        sprintf(tmp, "\"nome\":\"%s\",", serverProductList[i].name);
        strcat(json, tmp);

        sprintf(tmp, "\"prezzo\":%f,", serverProductList[i].price);
        strcat(json, tmp);

        sprintf(tmp, "\"quantitaDisponibile\":%lld", serverProductList[i].quantity);
        strcat(json, tmp);
        if(i == PRODUCT_NUMBER-1){
            strcat(json, "}"); //last item without ,
        }else{
            strcat(json, "},");
        }
    }
    strcat(json, "]}\n");

    char *heapJson = malloc(strlen(json)+1);
    if (heapJson == NULL){
        timestamp();
        printf("CAN'T ALLOCATE MEMORY");
    }
    memset(heapJson,0,strlen(json)+1);
    strcpy(heapJson,json);

    return heapJson;
}

int getJsonValue(char *json, char *strToFind, int *result) {
    char *foundStrPtr = strstr(json, strToFind);

    if (foundStrPtr == NULL) {
        return -1;
    }
    foundStrPtr += strlen(strToFind);

    errno = 0;
    int value = strtol(foundStrPtr, NULL, 10);
    if (errno != 0 && value == 0) { // if strtol has returned an error
        return -1;
    }
    *result = value;
    return 0;
}
// Json example
// {"codiceStato":2,"transazione":{"idTransazione":6,"idProdotto":2,"quantita":7}}
int getJsonStatusCode(char *json, int *jsonStatusCode) {
    char strToFind[] = {"\"codiceStato\":"};
    if (getJsonValue(json,strToFind,jsonStatusCode) == 0){
        return 0;
    }
    return -1;
}

/**return jsonTransaction struct if it finds all the transaction data in the json passed
 * @Return NULL if the json doesn't have all the transaction values
 * @Return *jsonTransaction allocated with malloc*/
struct jsonTransaction *getJsonTransaction(char *json){
    struct jsonTransaction *transaction;
    transaction = malloc(sizeof(struct jsonTransaction));
    int r;
    r = getJsonValue(json, "\"idTransazione\":",&transaction->transactionId);
    if (r == -1)
        return NULL;
    r = getJsonValue(json, "\"idProdotto\":",&transaction->productId);
    if (r == -1)
        return NULL;
    r = getJsonValue(json, "\"quantita\":",&transaction->quantity);
    if (r == -1)
        return NULL;
    return transaction;
}
