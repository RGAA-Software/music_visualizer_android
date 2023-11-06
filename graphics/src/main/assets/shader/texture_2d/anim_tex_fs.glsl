#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;
uniform vec3 tintColor;

out vec4 FragColor;

void main() {
    vec4 color = texture(image, outTex);
    float alpha = color.a;
    vec3 rgbColor = color.rgb * tintColor;
    FragColor = vec4(rgbColor, alpha);
}
