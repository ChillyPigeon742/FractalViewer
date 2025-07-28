#version 460 core
uniform mat4 projection;
layout(location = 0) in vec2 position;

void main() {
    gl_Position = projection * vec4(position, 0.0, 1.0);
}
