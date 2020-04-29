package com.camera_opengl.home.play;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.base.common.util.LogUtil;
import com.camera_opengl.R;

//RouteString.VIDEO_PLAY
@Route(path = "/camera/video_play", name = "组件化camera 视频播放页", extras = -1)
public class PlayActivity extends BaseActivity {
    private static final String TAG = "PlayActivity";

    @Autowired
    String path;

    @Override
    protected int getContentView() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        LogUtil.INSTANCE.log(TAG, "path " + path);
    }

    @Override
    protected void initData() {

    }
}
