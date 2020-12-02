#include <jni.h>
#include <string>
#include <android/log.h>
#include "ch/c_test.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ndk_home_NDKHelper_stringFromJNI(
        JNIEnv *env,
        jclass thiz) {

    __android_log_print(ANDROID_LOG_ERROR, "c_home", "----------------home");

    std::string hello = " from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ndk_home_NDKHelper_replaceSpaceC(JNIEnv *env, jclass clazz, jstring a0) {
    const char *nativeString = env->GetStringUTFChars(a0, nullptr);

    char *returnString = replaceSpaceC(nativeString);
    //释放GetStringUTFChars开辟的内存
    env->ReleaseStringUTFChars(a0, nativeString);

    jstring newArgName = env->NewStringUTF(returnString);

    //释放replaceSpaceC中new的数据
    delete[] returnString;
    //返回给java层的对象不用释放
//    env->DeleteLocalRef(newArgName);
    return newArgName;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_home_NDKHelper_reverseOrderC(JNIEnv *env, jclass clazz, jintArray a0) {
    int len = env->GetArrayLength(a0);
    int *nativeArray = env->GetIntArrayElements(a0, nullptr);
    reverseOrder(nativeArray, len);
    //第三个参数为0, Java数组进行更新，并且释放C/C++数组
    env->ReleaseIntArrayElements(a0, nativeArray, 0);
}