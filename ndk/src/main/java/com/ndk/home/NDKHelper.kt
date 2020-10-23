package com.ndk.home

object NDKHelper {
    init {
        System.loadLibrary("testNDK-lib")
    }

    external fun stringFromJNI(): String
}