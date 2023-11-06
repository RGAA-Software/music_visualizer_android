#version 320 es

layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aColor;
layout(location=2) in vec2 aTex;
layout(location=3) in vec3 aNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform float pointSize;

out vec3 outColor;
out vec2 outTex;
out vec3 outNormal;
out vec3 outPos;
out vec3 outPosInWorld;

void main() {
    outColor = aColor;
    outTex = aTex;
    vec4 posInWorld = model * vec4(aPos, 1.0);
    outPosInWorld = vec3(posInWorld);
    outPos = aPos;
    outNormal = normalize(mat3(transpose(inverse(model))) * aNormal);
    //outNormal = normalize(mat3(model)*aNormal);
    gl_Position = projection * view * model * vec4(aPos, 1);
    gl_Position = gl_Position.xyww;
    gl_PointSize = pointSize;
}