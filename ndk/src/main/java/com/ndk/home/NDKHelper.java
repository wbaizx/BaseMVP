package com.ndk.home;

public class NDKHelper {
    static {
        System.loadLibrary("testNDK-lib");
    }

   public static native String stringFromJNI();

   public static native String replaceSpaceC(String a0);
}
