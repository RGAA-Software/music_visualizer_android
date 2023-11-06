#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;
uniform vec3 color;
uniform float alpha;

out vec4 FragColor;

void main() {
    vec4 tex = texture(image, outTex);
    vec3 tColor = tex.rgb;
    tColor = tColor * color;
    FragColor = vec4(tColor, tex.a) * alpha;
}
