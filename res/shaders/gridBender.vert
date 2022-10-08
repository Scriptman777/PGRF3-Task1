#version 330

in vec2 inPos;
//in vec3 inColor;

//uniform int time;

out vec3 vertColor;

void main() {
    vertColor = vec3(1.f,0,0);
    gl_Position = vec4(inPos.x-0.5,inPos.y-0.5,0.f,1.f);
}