package com.camera_opengl.debug;

import com.base.common.BaseAPP;
import com.base.common.util.LogUtil;

/**
 * 对应模块的Application只有在配置Camera组件是否可以独立运行时才生效
 * 记得要在对应AndroidManifest中使用
 */
public class CameraAPP extends BaseAPP {

    /**
     * 组件单独运行时，可以在这模拟登陆等一些特殊操作
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.INSTANCE.log("CameraAPP", baseAppContext);
    }
}
