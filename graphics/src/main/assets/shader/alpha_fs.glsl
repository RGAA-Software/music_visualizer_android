#version 320 es

precision mediump float;

in vec3 outColor;

uniform float alpha;

out vec4 FragColor;

void main() {
    FragColor = vec4(outColor, 1.0f) * alpha;
}