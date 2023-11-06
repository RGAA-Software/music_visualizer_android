#version 320 es

precision mediump float;

in vec2 outTex;
in float outInstanceAlpha;

uniform sampler2D image;

out vec4 FragColor;

void main() {
    FragColor = texture(image, outTex) * outInstanceAlpha;
}
