#version 320 es

layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aColor;
layout(location=2) in vec2 aTex;
layout(location=3) in mat4 instanceModel;
layout(location=7) in float instanceAlpha;
layout(location=8) in vec3 instanceColor;

uniform mat4 view;
uniform mat4 projection;

out vec3 outColor;
out vec2 outTex;
out float outInstanceAlpha;
out vec3 outInstanceColor;

void main() {
    outColor = aColor;
    outTex = aTex;
    outInstanceAlpha = instanceAlpha;
    outInstanceColor = instanceColor;
    gl_Position = projection * view * instanceModel * vec4(aPos, 1);
    gl_PointSize = 9.0;
}