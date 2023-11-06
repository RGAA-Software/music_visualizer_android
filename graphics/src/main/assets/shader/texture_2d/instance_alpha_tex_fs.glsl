#version 320 es

precision mediump float;

in vec2 outTex;
in float outInstanceAlpha;

uniform sampler2D image;
uniform int enableTintColor;
uniform vec3 tintColor;

out vec4 FragColor;

void main() {
    if (enableTintColor == 1) {
        FragColor = texture(image, outTex) * outInstanceAlpha * vec4(tintColor, 1.0);
    } else {
        FragColor = texture(image, outTex) * outInstanceAlpha;
    }
}
