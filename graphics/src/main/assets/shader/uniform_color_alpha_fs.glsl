#version 320 es

precision mediump float;

uniform float alpha;
uniform vec3 color;

out vec4 FragColor;

void main() {
    FragColor = vec4(color, 1.0f) * alpha;
}