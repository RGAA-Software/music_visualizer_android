cmake_minimum_required (VERSION 3.0)


project(native_spectrum)


add_library(native_filter SHARED filter.cpp filter.h filter_factory.h)


# 从系统查找依赖库
find_library( # Sets the name of the path variable.
        # android系统每个类型的库会存放一个特定的位置，而log库存放在log-lib中
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        # android系统在c环境下打log到logcat的库
        log )

add_library(native_spectrum SHARED com.app.spectrum.Spectrum.cpp)
target_link_libraries(native_spectrum ${log-lib} native_filter)