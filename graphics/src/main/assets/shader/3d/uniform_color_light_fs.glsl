#version 320 es

precision mediump float;

in vec3 outColor;
in vec3 outPos;
in vec3 outNormal;
in vec3 outPosInWorld;

uniform int openFog;
uniform vec3 skyColor;
in float visibility;

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

uniform vec3 uColor;

out vec4 FragColor;

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
        //result += specColor * attenuation;
    }
    return result;
}

void main() {

    vec3 lightingColor = calculatePointLight(uColor);

    FragColor = vec4(lightingColor, 1.0);

    if (openFog == 1) {
        FragColor = mix(vec4(skyColor, 1.0), FragColor, visibility);
    }

    //FragColor = vec4(pointLights[0].color, 1.0);
    //FragColor = vec4(pointLights[0].position, 1.0);
}