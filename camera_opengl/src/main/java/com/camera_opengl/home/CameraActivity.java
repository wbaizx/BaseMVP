package com.camera_opengl.home;

import android.Manifest;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.base.common.util.LogUtilKt;
import com.base.common.util.RouterUtilKt;
import com.camera_opengl.R;
import com.camera_opengl.home.camera.CameraControl;
import com.camera_opengl.home.camera.CameraControlListener;
import com.camera_opengl.home.gl.egl.EGLSurfaceView;
import com.camera_opengl.home.gl.egl.GLSurfaceListener;
import com.camera_opengl.home.gl.renderer.filter.FilterType;
import com.camera_opengl.home.record.RecordListener;
import com.camera_opengl.home.record.RecordManager;
import com.camera_opengl.home.videolist.VideoListActivity;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@Route(path = "/camera/camera_home", name = "组件化camera首页")
public class CameraActivity extends BaseActivity implements CameraControlListener, GLSurfaceListener, RecordListener {
    private static final String TAG = "CameraActivity";
    private final int CAMERA_PERMISSION_CODE = 666;

    private boolean hasPermissions = false;
    private boolean isResume = false;
    private boolean isSurfaceCreated = false;

    private CameraControl cameraControl;
    private EGLSurfaceView eglSurfaceView;
    private RecordManager recordManager;

    private ReentrantLock look = new ReentrantLock();

    private FilterDialog filterDialog;

    private SavePictureThread mSaveThread;

    @Override
    protected int getContentView() {
        return R.layout.activity_camera;
    }

    @Override
    protected void setImmersionBar() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
    }

    @Override
    protected void initView() {
        getPermissions();

        mSaveThread = new SavePictureThread();
        mSaveThread.start();

        cameraControl = new CameraControl(this, this);

        eglSurfaceView = findViewById(R.id.eglSurfaceView);
        eglSurfaceView.setGlSurfaceListener(this);
        eglSurfaceView.setCameraControlListener(this);

        recordManager = new RecordManager(this);


        findViewById(R.id.goVideoList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterUtilKt.launchActivity(CameraActivity.this, VideoListActivity.class, 1);
            }
        });

        findViewById(R.id.switchCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraControl.switchCamera();
            }
        });

        findViewById(R.id.takePicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cameraControl.takePicture();
                eglSurfaceView.takePicture();
            }
        });

        findViewById(R.id.switchFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });

        Button record = findViewById(R.id.record);
        findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordManager.isRecording()) {
                    recordManager.stopRecord();
                    record.setText("录制");
                } else if (recordManager.isReady()) {
                    recordManager.startRecord();
                    record.setText("停止录制");
                }
            }
        });
    }

    private void showBottomDialog() {
        if (filterDialog == null) {
            filterDialog = new FilterDialog();

            filterDialog.setOnItemClickListener(new FilterDialog.OnItemClickListener() {
                @Override
                public void onItemClick(FilterType type) {
                    eglSurfaceView.switchFilterType(type);
                }
            });

            filterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ImmersionBar.with(CameraActivity.this).hideBar(BarHide.FLAG_HIDE_BAR).init();
                }
            });

            filterDialog.init(this);
        }

        filterDialog.show();
    }

    @AfterPermissionGranted(CAMERA_PERMISSION_CODE)
    private void getPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) {
            begin();
        } else {
            EasyPermissions.requestPermissions(this, "为了正常使用，需要获取以下权限",
                    CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    protected void deniedPermission(int requestCode, @NotNull List<String> perms) {
        finish();
    }

    @Override
    protected void resultCheckPermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) {
            finish();
        } else {
            begin();
        }
    }

    private void begin() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        if (am != null && am.getDeviceConfigurationInfo().reqGlEsVersion >= 0x30000) {
            look.lock();

            LogUtilKt.log(TAG, "begin");
            hasPermissions = true;
            openCamera();

            look.unlock();
        } else {
            finish();
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        look.lock();

        LogUtilKt.log(TAG, "onResume");
        isResume = true;
        openCamera();

        Button record = findViewById(R.id.record);
        record.setText("录制");
        look.unlock();
    }

    /**
     * eglSurfaceView 供相机预览的 SurfaceTexture 创建完成回调
     *
     * @param surfaceTexture
     */
    @Override
    public void onGLSurfaceCreated(SurfaceTexture surfaceTexture) {
        cameraControl.setSurfaceTexture(surfaceTexture);

        look.lock();

        LogUtilKt.log(TAG, "onSurfaceCreated");
        isSurfaceCreated = true;
        openCamera();

        look.unlock();
    }

    private void openCamera() {
        LogUtilKt.log(TAG, "try openCamera " + hasPermissions + "-" + isResume + "-" + isSurfaceCreated);
        if (hasPermissions && isResume && isSurfaceCreated) {
            LogUtilKt.log(TAG, "openCamera");
            cameraControl.startCameraThread();
            cameraControl.openCamera();
        }
    }

    /**
     * CameraControl 预览大小确定回调
     * 因为是相机过来的数据，所以宽高需要对调
     *
     * @param cameraSize
     */
    @Override
    public void confirmCameraSize(Size cameraSize) {
        Size reallySize = new Size(cameraSize.getHeight(), cameraSize.getWidth());
        eglSurfaceView.confirmReallySize(reallySize);
        recordManager.confirmReallySize(reallySize);
    }

    @Override
    public void onEncoderSurfaceCreated(Surface surface) {
        eglSurfaceView.onEncoderSurfaceCreated(surface);
    }

    @Override
    public void onEncoderSurfaceDestroy() {
        eglSurfaceView.onEncoderSurfaceDestroy();
    }

    /**
     * 相机拍照数据回调
     *
     * @param horizontalMirror 是否需要水平镜像处理
     * @param verticalMirror   是否需要垂直镜像处理
     */
    @Override
    public void imageAvailable(byte[] bytes, boolean horizontalMirror, boolean verticalMirror) {
        mSaveThread.putData(bytes, horizontalMirror, verticalMirror);
    }

    /**
     * opengl 拍照数据回调
     */
    @Override
    public void imageAvailable(Bitmap btm, boolean horizontalMirror, boolean verticalMirror) {
        mSaveThread.putData(btm, horizontalMirror, verticalMirror);
    }

    @Override
    protected void onPause() {
        super.onPause();
        look.lock();

        LogUtilKt.log(TAG, "onPause");
        recordManager.onPause();

        if (hasPermissions && isResume && isSurfaceCreated) {
            cameraControl.closeCamera();
            cameraControl.stopCameraThread();
        }

        isResume = false;
        look.unlock();
    }

    @Override
    protected void onDestroy() {
        LogUtilKt.log(TAG, "onDestroy");
        recordManager.onDestroy();
        cameraControl.onDestroy();
        eglSurfaceView.onDestroy();
        mSaveThread.interrupt();
        super.onDestroy();
    }
}
