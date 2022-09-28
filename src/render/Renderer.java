package render;

import lwjglutils.ShaderUtils;
import model.Scene;
import solids.DefaultTriangle;
import solids.DefaultTriangleColor;
import solids.ISolid;

import static org.lwjgl.opengl.GL33.*;


public class Renderer {

    private int tick = 0;
    private int shaderProg;
    private int uniformTime;

    public Renderer(){
        shaderProg = ShaderUtils.loadProgram("/shaders/simple2DColor");
        glUseProgram(shaderProg);
        uniformTime = glGetUniformLocation(shaderProg, "time");

    }

    public void draw(){

        tick += 1;
        glUniform1i(uniformTime,this.tick);

        ISolid tr = new DefaultTriangleColor();

        int glVertexB = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,glVertexB);

        glBufferData(GL_ARRAY_BUFFER,tr.getVertexBuffer(),GL_STATIC_DRAW);

        int glIndexB = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,glIndexB);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER,tr.getIndexBuffer(),GL_STATIC_DRAW);

        glDrawElements(GL_TRIANGLES,tr.getIndexBuffer().length,GL_UNSIGNED_INT,0);



        glVertexAttribPointer(0,2,GL_FLOAT,false,5*Float.BYTES,0);
        glVertexAttribPointer(1,3,GL_FLOAT,false,5*Float.BYTES,2*Float.BYTES);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);


    }

}
