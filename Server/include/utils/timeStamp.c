//
// Created by LucaIlari on 3/11/2024.
//
#include <time.h>
#include <stdio.h>

#include "timeStamp.h"

void timestamp()
{
    time_t now = time(NULL);
    struct tm *tm_struct = localtime(&now);
    printf("\n[%d:%d:%d] ",tm_struct->tm_hour,tm_struct->tm_min,tm_struct->tm_sec);
}