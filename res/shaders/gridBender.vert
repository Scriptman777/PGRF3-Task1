#version 330
// Grid position
in vec2 inPos;

// Uniforms
uniform mat4 u_Model;
uniform mat4 u_View;
uniform mat4 u_Proj;
uniform vec3 u_CamPos;
uniform vec3 u_LightPos;
uniform int u_LightMode;

uniform int u_shapeID;
uniform float u_Ratio;
uniform float u_Time;

out vec2 origVertPos;
out vec3 computedVertPos;

out vec3 toLightVector;
out vec3 normalVector;
out vec3 toViewVector;
out float lightDistance;
out mat3 TBN;


// Bool varying not allowed, has to be int...
flat out int isPolar;

uniform sampler2D inTexNormal;

float diff = 0.001;
vec3 tangentVector, biTangentVector;

// Prepare for cartesian object
vec3 initCartesian(vec2 coords) {
    isPolar = 0;
    float x_cart, y_cart, z_cart;
    vec2 pos = coords * u_Ratio - (u_Ratio/2);
    x_cart = pos.x;
    y_cart = pos.y;
    z_cart = 1.f;
    return vec3(x_cart, y_cart, z_cart);
}

// Prepare for polar object
vec3 initPolar(vec2 coords) {
    isPolar = 1;
    float phi, theta;
    phi = coords.x * radians(360.f);
    theta = coords.y * radians(180.f);
    return vec3(phi, theta, 1.f);
}

// Prepare for cylindrical object
vec3 initCylindrical(vec2 coords) {
    float phi, r;
    phi = inPos.x * radians(360.f);
    r = inPos.y * u_Ratio/6;
    return vec3(phi, r, 1.f);
}

// Convert spherical coords to cartesian
vec3 sphericalConvert(vec3 calcPos) {
    float x,y,z;
    float phi = calcPos.x;
    float theta = calcPos.y;
    float r = calcPos.z;

    x = r * sin(theta) * cos(phi);
    y = r * sin(theta) * sin(phi);
    z = r * cos(theta);
    return vec3(x,y,z);
}

// Convert cylindrical coords to cartesian
vec3 cylindricalConvert(vec3 calcPos) {
    float x,y,z;
    float phi = calcPos.x;
    float r = calcPos.y;
    float h = calcPos.z;
    x = r * cos(phi);
    y = r * sin(phi);
    z = h;
    return vec3(x,y,z);
}

vec3 getPosition(vec2 pos){

    float x,y,z;
    vec3 calcPos;

    // ======================
    // CARTESIAN OBJECTS
    // ======================

    // DONUT
    if (u_shapeID == 1) {
        calcPos = initCartesian(pos);
        float a = 2.f;
        float b = 0.5f;
        float x = calcPos.x;
        float y = calcPos.y;
        float z = calcPos.z;
        calcPos.x = cos(x)*(a + b*cos(y));
        calcPos.y = sin(x)*(a + b*cos(y));
        calcPos.z = b*sin(y);
    }
    // COS wave
    else if (u_shapeID == 2) {
        calcPos = initCartesian(pos);
        calcPos.z = 0.5 * cos(sqrt(20.f * pow(calcPos.x, 2.f) + 20 * pow(calcPos.y, 2.f)));
    }
    // COS wave anim
    else if (u_shapeID == 3) {
        calcPos = initCartesian(pos);
        calcPos.z = cos(u_Time) * cos(sqrt(20.f * pow(calcPos.x, 2.f) + 20 * pow(calcPos.y, 2.f)));
    }
    // CANDY
    else if (u_shapeID == 4) {
        calcPos = initCartesian(pos);
        calcPos.z = cos(calcPos.y*abs(sin(u_Time)));
    }

    // ======================
    // POLAR OBJECTS
    // x - phi
    // y - theta
    // z - r
    // ======================

    // FRUIT
    else if (u_shapeID == 5) {
        calcPos = initPolar(pos);
        calcPos.z = abs(cos(4.f*calcPos.x));
        calcPos = sphericalConvert(calcPos);
    }
    // SHELL
    else if (u_shapeID == 6) {
        calcPos = initPolar(pos);
        calcPos.z = sqrt(calcPos.x);
        calcPos = sphericalConvert(calcPos);
    }
    // HOLE
    else if (u_shapeID == 7) {
        calcPos = initPolar(pos);
        calcPos.z = atanh(calcPos.y) + sin(u_Time);
        calcPos = sphericalConvert(calcPos);
    }
    // SPACESHIP
    else if (u_shapeID == 8) {
        calcPos = initPolar(pos);
        calcPos.z = abs(log(calcPos.y) + cos(calcPos.x*4));
        calcPos = sphericalConvert(calcPos);
    }
    // BALL
    else if (u_shapeID == 9) {
        calcPos = initPolar(pos);
        calcPos.z = 1.f;
        calcPos = sphericalConvert(calcPos);
    }
    // BALL - LIGHT SOURCE
    else if (u_shapeID == 111) {
        calcPos = initPolar(pos);
        calcPos.z = 0.1;
        calcPos = sphericalConvert(calcPos);
    }
    // PINECONE
    else if (u_shapeID == 10) {
        calcPos = initPolar(pos);
        calcPos.z = abs(cos(calcPos.y*6));
        calcPos = sphericalConvert(calcPos);
    }

    // ======================
    // CYLINDRICAL OBJECTS
    // x - phi
    // y - r
    // z - h
    // ======================

    // SOMBRERO
    else if (u_shapeID == 11) {
        calcPos = initCylindrical(pos);
        calcPos.z = sin(calcPos.y*radians(360.f))/2;
        calcPos = cylindricalConvert(calcPos);
    }
    // FLOWER
    else if (u_shapeID == 12) {
        calcPos = initCylindrical(pos);
        calcPos.z = log(calcPos.y)+sin(calcPos.x*6)*0.3;
        calcPos = cylindricalConvert(calcPos);
    }
    // AAAAAAA
    else if (u_shapeID == 13) {
        calcPos = initCylindrical(pos);
        calcPos.z = pow(calcPos.y,3.f)-1;
        calcPos = cylindricalConvert(calcPos);
    }
    // DEFAULT
    else {
        calcPos = initCartesian(pos);
        // Do nothing - flat grid
    }


    return calcPos;
}

vec3 getNormal(float x, float y) {
    vec3 dx = vec3(getPosition(vec2(x + diff, y)) - getPosition(vec2(x, y)));
    vec3 dy = vec3(getPosition(vec2(x, y + diff)) - getPosition(vec2(x, y)));
    return cross(dx, dy);
}

vec3 getTangent(float x, float y) {
    return vec3(getPosition(vec2(x + diff, y)) - getPosition(vec2(x, y)));
}

void main() {

    vec3 transformedPos = getPosition(inPos);

    // LIGHT

    // Position in view coords
    mat4 VM = u_View * u_Model;

    vec4 objectPositionVM = VM * vec4(transformedPos, 1.f);
    vec4 lightSourcePos;
    if (u_LightMode == 3 || u_LightMode == 4) {
        // Light on viewer
        lightSourcePos = vec4(0,0,0,1.f);
    }
    else {
        lightSourcePos = u_View * vec4(u_LightPos, 1.f);
    }


    toLightVector = lightSourcePos.xyz - objectPositionVM.xyz;
    toViewVector = - objectPositionVM.xyz;

    lightDistance = length(toLightVector);




    mat3 normalMatrix = transpose(inverse(mat3(VM)));

    normalVector = normalize(normalMatrix * getNormal(inPos.x, inPos.y));
    tangentVector = normalize(mat3(VM) * getTangent(inPos.x, inPos.y));
    biTangentVector = normalize(cross(tangentVector, normalVector));


    TBN = mat3(normalize(tangentVector), normalize(biTangentVector), normalVector);

    // Convert to tangent space
    toLightVector = toLightVector * TBN;
    toViewVector = toViewVector * TBN;

    // Proj and pass
    vec4 postMVP = u_Proj * objectPositionVM;
    origVertPos = inPos;
    gl_Position = postMVP;
}



