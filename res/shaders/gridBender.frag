#version 330

in vec2 origVertPos;
in vec3 computedVertPos;
in vec3 toLightVector;
in vec3 normalVector;
in vec3 toViewVector;
in mat3 TBN;

flat in int isPolar;
in float lightDistance;

uniform int u_ColorMode;
uniform int u_LightMode;
uniform bool u_useLight;
uniform bool u_useNormalMap;
uniform bool u_useSpecular;
uniform bool u_useDiffuse;
uniform bool u_useAmbient;
uniform float u_Time;

uniform sampler2D inTexture;
uniform sampler2D inTexNormal;

out vec4 finalOutColor;

// Combine all parts of Phong lighting with option to toggle
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

// Get normal from normal map
vec3 loadNormalMap() {
    vec4 norm = texture(inTexNormal, origVertPos);
    vec3 transNorm = 2 * (norm.rgb - 0.5);

    return transNorm;
}

void main() {

    vec4 outColor;

    // Toggle normal mapping
    vec3 normal = normalVector;

    if (u_useNormalMap) {
        normal = loadNormalMap();
    }


    // Different modes of object coloring
    // TEXTURED
    if (u_ColorMode == 0) {
        outColor = texture(inTexture, origVertPos);
    }
    // VIEW POSITION
    else if (u_ColorMode == 1) {
        vec3 colorView = - (toViewVector * 2);
        outColor = vec4(colorView.xyz,1.f);
    }
    // DEPTH
    else if (u_ColorMode == 2) {
        outColor = vec4(gl_FragCoord.www,1.f);
    }
    // TEX COORD
    else if (u_ColorMode == 3) {
        vec3 plasmaColor1 = vec3(1.9,0.55,0);
        vec3 plasmaColor2 = vec3(0.226,0.000,0.615);
        vec3 mixed = mix(plasmaColor1,plasmaColor2,sqrt((pow(origVertPos.x,2.f)+(pow(origVertPos.y,2.f)))));
        outColor = vec4(mixed, 1.f);
    }
    // LIGHT DIST
    else if (u_ColorMode == 4) {
        outColor = vec4(1-lightDistance/3,1-lightDistance/3,0, 1);
    }
    // NORMAL
    else if (u_ColorMode == 5) {
        outColor = vec4((normalize(normalVector) + 1.f) / 2.f, 1.f);
    }
    // NORMAL TEXTURE
    else if (u_ColorMode == 6) {
        outColor = texture(inTexNormal, origVertPos);
    }
    // ITALIA
    else if (u_ColorMode == 7) {
        outColor = vec4(origVertPos.x, origVertPos.y, origVertPos.x * origVertPos.y, 1);
    }
    // BLUE GREEN
    else if (u_ColorMode == 8) {
        outColor = vec4(origVertPos.x * origVertPos.y, origVertPos.x, origVertPos.y, 1);
    }
    // WHITE - LIGHT
    else if (u_ColorMode == 100) {
        outColor = vec4(1,1,1,1);
    }
    // DEFAULT RED
    else {
        outColor = vec4(1.f,0,0,1.f);
    }

    // LIGHT
    // Different ways to calculate light. ID 100 is lightsource, no calculation needed
    if (u_useLight && u_ColorMode != 100) {
        vec4 diffuseColor, ambientColor, specularColor;
        float shininess, ambientStrength, specularStrength;
        float constantAttenuation = 1;
        float linearAttenuation = 0.1;
        float quadraticAttenuation = 0.05;
        float att;
        float spotCutOff;
        vec3 spotDirection;


        bool isSpot = false;
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
        // SHINY WITH ATT ON VIEW
        else if (u_LightMode == 3) {
            diffuseColor = vec4(1,1,1,1);
            ambientColor = vec4(1,1,1,1);
            specularColor = vec4(1,1,1,1);
            shininess = 30.f;
            ambientStrength = 0.1;
            specularStrength = 1;
            linearAttenuation = 1.f;
            att = 1.f / (constantAttenuation +
            linearAttenuation * lightDistance +
            quadraticAttenuation * lightDistance * lightDistance);
        }
        // SPOT "FLASHLIGHT"
        else if (u_LightMode == 4) {
            diffuseColor = vec4(1,1,1,1);
            ambientColor = vec4(1,1,1,1);
            specularColor = vec4(1,1,1,1);
            shininess = 30.f;
            ambientStrength = 0.05;
            specularStrength = 1;
            att = 1.f / (constantAttenuation +
            linearAttenuation * lightDistance +
            quadraticAttenuation * lightDistance * lightDistance);
            isSpot = true;
            spotCutOff = 0.97;
            spotDirection = vec3(0.05*sin(u_Time/2),0.05*cos(u_Time),-1);
            if (u_useNormalMap){
                // Adjust for normal mapped objects
                spotDirection = spotDirection * TBN;
            }
        }
        // GREEN
        else if (u_LightMode == 5) {
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
        // Polar objects are bent "backwards", need to adjust
        if (isPolar == 1){
            nd = -normalize(normal);
        }
        else {
            nd = normalize(normal);
        }

        // Normalize vectors
        vec3 ld = normalize(toLightVector);

        vec3 vd = normalize(toViewVector);

        float NDotL = max(dot(nd,ld),0.f);

        // Hlaf vector for specular
        vec3 halfVector = normalize(ld + vd);
        float NDotH = max(0.0,dot(nd, halfVector));

        // Calculate parts
        vec4 specularPart = specularColor * specularStrength  * (pow(NDotH,shininess));

        vec4 diffusePart = NDotL * diffuseColor;

        vec4 ambientPart = ambientColor * ambientStrength;

        float spotEffect = dot(normalize(spotDirection),-ld);

        // If spot mode is on, limit the angle
        if (isSpot){
            float blend = clamp((spotEffect-spotCutOff)/(1-spotCutOff),0.0,1.0);
            vec4 phongColor = combinePhong(ambientPart, diffusePart, specularPart) * outColor * att;
            finalOutColor = mix(ambientPart*outColor,phongColor,blend);
        }
        else {
            finalOutColor = combinePhong(ambientPart, diffusePart, specularPart) * outColor * att;
        }


    }
    else {
        // Do nothing with the color - no light calculation
        finalOutColor = outColor;
    }

}
