package render;

import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import model.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import solids.AbstractRenderable;
import solids.GridTriangleStrip;
import solids.GridTriangles;
import solids.ShapeIdents;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.awt.event.KeyEvent;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    private int shaderProgram;
    private Camera camera;
    private Mat4 projection;
    private OGLTexture2D texture;
    private Scene scene = new Scene();
    private long window;
    int width, height;
    double ox, oy;
    private boolean mouseButton1 = false;
    float camSpeed = 0.05f;
    float ratio = 6;
    float time = 0;

    //Uniforms
    int loc_uColorR;
    int loc_uView;
    int loc_uProj;
    int loc_uShape;
    int loc_uRatio;
    int loc_uTime;

    public Renderer(long window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Fill scene
        AbstractRenderable newObj = new GridTriangleStrip(40,40);
        newObj.setIdentifier(ShapeIdents.DONUT);
        scene.add(newObj);

        // MVP init
        camera = new Camera()
                .withPosition(new Vec3D(0.5f, -2f, 1.5f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));
        projection = new Mat4PerspRH(Math.PI / 3, 600 / (float)800, 0.1f, 50.f);


        // Shader init
        shaderProgram = ShaderUtils.loadProgram("/shaders/gridBender");
        // Uniform loc get
        loc_uColorR = glGetUniformLocation(shaderProgram, "u_ColorR");
        loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        loc_uShape = glGetUniformLocation(shaderProgram, "u_shapeID");
        loc_uRatio = glGetUniformLocation(shaderProgram, "u_Ratio");
        loc_uTime = glGetUniformLocation(shaderProgram, "u_Time");

        initControls();
    }



    public void draw() {

        glUseProgram(shaderProgram);

        glUniform1f(loc_uRatio, ratio);
        glUniform1f(loc_uTime, time);
        glUniform1f(loc_uColorR, 1.f);
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());

        for (AbstractRenderable renderable: scene.getSolids()) {

            glUniform1i(loc_uShape, renderable.getIdentifier());
            renderable.draw(shaderProgram);

        }
        time+=0.01;
    }

    private void initControls() {


        // Mose move (from samples)
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                            .addZenith((double) Math.PI * (oy - y) / width);
                    ox = x;
                    oy = y;
                }
            }
        });

        // Mose click (from samples)
        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

                if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                    mouseButton1 = true;
                    DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                    DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                    glfwGetCursorPos(window, xBuffer, yBuffer);
                    ox = xBuffer.get(0);
                    oy = yBuffer.get(0);
                }

                if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                    mouseButton1 = false;
                    DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                    DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                    glfwGetCursorPos(window, xBuffer, yBuffer);
                    double x = xBuffer.get(0);
                    double y = yBuffer.get(0);
                    camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                            .addZenith((double) Math.PI * (oy - y) / width);
                    ox = x;
                    oy = y;
                }
            }

        });

        // Movement keys (based on samples)
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key) {
                    case GLFW_KEY_W:
                        camera = camera.forward(camSpeed);
                        break;
                    case GLFW_KEY_D:
                        camera = camera.right(camSpeed);
                        break;
                    case GLFW_KEY_S:
                        camera = camera.backward(camSpeed);
                        break;
                    case GLFW_KEY_A:
                        camera = camera.left(camSpeed);
                        break;
                    case GLFW_KEY_LEFT_CONTROL:
                        camera = camera.down(camSpeed);
                        break;
                    case GLFW_KEY_LEFT_SHIFT:
                        camera = camera.up(camSpeed);
                        break;
                    case GLFW_KEY_SPACE:
                        camera = camera.withFirstPerson(!camera.getFirstPerson());
                        break;
                    case GLFW_KEY_R:
                        camera = camera.mulRadius(0.9f);
                        break;
                    case GLFW_KEY_F:
                        camera = camera.mulRadius(1.1f);
                        break;
                    case GLFW_KEY_KP_ADD:
                        ratio+=0.5;
                        break;
                    case GLFW_KEY_KP_SUBTRACT:
                        ratio-=0.5;
                        break;
                }
            }
        });
    }
}

