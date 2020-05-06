package com.camera_opengl.home.play;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.camera_opengl.R;
import com.camera_opengl.home.SavePictureThread;
import com.camera_opengl.home.camera.CameraControlListener;
import com.camera_opengl.home.gl.egl.EGLSurfaceView;
import com.camera_opengl.home.gl.egl.GLSurfaceListener;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

//RouteString.VIDEO_PLAY
@Route(path = "/camera/video_play", name = "组件化camera 视频播放页", extras = -1)
public class PlayActivity extends BaseActivity implements GLSurfaceListener, PlayListener {
    private static final String TAG = "PlayActivity";

    @Autowired
    String path;

    private EGLSurfaceView eglSurfaceView;
    private PlayManager playManager;

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
        eglSurfaceView = findViewById(R.id.eglSurfaceView);
        eglSurfaceView.setGlSurfaceListener(this);

        playManager = new PlayManager(this);

        findViewById(R.id.playSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playManager.play();
            }
        });
    }

    @Override
    public void onGLSurfaceCreated(SurfaceTexture surfaceTexture) {
        playManager.setSurfaceTexture(surfaceTexture);
        playManager.init(path);
    }

    @Override
    public void confirmPlaySize(Size playSize) {
        eglSurfaceView.confirmReallySize(playSize);
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
        eglSurfaceView.onDestroy();
        super.onDestroy();
    }
}
