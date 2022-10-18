#version 330

in vec2 origVertPos;
in vec3 computedVertPos;
in vec3 toLightVector;
in vec3 normalVector;

uniform int u_ColorMode;

uniform sampler2D inTexture;

out vec4 finalOutColor;

void main() {

    vec3 plasmaColor1 = vec3(1.9,0.55,0);
    vec3 plasmaColor2 = vec3(0.226,0.000,0.615);

    vec4 outColor;

    // ITALIA
    if (u_ColorMode == 0) {
        outColor = vec4(origVertPos.x, origVertPos.y, origVertPos.x * origVertPos.y, 1);
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
        outColor = vec4(0,0,0,1);
    }
    // DEFAULT RED
    else {
        outColor = vec4(1.f,0,0,1.f);
    }


    // LIGHT
    vec4 lightColor = vec4(0,1,0,1);

    // DIFFUSE
    vec3 ld = normalize(toLightVector);
    vec3 nd = normalize(normalVector);

    float NDotL = max(dot(nd,ld),0);

    vec4 diffuse = NDotL * lightColor;


    finalOutColor = diffuse * outColor;
    //finalOutColor = (diffuse + specular + ambient) * outColor;


}
