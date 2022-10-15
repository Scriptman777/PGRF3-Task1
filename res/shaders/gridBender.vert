#version 330
in vec2 inPos;

uniform mat4 u_View;
uniform mat4 u_Proj;
uniform int u_shapeID;
uniform float u_Ratio;

void main() {

    vec2 pos = inPos * u_Ratio - (u_Ratio/2);
    float x = pos.x;
    float y = pos.y;
    float z = 1.f;

    // DEFAULT
    if (u_shapeID == 0) {
        // Do nothing
    }
    // DONUT
    if (u_shapeID == 1) {
        float a = 2.f;
        float b = 0.5f;
        x = cos(pos.x)*(a + b*cos(pos.y));
        y = sin(pos.x)*(a + b*cos(pos.y));
        z = b*sin(pos.y);
    }
    // COS wave
    if (u_shapeID == 2) {
        z = 0.5 * cos(sqrt(20 * pow(pos.x, 2) + 20 * pow(pos.y, 2))) - 0.5;
    }
    // CANDY
    if (u_shapeID == 3) {
        x = cos(pos.x)*cos(pos.y);
        y = pos.y;
        z = sin(pos.x);
    }

    vec4 posMVP = u_Proj * u_View * vec4(x,y,z, 1.f);
    gl_Position = posMVP;
}

