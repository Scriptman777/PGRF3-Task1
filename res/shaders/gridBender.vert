#version 330
// Grid position
in vec2 inPos;

// Uniforms
uniform mat4 u_Model;
uniform mat4 u_View;
uniform mat4 u_Proj;
uniform vec3 u_CamPos;

uniform int u_shapeID;
uniform float u_Ratio;
uniform float u_Time;

out vec2 origVertPos;
out vec3 computedVertPos;

out vec3 toLightVector;
out vec3 normalVector;
out vec3 toViewVector;


// Prepare for cartesian object
vec3 initCartesian(vec2 coords) {
    float x_cart, y_cart, z_cart;
    vec3 tempPos = vec3(coords, 1.f);
    vec3 pos = tempPos * u_Ratio - (u_Ratio/2);
    x_cart = pos.x;
    y_cart = pos.y;
    z_cart = 1.f;
    return vec3(x_cart, y_cart, z_cart);
}

// Prepare for polar object
vec3 initPolar(vec2 coords) {
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
        calcPos.x = cos(calcPos.x)*(a + b*cos(calcPos.y));
        calcPos.y = sin(calcPos.x)*(a + b*cos(calcPos.y));
        calcPos.z = b*sin(calcPos.y);
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
        calcPos.y = calcPos.y;
        calcPos.z = sin(calcPos.x);
        calcPos.x = cos(calcPos.x)*cos(calcPos.y);
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
        calcPos.z = 3.f*cos(4.f*calcPos.x);
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
        calcPos.z = log(calcPos.y) + cos(calcPos.x*4);
        calcPos = sphericalConvert(calcPos);
    }
    // BALL
    else if (u_shapeID == 9) {
        calcPos = initPolar(pos);
        calcPos.z = 1.f;
        calcPos = sphericalConvert(calcPos);
    }
    // PINECONE
    else if (u_shapeID == 10) {
        calcPos = initPolar(pos);
        calcPos.z = cos(calcPos.y*6);
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

vec3 getNormal() {

    float diffU, diffV = 0.01;

    vec3 dU = getPosition(vec2(inPos.x + diffU,inPos.y)) - getPosition(vec2(inPos.x - diffU,inPos.y));
    vec3 dV = getPosition(vec2(inPos.x,inPos.y + diffV)) - getPosition(vec2(inPos.x,inPos.y - diffV));

    //return cross(dU, dV);



    return vec3(0,0,1);
}

void main() {

    vec3 transformedPos = getPosition(inPos);



    // LIGHT


    // Position in view coords
    vec4 objectPositionVM = u_View * u_Model * vec4(transformedPos, 1.f);


    vec4 lightSourcePos = u_View * u_Model * vec4(vec3(0, 0, 0.2), 1.f); // This will be uniform

    toLightVector = lightSourcePos.xyz - objectPositionVM.xyz;

    mat3 normalMatrix = transpose(inverse(mat3(u_Model * u_View)));

    normalVector = normalMatrix * getNormal();

    toViewVector = - objectPositionVM.xyz;

    // Proj and pass
    vec4 postMVP = u_Proj * objectPositionVM;
    origVertPos = inPos;
    gl_Position = postMVP;
}



