cmake_minimum_required(VERSION 3.16)
project(Server)

set(CMAKE_CXX_STANDARD 17)

add_executable(Server
        main.c
        headers/server.h
        server.c
        headers/define.h
        headers/product.h
        handleUpdateClients.c
        headers/handleUpdateClients.h
        handleJson/handleJson.c
        handleJson/handleJson.h
)

if(WIN32)
    target_link_libraries(Server wsock32 ws2_32)
elseif (UNIX)
    set(THREADS_PREFER_PTHREAD_FLAG ON)
    find_package(Threads REQUIRED)
    target_link_libraries(Server PRIVATE Threads::Threads)
endif()