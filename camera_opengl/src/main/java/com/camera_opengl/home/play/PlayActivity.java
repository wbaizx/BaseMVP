package com.camera_opengl.home.play;

import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.base.common.base.BaseActivity;
import com.camera_opengl.R;
import com.camera_opengl.home.gl.egl.EGLSurfaceView;
import com.camera_opengl.home.gl.egl.GLSurfaceListener;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

public class PlayActivity extends BaseActivity implements GLSurfaceListener, PlayListener {
    private static final String TAG = "PlayActivity";

    @Autowired
    String path;

    private EGLSurfaceView eglSurfaceView;
    private PlayManager playManager;

    private View playSwitch;

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

        playSwitch = findViewById(R.id.playSwitch);
        findViewById(R.id.eglSurfaceView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playManager.isReady()) {
                    playManager.play();
                    playSwitch.setVisibility(View.GONE);
                } else {
                    playManager.pause();
                    playSwitch.setVisibility(View.VISIBLE);
                }
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
    public void playEnd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playSwitch.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        playManager.onResume();
    }

    @Override
    protected void onPause() {
        playSwitch.setVisibility(View.VISIBLE);
        playManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        playManager.onDestroy();
        eglSurfaceView.onDestroy();
        super.onDestroy();
    }
}
