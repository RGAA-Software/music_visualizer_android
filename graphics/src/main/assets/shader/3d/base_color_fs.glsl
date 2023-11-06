#version 320 es

precision mediump float;

in vec3 outColor;

uniform int enableAlpha;
uniform float alpha;

out vec4 FragColor;

void main() {
    FragColor = vec4(outColor, 1.0);
    if (enableAlpha == 1) {
        FragColor *= alpha;
    }
}