#version 330

in vec2 origVertPos;
in vec3 computedVertPos;
in vec3 toLightVector;
in vec3 normalVector;
in vec3 toViewVector;

flat in int isPolar;
in float lightDistance;

uniform int u_ColorMode;
uniform int u_LightMode;
uniform bool u_useLight;
uniform bool u_useSpecular;
uniform bool u_useDiffuse;
uniform bool u_useAmbient;

uniform sampler2D inTexture;

out vec4 finalOutColor;

vec4 combinePhong(vec4 ambientPart, vec4 diffusePart, vec4 specularPart) {
    vec4 finalPhong = vec4(0.f,0.f,0.f,0.f);
    // Allow toggle for parts
    if (u_useAmbient){
        finalPhong = finalPhong + ambientPart;
    }
    if (u_useDiffuse){
        finalPhong = finalPhong + diffusePart;
    }
    if (u_useSpecular){
        finalPhong = finalPhong + specularPart;
    }
    return finalPhong;
}

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
        vec4 diffuseColor, ambientColor, specularColor;
        float shininess, ambientStrength, specularStrength;
        float constantAttenuation = 1;
        float linearAttenuation = 1;
        float quadraticAttenuation = 0.5;
        float att;

        // MATTE WITH ATT
        if (u_LightMode == 0) {
            diffuseColor = vec4(1,1,1,1);
            ambientColor = vec4(1,1,1,1);
            specularColor = vec4(1,1,1,1);
            shininess = 8.f;
            ambientStrength = 0.1;
            specularStrength = 0.1;
            att = 1.f / (constantAttenuation +
            linearAttenuation * lightDistance +
            quadraticAttenuation * lightDistance * lightDistance);
        }
        // SHINY
        else if (u_LightMode == 1) {
            diffuseColor = vec4(1,1,1,1);
            ambientColor = vec4(1,1,1,1);
            specularColor = vec4(1,1,1,1);
            shininess = 30.f;
            ambientStrength = 0.1;
            specularStrength = 1;
            att = 1.f;
        }
        // EXTREMELY SHINY
        else if (u_LightMode == 2) {
            diffuseColor = vec4(1,1,1,1);
            ambientColor = vec4(1,1,1,1);
            specularColor = vec4(1,1,1,1);
            shininess = 80.f;
            ambientStrength = 0.1;
            specularStrength = 5;
            att = 1.f;
        }
        // GREEN COLORED
        else if (u_LightMode == 3) {
            diffuseColor = vec4(0.5,0.9,0.5,1);
            ambientColor = vec4(0.5,0.9,0.5,1);
            specularColor = vec4(0.5,0.9,0.5,1);
            shininess = 8.f;
            ambientStrength = 0.1;
            specularStrength = 0.1;
            att = 1.f;
        }
        // OTHER - MATTE
        else {
            diffuseColor = vec4(1,1,1,1);
            ambientColor = vec4(1,1,1,1);
            specularColor = vec4(1,1,1,1);
            shininess = 8.f;
            ambientStrength = 0.1;
            specularStrength = 0.1;
            att = 1.f;
        }


        // DIFFUSE
        vec3 nd;
        if (isPolar == 1){
            nd = -normalize(normalVector);
        }
        else {
            nd = normalize(normalVector);
        }

        vec3 ld = normalize(toLightVector);

        vec3 vd = normalize(toViewVector);

        float NDotL = max(dot(nd,ld),0.f);



        vec3 halfVector = normalize(ld + vd);
        float NDotH = max(0.0,dot(nd, halfVector));

        vec4 specularPart = specularColor * specularStrength  * (pow(NDotH,shininess));

        vec4 diffusePart = NDotL * diffuseColor;

        vec4 ambientPart = ambientColor * ambientStrength;

        finalOutColor = combinePhong(ambientPart, diffusePart, specularPart) * outColor * att;

    }
    else {
        // Do nothing with the color
        finalOutColor = outColor;
    }

}
