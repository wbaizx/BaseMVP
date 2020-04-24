#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

in vec2 out_texCoord;
out vec4 outColor;
uniform sampler2D f_texture;

void main() {
    outColor = texture(f_texture, out_texCoord);
}
