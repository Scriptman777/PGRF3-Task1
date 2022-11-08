#version 330

in vec2 inPos;

out vec3 vertColor;

void main() {
    vertColor = vec3(1.f,0,0);
    vec2 pos = inPos * 2 - 1;
    gl_Position = vec4(pos,0.f,1.f);
}