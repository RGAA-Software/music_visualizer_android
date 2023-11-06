#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;
uniform vec3 color;

out vec4 FragColor;

void main() {
    FragColor = texture(image, outTex) * vec4(color, 1.0);
}
