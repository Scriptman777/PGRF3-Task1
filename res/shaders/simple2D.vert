#version 330

in vec2 inPos;

void main() {

    gl_Position = vec4(inPos,0.f,1.f);
}