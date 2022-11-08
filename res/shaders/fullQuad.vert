#version 330

in vec2 inPos;
out vec2 origPos;

void main() {
    vec2 pos = inPos * 2 - 1;
    origPos = inPos;
    gl_Position = vec4(pos,0.f,1.f);
}