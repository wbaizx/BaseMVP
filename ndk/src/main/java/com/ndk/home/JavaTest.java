package com.ndk.home;

import com.base.common.util.LogUtilKt;

public class JavaTest {
    public void start() {
        replaceSpace();
    }

    private void replaceSpace() {
        long time = System.currentTimeMillis();
        String aa="123 456 789 123 456 789";
        NDKHelper.replaceSpaceC();

        LogUtilKt.log("replaceSpace", System.currentTimeMillis() - time);

        LogUtilKt.log("replaceSpace", System.currentTimeMillis() - time);
    }
}
