#version 330
in vec2 inPos;

uniform mat4 u_View;
uniform mat4 u_Proj;

void main() {

    float ratio = 7;
    vec2 pos = inPos * ratio - (ratio/2);
    // Cos wave
    //float z = 0.5 * cos(sqrt(20 * pow(pos.x, 2) + 20 * pow(pos.y, 2))) - 0.5;
    float a = 2.f;
    float b = 0.5f;

    // Donut
    float x = cos(pos.x)*(a + b*cos(pos.y));
    float y = sin(pos.x)*(a + b*cos(pos.y));
    float z = b*sin(pos.y);


    vec4 posMVP = u_Proj * u_View * vec4(x,y,z, 1.f);
    gl_Position = posMVP;
}

