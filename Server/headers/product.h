//
// Created by LucaIlari on 3/3/2024.
//

#ifndef SERVER_PRODUCT_H
#define SERVER_PRODUCT_H

struct product{
    int id;
    char name[20];
    int quantity;
    float price;
};
struct jsonTransaction{
    int transactionId;
    int productId;
    int quantityToRemove;
};

#endif //SERVER_PRODUCT_H
