package com.camera_opengl.home;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.base.common.config.RouteString;
import com.camera_opengl.R;

@Route(path = RouteString.CAMERA_HOME, name = "组件化camera首页", extras = RouteString.isNeedLogin)
public class CameraActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
