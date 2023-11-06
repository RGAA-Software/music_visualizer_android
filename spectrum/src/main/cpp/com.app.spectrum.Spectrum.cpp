//
// Created by huayang on 10/16/20.
//

#include <jni.h>

#include <android/log.h>
#include <sys/system_properties.h>
#include <dlfcn.h>
#include <stddef.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "filter_factory.h"

#define LOG_TAG "Spectrum"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static sk::MonsterCatFilterPtr mc_filter = sk::FilterFactory::CreateMonsterCatFilter();

extern "C"
JNIEXPORT void JNICALL
Java_com_app_spectrum_Spectrum_filterMonsterCat(JNIEnv *env, jclass clazz, jfloatArray data) {
    jfloat* input_array_ptr = env->GetFloatArrayElements(data, nullptr);
    jsize input_array_size = env->GetArrayLength(data);
    std::vector<double> fft_data;
    fft_data.resize(input_array_size);
    for (int i = 0; i < input_array_size; i++) {
        fft_data[i] = (input_array_ptr[i]);
    }
    env->ReleaseFloatArrayElements(data, input_array_ptr, 0);
    mc_filter->FilterBars(fft_data);

    jfieldID sgs_array_id = env->GetStaticFieldID(clazz, "sMCArray", "[F");
    jfloatArray sgs_array = (jfloatArray)env->GetStaticObjectField(clazz, sgs_array_id);
    jsize sgs_array_size = env->GetArrayLength(sgs_array);

    jfloat* sgs_array_ptr = env->GetFloatArrayElements(sgs_array, nullptr);
    for (int i = 0; i < sgs_array_size; i++) {
        *(sgs_array_ptr + i) = (float)fft_data[i];
    }

    env->ReleaseFloatArrayElements(sgs_array, sgs_array_ptr, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_app_spectrum_Spectrum_filterWA(JNIEnv *env, jclass clazz, jfloatArray data) {

}