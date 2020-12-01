package com.ndk.home;

import com.base.common.util.LogUtilKt;

public class JavaTest {
    public void start() {
        replaceSpace();
    }

    private void replaceSpace() {
        long time = System.currentTimeMillis();
        NDKHelper.replaceSpaceC();

        LogUtilKt.log("replaceSpace", System.currentTimeMillis() - time);

        LogUtilKt.log("replaceSpace", System.currentTimeMillis() - time);
    }
}
