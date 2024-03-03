cmake_minimum_required(VERSION 3.27)
project(Server)

set(CMAKE_CXX_STANDARD 17)

add_executable(Server
        main.cpp
        server.h
        threadParameter.h
        server.cpp
)

if(WIN32)
    target_link_libraries(Server wsock32 ws2_32)
endif()
