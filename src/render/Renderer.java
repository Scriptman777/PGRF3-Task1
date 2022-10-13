package render;

import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import model.Scene;
import solids.*;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL33.*;


public class Renderer {

    private int tick = 100;
    private int uniformTime;
    private int uniformProj;
    private int uniformView;
    private Scene scene = new Scene();

    private Camera camera = new Camera()
            .withPosition(new Vec3D(0.5f,-2f,1.5f))
            .withAzimuth(Math.toRadians(90))
            .withZenith(Math.toRadians(-45));

    private Mat4 projection = new Mat4PerspRH(Math.PI,600/(float)800,0.1f, 50.f);
    private int shaderProg;

    public Renderer(){

        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

        scene.add(new GridTriangleStrip(10, 10));


        shaderProg = ShaderUtils.loadProgram("/shaders/gridBender");

        //uniformTime = glGetUniformLocation(shaderProg, "u_Time");
        //glUniform1f(uniformTime,this.tick);

        uniformProj = glGetUniformLocation(shaderProg, "u_Proj");
        glUniformMatrix4fv(uniformProj, false, projection.floatArray());

        uniformView = glGetUniformLocation(shaderProg, "u_View");
        glUniformMatrix4fv(uniformView, false, camera.getViewMatrix().floatArray());

        glUseProgram(shaderProg);
    }

    public void draw(){

        for (AbstractRenderable solid: scene.getSolids()) {
            solid.draw(shaderProg);
        }

    }

}
