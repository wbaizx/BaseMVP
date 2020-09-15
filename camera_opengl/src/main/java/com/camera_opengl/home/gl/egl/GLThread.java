package com.camera_opengl.home.gl.egl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES30;
import android.util.Size;
import android.view.Surface;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.camera.CameraControlListener;
import com.camera_opengl.home.gl.GLHelper;
import com.camera_opengl.home.gl.renderer.CodecRenderer;
import com.camera_opengl.home.gl.renderer.FBORenderer;
import com.camera_opengl.home.gl.renderer.ScreenRenderer;
import com.camera_opengl.home.gl.renderer.filter.FilterType;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class GLThread extends Thread {
    private static final String TAG = "GL_Thread";

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    private boolean isDestroy = false;

    private boolean isSurfaceCreated = false;
    private boolean isFirstSurfaceCreated = false;

    private boolean isSurfaceChanged = false;
    private boolean isFirstSurfaceChanged = false;

    private boolean isSurfaceDestroyed = false;

    private boolean hasData = false;
    private boolean isConfirmReallySize = false;
    private Size reallySize;

    //SurfaceView的surface，用于屏幕显示
    private Surface surface;
    //编码器的输入Surface
    private Surface codecSurface;

    private EGLDisplay eglDisplay;
    private EGLConfig eglConfig;
    private EGLSurface screenEglSurface;
    private EGLSurface fboEglSurface;
    private EGLSurface codecEglSurface;
    private EGLContext eglContext;

    //接收外部数据的纹理
    private int[] texture = new int[1];
    private SurfaceTexture surfaceTexture;
    private float[] surfaceMatrix = new float[16];

    //SurfaceView的宽高
    private int viewWidth;
    private int viewHeight;

    private GLSurfaceListener glSurfaceListener;
    private CameraControlListener cameraControlListener;

    private FBORenderer fboRenderer = new FBORenderer();
    private ScreenRenderer screenRenderer = new ScreenRenderer();
    private CodecRenderer codecRenderer;

    private ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);

    public void setGlSurfaceListener(GLSurfaceListener glSurfaceListener) {
        this.glSurfaceListener = glSurfaceListener;
    }

    public void setCameraControlListener(CameraControlListener cameraControlListener) {
        this.cameraControlListener = cameraControlListener;
    }

    @Override
    public void run() {
        super.run();
        setName(getName() + "-GLThread");
        guardedRun();
        LogUtilKt.log(TAG, getName() + " close");
    }

    public void surfaceCreated(Surface surface) {
        look.lock();

        this.isSurfaceCreated = true;
        this.isFirstSurfaceCreated = true;
        this.isSurfaceDestroyed = false;
        this.surface = surface;
        LogUtilKt.log(TAG, "surfaceCreated");

        condition.signal();
        look.unlock();
    }

    public void surfaceChanged(int width, int height) {
        look.lock();

        this.isSurfaceChanged = true;
        this.isFirstSurfaceChanged = true;
        this.viewWidth = width;
        this.viewHeight = height;
        LogUtilKt.log(TAG, "surfaceChanged  " + width + " -- " + height);

        condition.signal();
        look.unlock();
    }

    public void surfaceDestroyed() {
        look.lock();

        this.isSurfaceDestroyed = true;
        this.isSurfaceCreated = false;
        this.isSurfaceChanged = false;
        LogUtilKt.log(TAG, "surfaceDestroyed");

        condition.signal();
        look.unlock();
    }

    public void onDestroy() {
        look.lock();

        this.isDestroy = true;
        this.isSurfaceDestroyed = true;
        this.isSurfaceCreated = false;
        this.isSurfaceChanged = false;
        LogUtilKt.log(TAG, "onDestroy");

        condition.signal();
        look.unlock();
    }

    private void guardedRun() {
        try {
            look.lock();
            while (true) {
                LogUtilKt.log(TAG, "guardedRun");

                while (!queue.isEmpty()) {
                    queue.poll().run();
                }

                if (eglContext == null) {
                    initEglContext();
                }

                if (isSurfaceCreated) {
                    //创建可显示的EglSurface
                    if (screenEglSurface == null) {
                        int[] surfaceAttribs = {EGL14.EGL_NONE};
                        screenEglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribs, 0);
                        if (screenEglSurface == EGL14.EGL_NO_SURFACE) {
                            throw new RuntimeException("create screenEglSurface fail");
                        }
                        LogUtilKt.log(TAG, "create screenEglSurface X");
                    }

                    if (isSurfaceChanged) {
                        //创建离屏EglSurface
                        if (fboEglSurface == null) {
                            //离屏分辨率是由fbo挂载纹理决定，这里的宽高使用的是控件宽高
                            int[] surfaceAttribs = {EGL14.EGL_WIDTH, viewWidth, EGL14.EGL_HEIGHT, viewHeight, EGL14.EGL_NONE};
                            fboEglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs, 0);
                            if (fboEglSurface == EGL14.EGL_NO_SURFACE) {
                                throw new RuntimeException("create fboEglSurface fail");
                            }
                            LogUtilKt.log(TAG, "create fboEglSurface X");
                        }

                        /*
                         * 离屏操作部分
                         */
                        if (!EGL14.eglMakeCurrent(eglDisplay, fboEglSurface, fboEglSurface, eglContext)) {
                            throw new RuntimeException("eglMakeCurrent fbo fail");
                        }
                        if (surfaceTexture == null) {
                            //创建外部纹理用于接收数据
                            GLHelper.createExternalTexture(texture);
                            surfaceTexture = new SurfaceTexture(texture[0]);
                            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                                @Override
                                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                                    requestRender();
                                }
                            });
                            //回传surfaceTexture
                            glSurfaceListener.onGLSurfaceCreated(surfaceTexture);
                        }

                        if (isFirstSurfaceCreated) {
                            fboRenderer.onSurfaceCreated();
                            fboRenderer.setInTexture(texture[0]);
                        }

                        if (isFirstSurfaceChanged) {
                            //离屏分辨率是由fbo挂载纹理决定，fbo中使用的是实际宽高，所以这里随意传
                            fboRenderer.onSurfaceChanged(0, 0);
                        }

                        if (isConfirmReallySize) {
                            //这里决定fbo挂载纹理分辨率，这里设置为实际宽高
                            //不会影响预览尺寸，但是过小会导致模糊
                            //读取像素模式的拍照是从fbo挂载纹理中读取，所以这里的宽高决定读取的图片的宽高
                            fboRenderer.confirmReallySize(reallySize);
                        }

                        if (hasData) {
                            surfaceTexture.updateTexImage();
                            surfaceTexture.getTransformMatrix(surfaceMatrix);
                            fboRenderer.onDrawFrame(surfaceMatrix);
                            EGL14.eglSwapBuffers(eglDisplay, fboEglSurface);
                        }

                        /*
                         * 显示操作部分
                         */
                        if (!EGL14.eglMakeCurrent(eglDisplay, screenEglSurface, screenEglSurface, eglContext)) {
                            throw new RuntimeException("eglMakeCurrent screen fail");
                        }

                        if (isFirstSurfaceCreated) {
                            screenRenderer.onSurfaceCreated();
                            screenRenderer.setInTexture(fboRenderer.getOutTexture());
                        }

                        if (isFirstSurfaceChanged) {
                            screenRenderer.onSurfaceChanged(viewWidth, viewHeight);
                        }

                        if (isConfirmReallySize) {
                            screenRenderer.confirmReallySize(reallySize);
                        }

                        if (hasData) {
                            screenRenderer.onDrawFrame();
                            EGL14.eglSwapBuffers(eglDisplay, screenEglSurface);
                        }

                        /*
                         * 需要编码器输入部分
                         */
                        if (codecSurface != null) {
                            //创建编码器的EglSurface
                            if (codecEglSurface == null) {
                                int[] surfaceAttribs = {EGL14.EGL_NONE};
                                codecEglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, codecSurface, surfaceAttribs, 0);
                                if (codecEglSurface == EGL14.EGL_NO_SURFACE) {
                                    throw new RuntimeException("create codecEglSurface fail");
                                }
                                LogUtilKt.log(TAG, "create codecEglSurface X");
                            }

                            if (!EGL14.eglMakeCurrent(eglDisplay, codecEglSurface, codecEglSurface, eglContext)) {
                                throw new RuntimeException("eglMakeCurrent codec fail");
                            }

                            if (codecRenderer == null && reallySize != null) {
                                codecRenderer = new CodecRenderer();
                                codecRenderer.onSurfaceCreated();
                                codecRenderer.setInTexture(fboRenderer.getOutTexture());
                                //编码器分辨率需要使用实际宽高，所以这里随意传
                                codecRenderer.onSurfaceChanged(0, 0);
                                //编码器分辨率需要使用实际宽高
                                codecRenderer.confirmReallySize(reallySize);
                                LogUtilKt.log(TAG, "init codecRenderer");
                            }

                            if (hasData && codecRenderer != null) {
                                codecRenderer.onDrawFrame();
                                EGL14.eglSwapBuffers(eglDisplay, codecEglSurface);
                            }
                        }

                        //一次性变量重置
                        isFirstSurfaceCreated = false;
                        isFirstSurfaceChanged = false;
                        hasData = false;
                        isConfirmReallySize = false;
                    }
                }

                if (isSurfaceDestroyed) {
                    if (fboEglSurface != null || screenEglSurface != null) {
                        fboRenderer.onSurfaceDestroy();
                        screenRenderer.onSurfaceDestroy();
                        LogUtilKt.log(TAG, "destroyedSurface");

                        EGL14.eglDestroySurface(eglDisplay, fboEglSurface);
                        EGL14.eglDestroySurface(eglDisplay, screenEglSurface);
                        GLHelper.eglGetError("destroyedSurface");

                        fboEglSurface = null;
                        screenEglSurface = null;
                    }
                }

                if (isDestroy) {
                    if (surfaceTexture != null) {
                        GLES30.glDeleteTextures(texture.length, texture, 0);
                        if (surfaceTexture != null) {
                            surfaceTexture.release();
                        }
                        surfaceTexture = null;
                        LogUtilKt.log(TAG, "delete camera texture X");
                    }
                    LogUtilKt.log(TAG, "onDestroy");
                    fboRenderer.onDestroy();
                    screenRenderer.onDestroy();

                    EGL14.eglDestroyContext(eglDisplay, eglContext);
                    GLHelper.eglGetError("destroyContext");
                    EGL14.eglTerminate(eglDisplay);
                    GLHelper.eglGetError("destroyDisplay");
                    eglContext = null;
                    cameraControlListener = null;
                    return;
                }

                LogUtilKt.log(TAG, "await");
                condition.await();
            }
        } catch (InterruptedException e) {
            LogUtilKt.log(TAG, "guardedRun  Exception");
        } finally {
            look.unlock();
        }
    }

    /**
     * 初始化egl环境
     */
    private void initEglContext() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay fail");
        }

        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw new RuntimeException("eglInitialize fail");
        }

        int[] configAttributes = {
                EGL14.EGL_BUFFER_SIZE, 32,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE};

        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(eglDisplay, configAttributes, 0, configs,
                0, configs.length, numConfigs, 0)) {
            throw new RuntimeException("eglInitialize fail");
        }

        //如果没有配置的Config
        if (numConfigs[0] < 0) {
            throw new RuntimeException("no Config");
        }

        eglConfig = configs[0];
        if (eglConfig == null) {
            throw new RuntimeException("eglConfig is null");
        }

        //指定OpenGL 版本 3
        int[] contextAttributes = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE};
        //创建EGLContext上下文
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT,
                contextAttributes, 0);
        //需要检测Context是否存在
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("eglCreateContext fail");
        }

        LogUtilKt.log(TAG, "eglCreateContext X");
    }

    /**
     * 此方法一定会在数据来之前回调
     *
     * @param reallySize
     */
    public void confirmReallySize(Size reallySize) {
        look.lock();

        isConfirmReallySize = true;
        this.reallySize = reallySize;
        LogUtilKt.log(TAG, "confirmCameraSize");

        condition.signal();
        look.unlock();
    }

    private void requestRender() {
        look.lock();

        hasData = true;
        LogUtilKt.log(TAG, "requestRender");

        condition.signal();
        look.unlock();
    }

    /**
     * 编码surface创建完成
     *
     * @param surface
     */
    public void onEncoderSurfaceCreated(Surface surface) {
        LogUtilKt.log(TAG, "onEncoderSurfaceCreated");
        codecSurface = surface;
    }

    /**
     * 停止录制，销毁EncoderSurface相关
     */
    public void onEncoderSurfaceDestroy() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (eglContext != null && codecEglSurface != null) {
                    if (!EGL14.eglMakeCurrent(eglDisplay, codecEglSurface, codecEglSurface, eglContext)) {
                        throw new RuntimeException("eglMakeCurrent codec fail");
                    }

                    codecRenderer.onSurfaceDestroy();
                    codecRenderer.onDestroy();

                    EGL14.eglDestroySurface(eglDisplay, codecEglSurface);
                    GLHelper.eglGetError("destroyedCodecSurface");

                    codecSurface = null;
                    codecEglSurface = null;
                    codecRenderer = null;

                    LogUtilKt.log(TAG, "onEncoderSurfaceDestroy");
                }
            }
        });
    }

    /**
     * 添加需要再gl线程中执行的任务
     *
     * @param event
     */
    public void queueEvent(Runnable event) {
        look.lock();

        queue.offer(event);
        LogUtilKt.log(TAG, "queueEvent");

        condition.signal();
        look.unlock();
    }

    /**
     * 拍照，直接从opengl中读取像素
     */
    public void takePicture() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (isSurfaceCreated && fboEglSurface != null && eglContext != null) {
                    if (!EGL14.eglMakeCurrent(eglDisplay, fboEglSurface, fboEglSurface, eglContext)) {
                        throw new RuntimeException("eglMakeCurrent takePicture fail");
                    }
                    //在Android平台中，Bitmap绑定的2D纹理，是上下颠倒的
                    if (cameraControlListener != null) {
                        cameraControlListener.imageAvailable(fboRenderer.takePicture(), false, true);
                    }
                }
            }
        });
    }

    public void switchFilterType(FilterType type) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                fboRenderer.switchFilterType(type);
            }
        });
    }
}