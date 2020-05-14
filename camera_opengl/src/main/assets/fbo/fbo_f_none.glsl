#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

in vec2 out_texCoord;
out vec4 outColor;

//如果需要给gl传值，着色器中写法如下
//layout (location = 4) uniform int filterType;
//这里传递int值，java中使用使用glUniform1i
//如果传递float，java中使用glUniform1f
//另外在着色器中，如果是float写法必须带小数点0.0

uniform samplerExternalOES f_texture;

void main(){
    outColor = texture(f_texture, out_texCoord);
}

