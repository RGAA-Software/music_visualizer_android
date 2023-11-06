#version 320 es

precision mediump float;

in vec2 outTex;
in vec3 outPos;

uniform samplerCube cubeImage;

out vec4 FragColor;

void main() {
    FragColor = texture(cubeImage, outPos);
}
