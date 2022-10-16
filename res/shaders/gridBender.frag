#version 330

in vec2 vertPos;

uniform int u_ColorMode;

out vec4 outColor;

void main() {

    // ITALIA
    if (u_ColorMode == 0) {
        outColor = vec4(vertPos.x, vertPos.y, vertPos.x*vertPos.y, 1);
    }
    else if (u_ColorMode == 1) {
        outColor = vec4(vertPos.x*vertPos.y, vertPos.x, vertPos.y, 1);
    }
    // DEFAULT RED
    else {
        outColor = vec4(1.f,0,0,1.f);
    }


}
