#version 320 es

precision mediump float;


out float color;

void main() {
    color = gl_FragCoord.z;
}