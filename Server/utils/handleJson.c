//
// Created by LucaIlari on 3/4/2024.
//

#include <stdlib.h>
#include <stdio.h>

#ifdef WIN32
#include <windows.h>
#else
#include <string.h>
#endif

#include "handleJson.h"
#include "../headers/server.h"
#include "../headers/define.h"
#include "../headers/product.h"

extern struct product serverProductList[PRODUCT_NUMBER];

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

        sprintf(tmp, "\"quantitaDisponibile\":%d", serverProductList[i].quantity);
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

    int value = strtol(foundStrPtr, NULL, 10);
    if (value == 0) {
        return -1;
    }
    *result = value;
    return 0;
}

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
    int r = -1;
    r = getJsonValue(json, "\"idTransazione\":",&transaction->transactionId);
    if (r == -1)
        return NULL;
    r = getJsonValue(json, "\"idProdotto\":",&transaction->productId);
    if (r == -1)
        return NULL;
    r = getJsonValue(json, "\"quantita\":",&transaction->quantityToRemove);
    if (r == -1)
        return NULL;
    return transaction;
}
