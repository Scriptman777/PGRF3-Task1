package render;

import constants.TexturePaths;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import model.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import solids.AbstractRenderable;
import solids.GridTriangleStrip;
import constants.ShapeIdents;
import solids.GridTriangles;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    private int shaderProgram;
    private Camera camera;
    private Mat4 projectionPersp;
    private AbstractRenderable lightBall = new GridTriangleStrip(50,50);

    private Mat4 projectionOrto;
    private OGLTexture2D texture;
    private Scene scene = new Scene();
    private long window;

    private int width, height;
    private double ox, oy;
    private boolean mouseButton1 = false;
    private boolean wireframe = false;
    private boolean useFP = true;
    private boolean usePersp = true;
    private boolean useLight = true;
    private float camSpeed = 0.05f;
    private float ratio = 6;
    private float time = 0;

    private AbstractRenderable mainObj;

    //Uniforms
    int loc_uColorMode;
    int loc_uView;
    int loc_uProj;
    int loc_uModel;
    int loc_uShape;
    int loc_uRatio;
    int loc_uTime;
    int loc_uLight;
    int loc_uCamPos;

    public Renderer(long window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;

        glEnable(GL_DEPTH_TEST);

        createScene();

        // MVP init
        camera = new Camera()
                .withPosition(new Vec3D(0.5f, -2f, 1.5f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));


        projectionPersp = new Mat4PerspRH(Math.PI / 3, 600 / (float)800, 0.1f, 50.f);
        projectionOrto = new Mat4OrthoRH(10,10,0.1f, 50.f);


        // Shader init
        shaderProgram = ShaderUtils.loadProgram("/shaders/gridBender");
        // Uniform loc get
        loc_uColorMode = glGetUniformLocation(shaderProgram, "u_ColorMode");
        loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        loc_uModel = glGetUniformLocation(shaderProgram, "u_Model");
        loc_uShape = glGetUniformLocation(shaderProgram, "u_shapeID");
        loc_uRatio = glGetUniformLocation(shaderProgram, "u_Ratio");
        loc_uTime = glGetUniformLocation(shaderProgram, "u_Time");
        loc_uLight = glGetUniformLocation(shaderProgram,"u_useLight");
        loc_uCamPos = glGetUniformLocation(shaderProgram,"u_CamPos");

        // Texture init
        try {
            texture = new OGLTexture2D(TexturePaths.METAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initControls();
    }


    private void createScene() {
        // Fill scene
        Mat4 tempModel;
        /*
        AbstractRenderable skybox = new GridTriangleStrip(50,50);
        skybox.setIdentifier(ShapeIdents.BALL);
        Mat4 tempModel = skybox.getModel();
        skybox.setModel(tempModel.mul(new Mat4Scale(10,10,10)));
        skybox.setColorMode(3);
        scene.add(skybox);



        AbstractRenderable hole = new GridTriangleStrip(50,50);
        hole.setIdentifier(ShapeIdents.HOLE_ANIM);
        tempModel = hole.getModel();
        hole.setModel(tempModel.mul(new Mat4RotXYZ(45,45,0).mul(new Mat4Transl(4,4,2.5))));
        hole.setColorMode(2);
        scene.add(hole);

        lightBall.setColorMode(6);
        scene.add(lightBall);

        */

        mainObj = new GridTriangleStrip(100,100);
        mainObj.setIdentifier(ShapeIdents.DONUT);
        mainObj.setColorMode(0);
        scene.add(mainObj);
    }


    public void draw() {

        if (wireframe) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        // Bind texture
        texture.bind();

        // Shader
        glUseProgram(shaderProgram);

        // Pass uniforms
        glUniform1f(loc_uRatio, ratio);
        glUniform1f(loc_uTime, time);
        glUniform3fv(loc_uCamPos, new float[] {(float) camera.getPosition().getX(),(float) camera.getPosition().getY(),(float) camera.getPosition().getZ()});
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());

        // No support for passing bool uniforms, workaround needed - GLSL treats bool as a special int
        glUniform1i(loc_uLight,useLight ? 1 : 0);

        // Change persp
        if (usePersp) {
            glUniformMatrix4fv(loc_uProj, false, projectionPersp.floatArray());
        }
        else {
            glUniformMatrix4fv(loc_uProj, false, projectionOrto.floatArray());
        }

        // Render scene
        for (AbstractRenderable renderable: scene.getSolids()) {
            glUniform1i(loc_uShape, renderable.getIdentifier());
            glUniform1i(loc_uColorMode, renderable.getColorMode());
            glUniformMatrix4fv(loc_uModel, false, renderable.getModel().floatArray());
            renderable.draw(shaderProgram);

        }
        // Advance time
        time+=0.01;
    }

    public void updateSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Initializes the window controls
     */
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

        // Mouse click (from samples)
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

        glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                if (dy < 0)
                    camera = camera.mulRadius(1.1f);
                else
                    camera = camera.mulRadius(0.9f);
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
                    case GLFW_KEY_K:
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
                    case GLFW_KEY_T:
                        mainObj.setColorMode(mainObj.getColorMode()+1);
                        break;
                    case GLFW_KEY_G:
                        mainObj.setColorMode(mainObj.getColorMode()-1);
                        break;
                    case GLFW_KEY_O:
                        wireframe = !wireframe;
                        break;
                    case GLFW_KEY_P:
                        usePersp = !usePersp;
                        break;
                    case GLFW_KEY_L:
                        useLight = !useLight;
                        break;

                }
            }
        });
    }

}

