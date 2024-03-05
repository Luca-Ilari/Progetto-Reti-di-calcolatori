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

// {"codiceStato":2,"transazione":{"idTransazione":6,"idProdotto":2,"quantita":7}}
int validateJson(char *json, int *jsonStatusCode) {
  //  if (json[0] == '{' && json[strlen(json)-2] == '}') {
        char strToFind[] = {"\"codiceStato\":"};
        char *foundStr = strstr(json, strToFind);

        if (foundStr == NULL){
            printf("\nNULL\n");
            return -1;
        }
        foundStr += strlen(strToFind);

        //  statusCode = atoi(foundStr);
        int statusCode = strtol(foundStr, NULL, 10);
        if (statusCode == 0){
            printf("\nto INt\n");
            return -1;
        }
        *jsonStatusCode = statusCode;
        return 0;
/*
    }else{
        printf("\n{}\n");
        return -1;
    }*/
}