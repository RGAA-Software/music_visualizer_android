#version 320 es

precision mediump float;

in vec3 outColor;
in vec3 outPosInWorld;
in vec3 outNormal;
in vec2 outTex;


uniform int pointLightSize;

struct PointLight {
    vec3 position;
    vec3 color;
    float constant;
    float linear;
    float quadratic;
};

const int maxPointLights = 16;
uniform PointLight pointLights[maxPointLights];

uniform vec3 cameraPosition;
// for shadow
uniform vec3 lightPosition;
uniform sampler2D shadowmap;

in vec4 outPosInLightSpace;

out vec4 FragColor;

vec4 calculateShadowMap() {
    vec3 pos = outPosInLightSpace.xyz/outPosInLightSpace.w;
    float s=(pos.s+1.0)/2.0;
    float t=(pos.t+1.0)/2.0;

    float current = pos.z;
    float origin = texture(shadowmap, vec2(s, t)).r;

    vec4 result = vec4(vec3(origin), 1.0);
    if (current > origin) {
        result.w = 1.0;
    } else {
        result.w = 0.0;
    }

    if (pos.z > 1.0) {
        result.w = 0.0;
    }

//    result.x = current;
//    result.y = current;
//    result.z = current;

    return result;
}


vec3 calculatePointLight(vec3 inColor) {
    vec3 result = vec3(0);

    vec3 ambient = vec3(0.1);
    result = ambient * inColor;
    for (int i = 0; i < pointLightSize; i++) {
        float distance = length(pointLights[i].position - outPosInWorld);
        float attenuation = 1.0/(pointLights[i].constant + pointLights[i].linear*distance + pointLights[i].quadratic*(distance*distance));

        vec3 toLightDir = normalize(pointLights[i].position - outPosInWorld);
        float cosTheta = max(dot(outNormal, toLightDir), 0.0);
        vec3 diffuseColor = cosTheta * pointLights[i].color * inColor;
        result += diffuseColor * attenuation;

        vec3 toCameraDir = normalize(cameraPosition - outPosInWorld);
        vec3 halfVec = normalize(toLightDir+toCameraDir);
        float halfFactor = max(dot(outNormal, halfVec), 0.0);
        float specFactor = pow(halfFactor, 128.0);
        vec3 specColor = specFactor * pointLights[i].color * inColor;
        result += specColor * attenuation;
    }
    return result;
}

void main() {

    vec3 lightingColor = calculatePointLight(outColor);
    //

    vec4 shadowmap = calculateShadowMap();

    FragColor = vec4(shadowmap.rgb, 1.0);

    lightingColor *= (shadowmap.w == 1.0 ? 0.5 : 1.0);
    FragColor = vec4(lightingColor, 1.0);
}