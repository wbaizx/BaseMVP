package com.camera_opengl.home.play;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.camera_opengl.R;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

//RouteString.VIDEO_PLAY
@Route(path = "/camera/video_play", name = "组件化camera 视频播放页", extras = -1)
public class PlayActivity extends BaseActivity {
    private static final String TAG = "PlayActivity";

    @Autowired
    String path;

    private PlayManager playManager = new PlayManager();

    @Override
    protected int getContentView() {
        return R.layout.activity_play;
    }

    @Override
    protected void setImmersionBar() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
    }

    @Override
    protected void initView() {
        findViewById(R.id.playSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {
                    playManager.play();
                }
            }
        });

        playManager.init(path);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        playManager.onPause();
    }

    @Override
    protected void onDestroy() {
        playManager.onDestroy();
        super.onDestroy();
    }
}
