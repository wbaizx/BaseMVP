#include <jni.h>
#include <string>
#include <android/log.h>
#include "ch/c_test.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_ndk_home_NDKHelper_stringFromJNI(
        JNIEnv *env,
        jobject) {

    __android_log_print(ANDROID_LOG_ERROR, "c_ndk", "----------------home");


    testMain();


    std::string hello = " from C++";
    return env->NewStringUTF(hello.c_str());
}


