package com.ndk.home;

import com.base.common.util.LogUtilKt;

import java.util.Arrays;

public class JavaTest {
    public void start() {
        replaceSpace();
        reverseOrder();
    }

    private void replaceSpace() {
        String a0 = "123 456 789";

        //NDK
        long time = System.nanoTime();
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

    private void reverseOrder() {
        int[] a0 = {1, 2, 3, 4, 5};
        LogUtilKt.log("reverseOrder", Arrays.toString(a0));
        //NDK
        long time = System.nanoTime();
        NDKHelper.reverseOrderC(a0);
        LogUtilKt.log("reverseOrder", (System.nanoTime() - time) + " -- " + Arrays.toString(a0));

        //手动
        a0 = new int[]{1, 2, 3, 4, 5};
        time = System.nanoTime();
        int midpoint = a0.length >> 1;
        int t;
        for (int i = 0, j = a0.length - 1; i < midpoint; i++, j--) {
            t = a0[i];
            a0[i] = a0[j];
            a0[j] = t;
        }
        LogUtilKt.log("reverseOrder", (System.nanoTime() - time) + " -- " + Arrays.toString(a0));
    }
}
