#version 320 es

precision mediump float;

in vec3 outColor;

out vec4 FragColor;

void main() {
    float xDistance = 0.5 - gl_PointCoord.x;
    float yDistance = 0.5 - gl_PointCoord.y;
    float distance = sqrt(xDistance*xDistance + yDistance*yDistance);
    if (distance > 0.5) {
        discard;
    } else {
        FragColor = vec4(outColor, 1.0);
    }
}