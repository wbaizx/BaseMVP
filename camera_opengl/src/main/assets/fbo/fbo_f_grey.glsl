#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

in vec2 out_texCoord;
out vec4 outColor;

uniform samplerExternalOES f_texture;

//灰度滤镜
vec4 grey(inout vec4 color){
    float weightMean = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    color.r = color.g = color.b = weightMean;
    return color;
}

void main(){
    vec4 tmpColor = texture(f_texture, out_texCoord);

    //灰度滤镜
    outColor = grey(tmpColor);
}

