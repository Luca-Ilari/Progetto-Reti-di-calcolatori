//
// Created by LucaIlari on 3/18/2024.
//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "loadFile.h"
#include "timeStamp.h"
#include "../product.h"

extern struct product *serverProductList;
extern int PRODUCT_NUMBER;

int customGetline(FILE *file, char *destBuffer, size_t bufferSize){
    char line[bufferSize];
    memset(line, 0, sizeof(line));
    char tempChar;
    unsigned int tempCharIdx = 0U;

    while(tempChar = fgetc(file)){
        /* avoid buffer overflow error */
        if (tempCharIdx == bufferSize) {
            return EXIT_FAILURE;
        }
        if (tempChar == '\n') {
            line[tempCharIdx] = '\n';
             break;
        }else{
            line[tempCharIdx] = tempChar;
            tempCharIdx += 1;
        }
    }
    memcpy(destBuffer,line,bufferSize);
    return 0;
}

int addProduct(char *name, long long quantity, float price){
    int newArraySize = sizeof(struct product) * (PRODUCT_NUMBER + 1);
    serverProductList = realloc(serverProductList, newArraySize);//TODO MemoryLeak??

    serverProductList[PRODUCT_NUMBER].id = PRODUCT_NUMBER;
    strcpy(serverProductList[PRODUCT_NUMBER].name, name); //TODO check name lenght
    serverProductList[PRODUCT_NUMBER].quantity = quantity;
    serverProductList[PRODUCT_NUMBER].price = price;

    PRODUCT_NUMBER++;
}

void readProductsFromFile(char *fileName){
    FILE *file = fopen(fileName, "r");
    if (file == NULL){
        timestamp();
        printf("WARNING: Can't open file %s", fileName);
        timestamp();
        printf("WARNING: Loaded default products\n");

        addProduct("Pane",100000, 5.5);
        addProduct("Acqua",50000, 1.99);
        addProduct("Vino",30000, 20);
        addProduct("Birra",40000, 2);
        addProduct("Patatine",10000, 2.8);
    }else{
        size_t lineMaxLen = 50;
        char line[lineMaxLen];
        int lineRead = 1;
        while (getc(file) != EOF){
            fseek(file, -1, SEEK_CUR); //rest cursor to line start
            int res = customGetline(file, line, lineMaxLen);
            if (res == EXIT_FAILURE){
                printf("Error loading %s line %d is too long\nMax length is %d characters\n", fileName, lineRead, lineMaxLen);
                exit(-1);
            }
            //TODO: create a custom function to replace deprecate strtok()
            char *name = strtok(line, ", ");
            char *qt = strtok(NULL, ", ");
            char *price = strtok(NULL, ", ");
            if(price == NULL || name == NULL || qt == NULL){ //Check for errors
                printf("Error loading %s at line %d\n",fileName, lineRead);
                exit(-1);
            }

            //TODO check for errors in conversion
            float floatPrice = strtof(price, NULL);
            long long quantity = strtoll(qt, NULL, 10);

            addProduct(line,quantity,floatPrice);
            lineRead++;
        }
        printf("Product loaded from CSV:\n");
        for (int i=0; i < PRODUCT_NUMBER; ++i) {
            printf("name:%s, quantity:%lld, price:%.2f\n", serverProductList[i].name, serverProductList[i].quantity,  serverProductList[i].price);
        }
    }
}
