#version 330
// Grid position
in vec2 inPos;

// Uniforms
uniform mat4 u_View;
uniform mat4 u_Proj;
uniform int u_shapeID;
uniform float u_Ratio;
uniform float u_Time;

// Function vars
float phi, theta, r, h;
float x,y,z;
vec2 pos;


out vec2 vertPos;


// Prepare for cartesian object
void initCartesian() {
    pos = inPos * u_Ratio - (u_Ratio/2);
    x = pos.x;
    y = pos.y;
    z = 1.f;
}

// Prepare for polar object
void initPolar() {
    phi = inPos.x * radians(360.f);
    theta = inPos.y * radians(180.f);
}

// Prepare for cylindrical object
void initCylindrical() {
    phi = inPos.x * radians(360.f);
    r = inPos.y * u_Ratio/6;
}

// Convert spherical coords to cartesian
void sphericalConvert() {
    x = r * sin(theta) * cos(phi);
    y = r * sin(theta) * sin(phi);
    z = r * cos(theta);
}

// Convert cylindrical coords to cartesian
void cylindricalConvert() {
    x = r * cos(phi);
    y = r * sin(phi);
    z = h;
}

void main() {

    // ======================
    // CARTESIAN OBJECTS
    // ======================

    // DEFAULT
    if (u_shapeID == 0) {
        initCartesian();
        // Do nothing - flat grid
    }
    // DONUT
    else if (u_shapeID == 1) {
        initCartesian();
        float a = 2.f;
        float b = 0.5f;
        x = cos(pos.x)*(a + b*cos(pos.y));
        y = sin(pos.x)*(a + b*cos(pos.y));
        z = b*sin(pos.y);
    }
    // COS wave
    else if (u_shapeID == 2) {
        initCartesian();
        z = 0.5 * cos(sqrt(20.f * pow(pos.x, 2.f) + 20 * pow(pos.y, 2.f)));
    }
    // COS wave anim
    else if (u_shapeID == 3) {
        initCartesian();
        z = cos(u_Time) * cos(sqrt(20.f * pow(pos.x, 2.f) + 20 * pow(pos.y, 2.f)));
    }
    // CANDY
    else if (u_shapeID == 4) {
        initCartesian();
        x = cos(pos.x)*cos(pos.y);
        y = pos.y;
        z = sin(pos.x);
    }

    // ======================
    // POLAR OBJECTS
    // ======================

    // FRUIT
    else if (u_shapeID == 5) {
        initPolar();
        r = 3.f*cos(4.f*phi);
        sphericalConvert();
    }
    // SHELL
    else if (u_shapeID == 6) {
        initPolar();
        r = sqrt(phi);
        sphericalConvert();
    }
    // HOLE
    else if (u_shapeID == 7) {
        initPolar();
        r = atanh(theta) + sin(u_Time);
        sphericalConvert();
    }
    // SPACESHIP
    else if (u_shapeID == 8) {
        initPolar();
        r = log(theta) + cos(phi*4);
        sphericalConvert();
    }
    // BALL
    else if (u_shapeID == 9) {
        initPolar();
        r = 1;
        sphericalConvert();
    }
    // PINECONE
    else if (u_shapeID == 10) {
        initPolar();
        r = cos(theta*6);
        sphericalConvert();
    }

    // ======================
    // CYLINDRICAL OBJECTS
    // ======================

    // SOMBRERO
    else if (u_shapeID == 11) {
        initCylindrical();
        h = sin(r*radians(360.f));
        cylindricalConvert();
    }
    // FLOWER
    else if (u_shapeID == 12) {
        initCylindrical();
        h = log(r)+sin(phi*6)*0.3;
        cylindricalConvert();
    }
    // AAAAAAA
    if (u_shapeID == 13) {
        initCylindrical();
        h = 1;
        cylindricalConvert();
    }



    vec4 posMVP = u_Proj * u_View * vec4(x,y,z,1.f);
    vertPos = inPos;
    gl_Position = posMVP;
}



