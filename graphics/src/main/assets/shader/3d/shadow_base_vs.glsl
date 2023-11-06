#version 320 es

layout(location=0) in vec3 aPos;

uniform mat4 model;
uniform mat4 shadowView;
uniform mat4 shadowProj;



void main() {

    gl_Position = shadowProj * shadowView * model * vec4(aPos, 1);

}