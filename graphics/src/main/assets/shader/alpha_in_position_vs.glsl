#version 320 es

//layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aColor;
layout(location=2) in vec2 aTex;
layout(location=3) in vec4 aPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform float pointSize;

out vec3 outColor;
out vec2 outTex;
out float outAlpha;

void main() {
    outColor = aColor;
    outTex = aTex;
    outAlpha = aPosition.w;
    gl_Position = projection * view * model * vec4(aPosition.xyz, 1);
    gl_PointSize = pointSize;
}