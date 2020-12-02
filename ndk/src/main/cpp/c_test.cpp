#include <android/log.h>
#include <string>

using namespace std;

#define LOG(s1, s2) __android_log_print(ANDROID_LOG_ERROR, s1, s2);

//可变参数只能是int
inline void log(const char *s1, const char *s2, ...) {
    va_list args;
    va_start(args, s2);
    int value = va_arg(args, int);
    __android_log_print(ANDROID_LOG_ERROR, s1, s2, value);
    va_end(args);
}

char *replaceSpaceC(const char *s) {
//    __android_log_print(ANDROID_LOG_ERROR, "c_replaceSpaceC", "length %d", strlen(s));

    int count = 0;
    int len = strlen(s);
    for (int i = 0; i < len; ++i) {
        if (*(s + i) == ' ') {
            count += 2;
        } else {
            count++;
        }
    }

    char *returnString = new char[count];

    for (int i = 0, j = 0; i < len; ++i) {
        if (*(s + i) == ' ') {
            returnString[j] = '%';
            returnString[j + 1] = '2';
            j += 2;
        } else {
            returnString[j] = *(s + i);
            j++;
        }
    }
    return returnString;
}