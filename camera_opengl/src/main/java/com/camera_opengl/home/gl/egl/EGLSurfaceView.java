package com.camera_opengl.home.gl.egl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES30;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.gl.GLHelper;
import com.camera_opengl.home.gl.renderer.BaseRenderer;
import com.camera_opengl.home.gl.renderer.FBORenderer;
import com.camera_opengl.home.gl.renderer.ScreenRenderer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "EGLSurfaceView";

    private GLThread glThread;


    public EGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        glThread = new GLThread();
        glThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        glThread.surfaceCreated(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        glThread.surfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        glThread.surfaceDestroyed();
    }

    public void onDestroy() {
        glThread.onDestroy();
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        glThread.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void takePicture() {
    }

    public void confirmCameraSize(Size cameraSize) {
        glThread.confirmCameraSize(cameraSize);
    }

    private static class GLThread extends Thread {
        private ReentrantLock look = new ReentrantLock();
        private Condition condition = look.newCondition();

        private boolean isDestroy = false;

        private boolean isSurfaceCreated = false;
        private boolean isFirstSurfaceCreated = false;

        private boolean isSurfaceChanged = false;
        private boolean isFirstSurfaceChanged = false;

        private boolean isSurfaceDestroyed = false;

        //SurfaceView的surface，用于屏幕显示
        private Surface surface;

        private EGLDisplay eglDisplay;
        private EGLConfig eglConfig;
        private EGLSurface screenEglSurface;
        private EGLSurface fboEglSurface;
        private EGLContext eglContext;

        //接收相机数据的纹理
        private int[] texture = new int[1];
        private SurfaceTexture surfaceTexture;

        //SurfaceView的宽高
        private int viewWidth;
        private int viewHeight;

        private SurfaceTextureListener surfaceTextureListener;
        private BaseRenderer fboRenderer = new FBORenderer();
        private BaseRenderer screenRenderer = new ScreenRenderer();

        public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
            this.surfaceTextureListener = surfaceTextureListener;
        }

        @Override
        public void run() {
            super.run();
            setName("mGLThread");

            LogUtil.INSTANCE.log(TAG, "GLThread  run");
            guardedRun();
            LogUtil.INSTANCE.log(TAG, "GLThread  run X");
        }

        public void surfaceCreated(Surface surface) {
            look.lock();

            this.isSurfaceCreated = true;
            this.isFirstSurfaceCreated = true;
            this.isSurfaceDestroyed = false;
            this.surface = surface;
            LogUtil.INSTANCE.log(TAG, "surfaceCreated");

            condition.signal();
            look.unlock();
        }

        public void surfaceChanged(int width, int height) {
            look.lock();

            this.isSurfaceChanged = true;
            this.isFirstSurfaceChanged = true;
            this.viewWidth = width;
            this.viewHeight = height;
            LogUtil.INSTANCE.log(TAG, "surfaceChanged  " + width + " -- " + height);

            condition.signal();
            look.unlock();
        }

        public void surfaceDestroyed() {
            look.lock();

            this.isSurfaceDestroyed = true;
            this.isSurfaceCreated = false;
            this.isSurfaceChanged = false;
            LogUtil.INSTANCE.log(TAG, "surfaceDestroyed");

            condition.signal();
            look.unlock();
        }

        public void onDestroy() {
            look.lock();

            this.isDestroy = true;
            this.isSurfaceDestroyed = true;
            this.isSurfaceCreated = false;
            this.isSurfaceChanged = false;
            LogUtil.INSTANCE.log(TAG, "onDestroy");

            condition.signal();
            look.unlock();
        }

        private void guardedRun() {
            LogUtil.INSTANCE.log(TAG, "guardedRun");
            try {
                look.lock();
                while (true) {
                    if (eglContext == null) {
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

                        LogUtil.INSTANCE.log(TAG, "eglCreateContext X");
                    }

                    if (isSurfaceCreated) {
                        //创建可显示的Surface
                        if (screenEglSurface == null) {
                            int[] surfaceAttribs = {EGL14.EGL_NONE};
                            screenEglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribs, 0);
                            if (screenEglSurface == EGL14.EGL_NO_SURFACE) {
                                throw new RuntimeException("create screenEglSurface fail");
                            }
                            LogUtil.INSTANCE.log(TAG, "create screenEglSurface X");
                        }

                        if (isSurfaceChanged) {
                            //创建离屏Surface
                            if (fboEglSurface == null) {
                                int[] surfaceAttribs = {EGL14.EGL_WIDTH, viewWidth, EGL14.EGL_HEIGHT, viewHeight, EGL14.EGL_NONE};
                                fboEglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs, 0);
                                if (fboEglSurface == EGL14.EGL_NO_SURFACE) {
                                    throw new RuntimeException("create fboEglSurface fail");
                                }
                                LogUtil.INSTANCE.log(TAG, "create fboEglSurface X");
                            }
                        }

                        //初始化准备完成，可以绑定EGL环境以及渲染
                        if (fboEglSurface != null) {
                            if (!EGL14.eglMakeCurrent(eglDisplay, fboEglSurface, fboEglSurface, eglContext)) {
                                throw new RuntimeException("eglMakeCurrent fail");
                            }

                            LogUtil.INSTANCE.log(TAG, "eglMakeCurrent X");

                            if (surfaceTexture == null) {
                                GLHelper.createExternalSurface(texture);
                                surfaceTexture = new SurfaceTexture(texture[0]);
                                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                                    @Override
                                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                                        LogUtil.INSTANCE.log(TAG, "onFrameAvailable");
                                        requestRender();
                                    }
                                });
                            }

                            if (isFirstSurfaceCreated) {
                                isFirstSurfaceCreated = false;

                                //回传surfaceTexture，控制相机打开
                                surfaceTextureListener.onSurfaceCreated(surfaceTexture);

                                fboRenderer.onSurfaceCreated();
                                fboRenderer.setInTexture(texture[0]);
                                fboRenderer.setSurfaceTexture(surfaceTexture);

                                screenRenderer.onSurfaceCreated();
                                screenRenderer.setInTexture(fboRenderer.getOutTexture());
                            }

                            if (isFirstSurfaceChanged) {
                                isFirstSurfaceChanged = false;

                                fboRenderer.onSurfaceChanged(viewWidth, viewHeight);
                                screenRenderer.onSurfaceChanged(viewWidth, viewHeight);
                            }


                            fboRenderer.onDrawFrame();
                            EGL14.eglSwapBuffers(eglDisplay, fboEglSurface);

                            if (!EGL14.eglMakeCurrent(eglDisplay, screenEglSurface, screenEglSurface, eglContext)) {
                                throw new RuntimeException("eglMakeCurrent fail");
                            }
                            screenRenderer.onDrawFrame();
                            EGL14.eglSwapBuffers(eglDisplay, screenEglSurface);
                        }
                    }

                    if (isSurfaceDestroyed) {
                        if (fboEglSurface != null || screenEglSurface != null) {
                            fboRenderer.onSurfaceDestroy();
                            screenRenderer.onSurfaceDestroy();

                            EGL14.eglDestroySurface(eglDisplay, fboEglSurface);
                            EGL14.eglDestroySurface(eglDisplay, screenEglSurface);
                            checkError("destroyedSurface");
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
                            LogUtil.INSTANCE.log(TAG, "delete camera texture X");
                        }

                        fboRenderer.onDestroy();
                        screenRenderer.onDestroy();

                        EGL14.eglDestroyContext(eglDisplay, eglContext);
                        checkError("destroyContext");
                        EGL14.eglTerminate(eglDisplay);
                        checkError("destroyDisplay");
                        return;
                    }

                    LogUtil.INSTANCE.log(TAG, "await");
                    condition.await();
                }
            } catch (InterruptedException e) {
                LogUtil.INSTANCE.log(TAG, "guardedRun  Exception");
            } finally {
                look.unlock();
            }
        }

        private void requestRender() {
            look.lock();
            LogUtil.INSTANCE.log(TAG, "requestRender");
            condition.signal();
            look.unlock();
        }

        private void checkError(String msg) {
            if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                LogUtil.INSTANCE.log(TAG, msg + " X");
            } else {
                throw new RuntimeException(msg + " fail");
            }
        }

        public void confirmCameraSize(Size cameraSize) {
            fboRenderer.confirmReallySize(cameraSize);
            screenRenderer.confirmReallySize(cameraSize);
        }
    }
}
