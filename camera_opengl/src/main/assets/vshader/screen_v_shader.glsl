#version 300 es

layout (location = 0) in vec4 v_Position;
layout (location = 1) in vec4 v_texCoord;

out vec2 out_texCoord;

void main() {
    gl_Position = v_Position;
    out_texCoord = v_texCoord.xy;
}
