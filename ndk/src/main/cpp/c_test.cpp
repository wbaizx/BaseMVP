#include <android/log.h>
#include <string>

using namespace std;

#define LOG(s1, s2) __android_log_print(ANDROID_LOG_ERROR, s1, s2);

inline void log(const char *s1, const char *s2) { __android_log_print(ANDROID_LOG_ERROR, s1, "%s", s2); }

int replaceSpaceC() {
//    sizeof(a);
//    strlen(b);
//    int *a = nullptr;

    LOG("c_replaceSpaceC", "----------------test")
    log("c_replaceSpaceC", "----------------test");

    return 0;
}