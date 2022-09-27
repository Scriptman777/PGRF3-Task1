package render;

import lwjglutils.ShaderUtils;
import solids.DefaultTriangle;
import static org.lwjgl.opengl.GL33.*;


public class Renderer {

    public Renderer(){
        int shaderProg = ShaderUtils.loadProgram("/shaders/simple2D");

        glUseProgram(shaderProg);
    }

    public void draw(){
        DefaultTriangle tr = new DefaultTriangle();

        int glVertexB = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,glVertexB);

        glBufferData(GL_ARRAY_BUFFER,tr.getVertexBuffer(),GL_STATIC_DRAW);

        int glIndexB = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,glIndexB);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER,tr.getIndexBuffer(),GL_STATIC_DRAW);

        glDrawElements(GL_TRIANGLES,tr.getIndexBuffer().length,GL_UNSIGNED_INT,0);

        glVertexAttribPointer(0,2,GL_FLOAT,false,2*Float.BYTES,0);
        glEnableVertexAttribArray(0);



    }

}
