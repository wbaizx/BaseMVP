#include <android/log.h>
#include "ch/c_test.h"

int testMain() {
    __android_log_print(ANDROID_LOG_ERROR, "c_ndk", "----------------test");

    int a = 5;
    int b = sizeof(a);

    __android_log_print(ANDROID_LOG_ERROR, "c_ndk", "%d", b);

    return 0;
}