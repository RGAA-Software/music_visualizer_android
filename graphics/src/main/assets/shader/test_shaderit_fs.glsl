#version 320 es

precision mediump float;

uniform vec3 iResolution;
uniform float iTime;

out vec4 fragColor;

void main() {

    vec2 fragCoord = gl_FragCoord.xy;

    vec2 st = fragCoord/iResolution.xy;
    float aspect = iResolution.x/iResolution.y;

    float pcf = 0.0;
    vec3 color = vec3(0.0);
    float pct = 0.0;

    vec2 center = vec2(0.5, 0.5);
    center = vec2(abs(sin(iTime)), 0.5);
    vec2 dist = st - center;

    dist.x *= aspect;
    float toCenterLength = length(dist);

    pct = step(0.2, toCenterLength);
    color = vec3(1.0-pct);
    fragColor = vec4(color, 1.0);

    color = vec3(0.1, 0.1, 0.1);

    pct = smoothstep(0.0, abs(sin(iTime))*0.2, toCenterLength);
    color += vec3(1.0-pct) * vec3(1.0, 0.5, 0.2);
    fragColor = vec4(color, 1.0);

    pct = smoothstep(0.0, abs(cos(iTime))*0.2, toCenterLength);
    color += vec3(1.0-pct) * vec3(0.0, 0.5, 0.8);
    fragColor += vec4(color, 1.0);
}
