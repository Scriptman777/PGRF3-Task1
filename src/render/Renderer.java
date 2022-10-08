package render;

import lwjglutils.ShaderUtils;
import model.Scene;
import solids.*;

import static org.lwjgl.opengl.GL33.*;


public class Renderer {

    private int tick = 100;
    private int shaderProg;
    private int uniformTime;
    private Scene scene = new Scene();

    public Renderer(){
        shaderProg = ShaderUtils.loadProgram("/shaders/gridBender");
        glUseProgram(shaderProg);
        uniformTime = glGetUniformLocation(shaderProg, "time");

        scene.add(new GridTriangleStrip(8, 8));

    }

    public void draw(){

        glUniform1i(uniformTime,this.tick);

        for (IRenderable solid: scene.getSolids()) {

            // Buffers
            int glVertexB = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER,glVertexB);
            glBufferData(GL_ARRAY_BUFFER,solid.getVertexBuffer(),GL_STATIC_DRAW);

            int glIndexB = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,glIndexB);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,solid.getIndexBuffer(),GL_STATIC_DRAW);

            // Draw solid
            glDrawElements(solid.getPreferedRenderMode(),solid.getIndexBuffer().length,GL_UNSIGNED_INT,0);

            // Position attrib
            int inPosIndex = glGetAttribLocation(shaderProg,"inPos");
            glVertexAttribPointer(inPosIndex,2,GL_FLOAT,false,2*Float.BYTES,0);
            glEnableVertexAttribArray(inPosIndex);

            /*
            COLOR
                int inColorIndex = glGetAttribLocation(shaderProg,"inColor");
                glVertexAttribPointer(inColorIndex,3,GL_FLOAT,false,5*Float.BYTES,2*Float.BYTES);
                glEnableVertexAttribArray(inColorIndex);
            */

            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        }

    }

}
