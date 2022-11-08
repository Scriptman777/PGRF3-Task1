#version 330

in vec2 origPos;

uniform int time;

uniform sampler2D inTexture;

out vec4 outColor;

void main() {
    outColor = texture(inTexture, origPos);
}