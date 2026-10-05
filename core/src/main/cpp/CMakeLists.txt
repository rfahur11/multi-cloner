cmake_minimum_required(VERSION 3.22.1)

project("vcore")

add_library(
    vcore
    SHARED
    vcore_hook.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    vcore
    ${log-lib}
)
