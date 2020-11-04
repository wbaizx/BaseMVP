#include <android/log.h>
#include <cstring>
#include "ch/c_test.h"

void testChars();

void testStruct();

void testPointer();

void exchange(int *a, int *b);

struct S {
    int a = 0;
    int b;
};

int testMain() {
    __android_log_print(ANDROID_LOG_ERROR, "c_test_main", "----------------test");

    int a = 5;
    int b = sizeof(a);

    __android_log_print(ANDROID_LOG_ERROR, "c_test_main", "%d", b);

    testChars();
    testStruct();
    testPointer();

    return 0;
}

void testChars() {
    char b[6] = {'s', 's'};
    __android_log_print(ANDROID_LOG_ERROR, "c_test_chars", "%s", b);
    char a[3] = {'s', 's', '\0'};
    __android_log_print(ANDROID_LOG_ERROR, "c_test_chars", "%s", a);
    char aa[3] = "12";
    __android_log_print(ANDROID_LOG_ERROR, "c_test_chars", "%s", aa);
    char bb[] = "123"
                "456";
    __android_log_print(ANDROID_LOG_ERROR, "c_test_chars", "%s", bb);
    __android_log_print(ANDROID_LOG_ERROR, "c_test_chars", "strlen %d", strlen(bb));
    __android_log_print(ANDROID_LOG_ERROR, "c_test_chars", "%c", bb[1]);
}

void testStruct() {
    S s;
    S b;
    b = s;
    s.a = 6;
    __android_log_print(ANDROID_LOG_ERROR, "c_test_struct", "%d", s.a);
    __android_log_print(ANDROID_LOG_ERROR, "c_test_struct", "%d", b.a);
}

void testPointer() {
    int a = 1;
    int b = 2;
    int c;
    __android_log_print(ANDROID_LOG_ERROR, "c_test_pointer", "%d%d", a, b);
    __android_log_print(ANDROID_LOG_ERROR, "c_test_pointer", "%p %p", &a, &b);

    c = a;
    a = b;
    b = c;

    __android_log_print(ANDROID_LOG_ERROR, "c_test_pointer", "%d%d", a, b);
    __android_log_print(ANDROID_LOG_ERROR, "c_test_pointer", "%p %p", &a, &b);
    exchange(&a, &b);
    __android_log_print(ANDROID_LOG_ERROR, "c_test_pointer", "%d%d", a, b);


    int *t = new int;
    int *db = new int[10];

    delete t;
    delete[]db;
}

void exchange(int *a, int *b) {
    int c = *a;
    *a = *b;
    *b = c;
}
