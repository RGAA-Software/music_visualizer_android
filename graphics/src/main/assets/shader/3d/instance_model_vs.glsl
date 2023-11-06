#version 320 es

layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aColor;
layout(location=2) in vec2 aTex;
layout(location=3) in vec3 aNormal;
layout(location=4) in mat4 instanceModel;

uniform mat4 view;
uniform mat4 projection;

uniform float pointSize;

uniform float density;
uniform float gradient;
out float visibility;

out vec3 outColor;
out vec2 outTex;
out vec3 outNormal;
out vec3 outPos;
out vec3 outPosInWorld;

void main() {
    outColor = aColor;
    outTex = aTex;
    vec4 posInWorld = instanceModel * vec4(aPos, 1.0);
    outPosInWorld = vec3(posInWorld);
    outPos = aPos;
    outNormal = normalize(mat3(transpose(inverse(instanceModel))) * aNormal);
    gl_Position = projection * view * instanceModel * vec4(aPos, 1);
    gl_PointSize = pointSize;

    // for fog
    vec4 posInCamera = view * posInWorld;
    float distance = length(posInCamera);
    visibility = exp(-pow((distance*density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}