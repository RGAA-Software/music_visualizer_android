#version 320 es

precision mediump float;

in vec2 outTex;

uniform sampler2D image;

uniform float colorEnhance;

out vec4 FragColor;

vec4 blur(sampler2D image, vec2 uv) {
    float disp = 0.;
    float intensity = .2;
    const int passes = 12;
    vec4 c1 = vec4(0.0);
    disp = intensity*(0.5-distance(0.5, .1));

    for (int xi=0; xi<passes; xi++) {
        float x = float(xi) / float(passes) - 0.5;
        for (int yi=0; yi<passes; yi++) {
            float y = float(yi) / float(passes) - 0.5;
            vec2 v = vec2(x, y);
            float d = disp;
            c1 += texture(image, uv + d*v);
        }
    }
    c1 /= float(passes*passes);
    c1.x += colorEnhance;
    c1.y += colorEnhance;
    c1.z += colorEnhance;
    return c1;
}

vec4 blur2(sampler2D image, vec2 uv) {
    float disp = 0.;
    float intensity = .1;
    const int passes = 6;
    vec4 c1 = vec4(0.0);
    disp = intensity*(0.5-distance(0.5, .1));

    for (int xi=0; xi<passes; xi++) {
        float x = float(xi) / float(passes) - 0.5;
        for (int yi=0; yi<passes; yi++) {
            float y = float(yi) / float(passes) - 0.5;
            vec2 v = vec2(x, y);
            float d = disp;
            c1 += texture(image, uv + d*v);
        }
    }
    c1 /= float(passes*passes);
    c1.x += colorEnhance;
    c1.y += colorEnhance;
    c1.z += colorEnhance;
    return c1;
}


void main()
{
    FragColor = blur2(image, outTex);
}