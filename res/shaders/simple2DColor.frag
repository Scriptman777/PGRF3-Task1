#version 330

in vec3 vertColor;

uniform int time;

out vec4 outColor;

void main() {
    outColor = vec4(vertColor.x*abs(sin(time/100)), vertColor.y, vertColor.z, 1.f);

}