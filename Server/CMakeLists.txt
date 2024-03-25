cmake_minimum_required(VERSION 3.16)
project(Server)

set(CMAKE_CXX_STANDARD 17)

add_executable(Server
        main.c
        include/sockets/handleClient.h
        include/sockets/handleClient.c
        include/define.h
        include/product.h
        include/sockets/handleUpdateClients.c
        include/sockets/handleUpdateClients.h
        include/utils/handleJson.c
        include/utils/handleJson.h
        include/utils/customCriticalSection.c
        include/utils/customCriticalSection.h
        include/utils/timeStamp.c
        include/utils/timeStamp.h
        include/sockets/socketFunctions.c
        include/sockets/socketFunctions.h
        include/utils/loadFile.c
        include/utils/loadFile.h
        include/sockets/webServer.c
        include/sockets/webServer.h
)

if(WIN32)
    target_link_libraries(Server wsock32 ws2_32)
elseif (UNIX)
    set(THREADS_PREFER_PTHREAD_FLAG ON)
    find_package(Threads REQUIRED)
    target_link_libraries(Server PRIVATE Threads::Threads)
endif()