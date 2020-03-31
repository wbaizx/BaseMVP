package com.camera_opengl.home.gl;

import android.opengl.GLES11Ext;
import android.opengl.GLES30;

import com.base.common.BaseAPP;
import com.base.common.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLHelper {
    private static final String TAG = "GLHelper";
    public static final int BYTES_PER_FLOAT = 4;

    private static String loadAssetsGlsl(String fileName) {
        BufferedReader bufReader = null;
        try {
            InputStreamReader inputReader = new InputStreamReader(
                    BaseAPP.baseAppContext.getResources().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder content = new StringBuilder();

            while ((line = bufReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            LogUtil.INSTANCE.log(TAG, content.toString());
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "null";
    }

    private static int shaderLoad(String fileName, int type) {
        String code = loadAssetsGlsl(fileName);
        int shader = GLES30.glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("编译顶点失败");
        }
        GLES30.glShaderSource(shader, code);
        GLES30.glCompileShader(shader);

        int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            GLES30.glDeleteShader(shader);
            throw new RuntimeException("编译顶点失败 " + GLES30.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    private static int linkLoad(int shaderV, int shaderF) {
        int program = GLES30.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("链接失败");
        }
        GLES30.glAttachShader(program, shaderV);
        GLES30.glAttachShader(program, shaderF);
        GLES30.glLinkProgram(program);

        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES30.glDeleteProgram(program);
            throw new RuntimeException("链接失败 " + GLES30.glGetProgramInfoLog(program));
        }

        LogUtil.INSTANCE.log(TAG, "编译链接成功");
        return program;
    }

    public static int compileAndLink(String fileNameV, String fileNameF) {
        //编译顶点着色器
        int shaderV = shaderLoad(fileNameV, GLES30.GL_VERTEX_SHADER);
        //编译片段着色器
        int shaderF = shaderLoad(fileNameF, GLES30.GL_FRAGMENT_SHADER);

        //链接
        int program = linkLoad(shaderV, shaderF);
        return program;
    }

    public static FloatBuffer getFloatBuffer(float[] point) {
        FloatBuffer floatBuffer = ByteBuffer
                .allocateDirect(point.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(point);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static void createExternalSurface(int[] texture) {
        GLES30.glGenTextures(1, texture, 0);
        if (texture[0] == 0) {
            throw new RuntimeException("创建外部纹理失败");
        } else {
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
            GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_NONE);
        }
        LogUtil.INSTANCE.log(TAG, "创建外部纹理成功 " + texture[0]);
    }

    public static void createFBOSurface(int[] fboTexture) {
        GLES30.glGenTextures(1, fboTexture, 0);
        if (fboTexture[0] == 0) {
            throw new RuntimeException("创建fbo挂载纹理失败");
        } else {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTexture[0]);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
        }
        LogUtil.INSTANCE.log(TAG, "创建fbo挂载纹理 " + fboTexture[0]);
    }
}
