#version 300 es

layout (location = 0) in vec4 v_Position;
layout (location = 1) in vec4 v_texCoord;
layout (location = 2) uniform mat4 pos_matrix;

out vec2 out_texCoord;

void main() {
    gl_Position = v_Position * pos_matrix;
    out_texCoord = v_texCoord.xy;
}
