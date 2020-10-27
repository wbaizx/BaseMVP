#当前目录对应 src/main/cpp 下

#版本
cmake_minimum_required(VERSION 3.4.1)

#将当前目录下所有的文件资源存储起来
aux_source_directory(. SRC_LIST)

#添加自己的总库，c_home.cpp为入口c++文件
add_library(testNDK-lib
        SHARED
        ${SRC_LIST})

#添加自己的头文件
#include_directories(.)
target_include_directories(
        testNDK-lib
        PRIVATE
        ch)

#关联三方库头文件
#target_include_directories()
#导入三方库
#add_library()
#设置导入三方库名，目标位置
#set_target_properties()

#导入ndk原生库
find_library(log-lib
        log)

#将自己的入口库和导入的三方库（如果还添了其他三方库的话），一起链接到NDK中
target_link_libraries(testNDK-lib
        ${log-lib})