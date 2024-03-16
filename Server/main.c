#include <stdio.h>
#include <stdlib.h>
#ifdef WIN32
#include <winsock2.h>
#include <windows.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>
#endif

#include "./include/define.h"
#include "./include/product.h"
#include "./include/utils/timeStamp.h"
#include "./include/sockets/handleClient.h"
#include "./include/sockets/socketFunctions.h"
#include "./include/sockets/handleUpdateClients.h"

#ifdef WIN32
CRITICAL_SECTION CriticalSection;
#else
pthread_mutex_t CriticalSection;
#endif

int updateAllClients = 0;
int nConnectedClient = 0;
int connectedSockets[MAX_CLIENT];

struct product *serverProductList;
int PRODUCT_NUMBER = 0;

int addProduct(char *name, long long quantity, float price){
    int newArraySize = sizeof(struct product) * (PRODUCT_NUMBER + 1);
    serverProductList = realloc(serverProductList, newArraySize);
    
    serverProductList[PRODUCT_NUMBER].id = PRODUCT_NUMBER;
    strcpy(serverProductList[PRODUCT_NUMBER].name, name); //TODO check name lenght
    serverProductList[PRODUCT_NUMBER].quantity = quantity;
    serverProductList[PRODUCT_NUMBER].price = price;
    
    PRODUCT_NUMBER++;
}

void readProductsFromFile(){
    //serverProductList = malloc(sizeof(struct product) * PRODUCT_NUMBER);
    FILE *file = fopen("./products.csv", "r");
    if (file == NULL){
        timestamp();
        printf("WARNING: Can't open file products.csv");
        timestamp();
        printf("WARNING: Loaded default products\n");
        
        addProduct("Pane",100000, 5.5);
        addProduct("Acqua",50000, 1.99);
        addProduct("Vino",30000, 20);
        addProduct("Birra",40000, 2);
        addProduct("Patatine",10000, 2.8);
    }
    
    char *line = NULL;
    size_t len;
    int lineRead = 1;
    while (getc(file) != EOF){
        fseek(file, -1, SEEK_CUR); //rest cursor to line start
        size_t a = getline(&line,&len,file);
        char * name = strtok(line, ", ");
        if(name == NULL){
            printf("Error loading products.csv at line %d\n", lineRead);
            exit(-1);
        }
        char *qt = strtok(NULL, ", ");
        if(qt == NULL){
            printf("Error loading products.csv at line %d\n", lineRead);
            exit(-1);
        }
        char *price = strtok(NULL, ", ");
    
        if(price == NULL){
            printf("Error loading products.csv at line %d\n", lineRead);
            exit(-1);
        }
        float floatPrice = strtof(price, NULL);
        //TODO check error in conversion
        long long quantity = strtoll(qt, NULL, 10);

        addProduct(line,quantity,floatPrice);
        lineRead++;
    }
    free(line);
    printf("Product loaded from CSV:\n");
    for (int i=0; i < PRODUCT_NUMBER; ++i) {
        printf("name:%s, quantity:%lld, price:%.2f\n", serverProductList[i].name, serverProductList[i].quantity,  serverProductList[i].price);
    }
}

int main(int argc, char* argv[]){
    if (argc < 2){
        timestamp();
        printf("Can't start server. Please specify port number\n");
        return -1;
    }

    readProductsFromFile();
    
    timestamp();
    printf("Starting sever on port %s", argv[1]);
    
    int sockfd, portno;
    struct sockaddr_in serv_addr;
    portno = atoi(argv[1]);

    sockfd = setupSocket(argc);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);
    
    #ifdef WIN32
    InitializeCriticalSection(&CriticalSection);
    #endif
    
    if (bind(sockfd, (struct sockaddr*)&serv_addr,sizeof(serv_addr)) < 0){
        perror("\nERROR on binding");
        return 0;
    }
    timestamp();
    printf("Server started\n");

    #ifdef WIN32
    CreateThread(NULL, 0, handleUpdateClients, NULL, 0, NULL);
    #else
    pthread_t thread_id = 0;
    pthread_create(&thread_id,NULL,handleUpdateClients, NULL);
    #endif

    while(1) {
        if (nConnectedClient < MAX_CLIENT){
            memset(&serv_addr, 0, sizeof(serv_addr));
            int newsockfd = acceptNewConnection(sockfd);

            if (newsockfd > 0) {
                int *param = &newsockfd;
                #ifdef WIN32
                CreateThread(NULL, 0, handleNewClient, param, 0, NULL);
                #else
                pthread_create(&thread_id, NULL, handleNewClient, param);
                #endif
            }else{
                timestamp();
                perror("ERROR while accepting new client\n");
            }
        }
    }
    free(serverProductList);
    #ifdef WIN32
    closesocket(sockfd);
    #else
    close(sockfd);
    #endif
}
