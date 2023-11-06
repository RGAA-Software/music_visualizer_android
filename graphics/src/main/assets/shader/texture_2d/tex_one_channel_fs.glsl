#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;

out vec4 FragColor;

void main() {
    vec3 color = texture(image, outTex).rrr;
    FragColor = vec4(color, 1.0f);
}
