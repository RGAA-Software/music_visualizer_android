#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;

out vec4 FragColor;

void main() {
    FragColor = texture(image, outTex);
    FragColor = vec4(vec3(FragColor.r*0.3+FragColor.g*0.59+FragColor.b*0.11), 1.0);
}
