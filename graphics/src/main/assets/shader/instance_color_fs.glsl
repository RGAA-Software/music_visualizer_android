#version 320 es

precision mediump float;

in float outInstanceAlpha;
in vec3 outInstanceColor;

out vec4 FragColor;

void main() {
    FragColor = vec4(outInstanceColor, 1.0) * outInstanceAlpha;
}