#ifndef SERVER_THREADPARAMETER_H
#define SERVER_THREADPARAMETER_H

struct threadParamStruct{
    int newsockfd;
    int *globalVar;
};

#endif //SERVER_THREADPARAMETER_H