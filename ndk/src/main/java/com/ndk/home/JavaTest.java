package com.ndk.home;

import com.base.common.util.LogUtilKt;

public class JavaTest {
    public void start() {
        replaceSpace();
    }

    private void replaceSpace() {
        //NDK
        long time = System.nanoTime();
        String a0 = "123 456 789";
        String a1 = NDKHelper.replaceSpaceC(a0);
        LogUtilKt.log("replaceSpace", (System.nanoTime() - time) + " -- " + a1);

        //java内置
        time = System.nanoTime();
        String a2 = a0.replace(" ", "%2");
        LogUtilKt.log("replaceSpace", (System.nanoTime() - time) + " -- " + a2);

        //手动
        time = System.nanoTime();
        char[] chars = a0.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char aChar : chars) {
            if (aChar == ' ') {
                sb.append("%2");
            } else {
                sb.append(aChar);
            }
        }
        LogUtilKt.log("replaceSpace", (System.nanoTime() - time) + " -- " + sb.toString());
    }
}
