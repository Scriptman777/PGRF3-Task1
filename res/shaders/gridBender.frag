#version 330

in vec2 origVertPos;
in vec3 computedVertPos;
in vec3 toLightVector;
in vec3 normalVector;
in vec3 toViewVector;

uniform int u_ColorMode;
uniform bool u_useLight;

uniform sampler2D inTexture;

out vec4 finalOutColor;

void main() {

    vec3 plasmaColor1 = vec3(1.9,0.55,0);
    vec3 plasmaColor2 = vec3(0.226,0.000,0.615);

    vec4 outColor;


    // NORMAL
    if (u_ColorMode == 0) {
        outColor = vec4((normalize(normalVector) + 1.f) / 2.f, 1.f);
    }
    // BLUE GREEN
    else if (u_ColorMode == 1) {
        outColor = vec4(origVertPos.x * origVertPos.y, origVertPos.x, origVertPos.y, 1);
    }
    // PLASMA - DIAGONAL
    else if (u_ColorMode == 2) {
        vec3 mixed = mix(plasmaColor1,plasmaColor2,sqrt((pow(origVertPos.x,2.f)+(pow(origVertPos.y,2.f)))));
        outColor = vec4(mixed, 1.f);
    }
    // TEXTURE
    else if (u_ColorMode == 3) {
        outColor = texture(inTexture, origVertPos);
    }
    // BLACK
    else if (u_ColorMode == 4) {
        outColor = vec4(0.1,0.1,0.1,1);
    }
    // ITALIA
    else if (u_ColorMode == 5) {
        outColor = vec4(origVertPos.x, origVertPos.y, origVertPos.x * origVertPos.y, 1);
    }
    // WHITE - LIGHT
    else if (u_ColorMode == 6) {
        outColor = vec4(1,1,1,1);
    }
    // DEFAULT RED
    else {
        outColor = vec4(1.f,0,0,1.f);
    }

    // LIGHT
    if (u_useLight && u_ColorMode != 6) {
        vec4 lightColor = vec4(1,1,1,1);
        float shininess = 8;
        float ambient = 0.1;
        float specularStrength = 3;

        // DIFFUSE
        vec3 ld = normalize(toLightVector);
        vec3 nd = -normalize(normalVector);
        vec3 vd = normalize(toViewVector);

        float NDotL = max(dot(nd,ld),0.f);



        vec3 halfVector = normalize(ld + vd);
        float NDotH = max(0.0,dot(nd, halfVector));

        vec4 specularPart = vec4(1,0,0,1) * specularStrength  * (pow(NDotH,shininess));

        vec4 diffusePart = NDotL * lightColor;

        vec4 ambientPart = lightColor * ambient;


        //finalOutColor = (diffusePart + specularPart + ambientPart) * outColor;
        finalOutColor = (diffusePart + ambientPart) * outColor;
        //finalOutColor = diffusePart * outColor;
        //finalOutColor = specularPart * outColor;
    }
    else {
        // Do nothing with the color
        finalOutColor = outColor;
    }



}
