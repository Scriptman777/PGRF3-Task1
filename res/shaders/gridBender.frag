#version 330

in vec2 origVertPos;
in vec3 computedVertPos;

uniform int u_ColorMode;

uniform sampler2D inTexture;

out vec4 outColor;


vec3 plasmaColor1 = vec3(1.9,0.55,0);
vec3 plasmaColor2 = vec3(0.226,0.000,0.615);

void main() {

    // ITALIA
    if (u_ColorMode == 0) {
        outColor = vec4(origVertPos.x, origVertPos.y, origVertPos.x * origVertPos.y, 1);
    }
    // BLUE GREEN
    else if (u_ColorMode == 1) {
        outColor = vec4(origVertPos.x * origVertPos.y, origVertPos.x, origVertPos.y, 1);
    }
    // PLASMA - DIAGONAL
    else if (u_ColorMode == 2) {
        vec3 mixed = mix(plasmaColor1,plasmaColor2,sqrt((pow(origVertPos.x,2.f)+(pow(origVertPos.y,2.f)))));
        outColor = vec4(mixed, 1.f);
    }
    // Texture
    else if (u_ColorMode == 3) {
        outColor = texture(inTexture, origVertPos);
    }
    // DEFAULT RED
    else {
        outColor = vec4(1.f,0,0,1.f);
    }


}
