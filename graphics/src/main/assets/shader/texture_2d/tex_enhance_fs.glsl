#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;
uniform float enhance;

out vec4 FragColor;

void main() {
    FragColor = texture(image, outTex)*enhance;
}
