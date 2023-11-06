#version 320 es

precision mediump float;

in vec3 outColor;
in float outAlpha;

uniform vec2 center;
uniform float innerRadius;
uniform float ringWidth;

uniform int enableAlpha;
uniform int alphaForward;

out vec4 FragColor;

void main() {

    if (enableAlpha == 1) {
        float dist = distance(gl_FragCoord.xy, center);
        float alpha = (dist - innerRadius) / ringWidth;
        if (alphaForward == 0) {
            alpha = 1.0f - alpha;
        }
        FragColor = vec4(outColor, 1.0) * alpha;
    } else {
        FragColor = vec4(outColor, 1.0);
    }

}