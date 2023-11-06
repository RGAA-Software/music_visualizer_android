#version 320 es

precision mediump float;

in vec3 outColor;

out vec4 FragColor;

void main() {
    FragColor = vec4(vec3(0.6, 0.5, 0.3), 1.0);
}