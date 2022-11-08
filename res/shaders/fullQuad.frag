#version 330

in vec2 origPos;

uniform int time;

uniform sampler2D inTexture;

out vec4 outColor;

void main() {

    if (origPos.y < 0.1 || origPos.y > 0.9) {
        outColor = texture(inTexture, origPos).gbra;
    }
    else {
        outColor = texture(inTexture, origPos);
    }


}