package render;

import constants.TexturePaths;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import model.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import solids.AbstractRenderable;
import solids.GridTriangleStrip;
import constants.ShapeIdents;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    private int shaderProgram;
    private int shaderProgramPost;
    private Camera camera;
    private Mat4 projectionPersp;
    private AbstractRenderable lightBall = new GridTriangleStrip(50,50);

    private Mat4 projectionOrto;
    private OGLTexture2D texture;
    private OGLTexture2D textureNormal;
    private Scene scene = new Scene();
    private long window;

    private int width, height;
    private int lightMode = 0;
    private double ox, oy;
    private boolean mouseButton1 = false;
    private int renderingMode = 0;
    private boolean usePersp = true;
    private boolean useLight = true;
    private boolean useAmbient = true;
    private boolean useDiffuse = true;
    private boolean useSpecular = true;
    private float camSpeed = 0.05f;
    private float ratio = 7;
    private float time = 0;
    private float lightHeight = 0.7f;

    private AbstractRenderable mainObj;
    private AbstractRenderable fullQuad;

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
    int loc_uLightPos;
    int loc_uLightMode;
    int loc_uUseAmbient;
    int loc_uUseDiffuse;
    int loc_uUseSpecular;

    private OGLRenderTarget renderTarget;

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
        shaderProgramPost = ShaderUtils.loadProgram("/shaders/fullQuad");
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
        loc_uLightPos = glGetUniformLocation(shaderProgram,"u_LightPos");
        loc_uLightMode = glGetUniformLocation(shaderProgram,"u_LightMode");
        loc_uUseAmbient = glGetUniformLocation(shaderProgram,"u_useAmbient");
        loc_uUseDiffuse = glGetUniformLocation(shaderProgram,"u_useDiffuse");
        loc_uUseSpecular = glGetUniformLocation(shaderProgram,"u_useSpecular");

        // Texture init
        try {
            texture = new OGLTexture2D(TexturePaths.BRICKAI);
            textureNormal = new OGLTexture2D(TexturePaths.BRICKS_NORM);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initControls();

        renderTarget = new OGLRenderTarget(width,height);
    }


    private void createScene() {
        // Fill scene
        Mat4 tempModel;
        fullQuad = new GridTriangleStrip(2,2);


        AbstractRenderable pinecone = new GridTriangleStrip(50,50);
        pinecone.setIdentifier(ShapeIdents.PINECONE);
        tempModel = pinecone.getModel();
        pinecone.setModel(tempModel.mul(new Mat4Transl(4,4,2.5)));
        pinecone.setColorMode(0);
        scene.add(pinecone);




        lightBall.setColorMode(100);
        lightBall.setIdentifier(ShapeIdents.LIGHT);
        scene.add(lightBall);

        mainObj = new GridTriangleStrip(300,300);
        mainObj.setIdentifier(ShapeIdents.DONUT);
        mainObj.setColorMode(0);
        scene.add(mainObj);
    }


    public void draw() {

        switch (renderingMode) {
            case 0:
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                break;
            case 1:
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                break;
            case 2:
                glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
                break;
        }

        // Bind texture
        texture.bind(shaderProgram,"inTexture",0);
        textureNormal.bind(shaderProgram,"inTexNormal",1);



        // Shader
        glUseProgram(shaderProgram);

        // Pass uniforms
        glUniform1f(loc_uRatio, ratio);
        glUniform1f(loc_uTime, time);
        glUniform1i(loc_uLightMode, lightMode);
        glUniform3fv(loc_uCamPos, new float[] {(float) camera.getPosition().getX(),(float) camera.getPosition().getY(),(float) camera.getPosition().getZ()});
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());

        // No support for passing bool uniforms, workaround needed - GLSL treats bool as a special int
        glUniform1i(loc_uLight,useLight ? 1 : 0);
        glUniform1i(loc_uUseAmbient,useAmbient ? 1 : 0);
        glUniform1i(loc_uUseDiffuse,useDiffuse ? 1 : 0);
        glUniform1i(loc_uUseSpecular,useSpecular ? 1 : 0);

        // Move light
        glUniform3fv(loc_uLightPos, new float[] {0, (float) (3.5*Math.sin(time/2)), lightHeight});
        lightBall.setModel(new Mat4Transl(0,3.5*Math.sin(time/2),lightHeight));


        // Change persp
        if (usePersp) {
            glUniformMatrix4fv(loc_uProj, false, projectionPersp.floatArray());
        }
        else {
            glUniformMatrix4fv(loc_uProj, false, projectionOrto.floatArray());
        }



        // To texture
        renderTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(shaderProgram);
        // Render scene
        for (AbstractRenderable renderable: scene.getSolids()) {
            glUniform1i(loc_uShape, renderable.getIdentifier());
            glUniform1i(loc_uColorMode, renderable.getColorMode());
            glUniformMatrix4fv(loc_uModel, false, renderable.getModel().floatArray());
            renderable.draw(shaderProgram);

        }


        // To quad
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        renderTarget.getColorTexture().bind(shaderProgramPost,"inTexture",0);
        glUseProgram(shaderProgramPost);
        fullQuad.draw(shaderProgramPost);



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
                        lightMode++;
                        break;
                    case GLFW_KEY_F:
                        if (lightMode >= 1){
                            lightMode--;
                        }
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
                        if (mainObj.getColorMode() >= 1) {
                            mainObj.setColorMode(mainObj.getColorMode()-1);
                        }
                        break;
                    case GLFW_KEY_O:
                        renderingMode = renderingMode == 2 ? 0 : renderingMode + 1;
                        break;
                    case GLFW_KEY_KP_1:
                        useAmbient = !useAmbient;
                        break;
                    case GLFW_KEY_KP_2:
                        useDiffuse = !useDiffuse;
                        break;
                    case GLFW_KEY_KP_3:
                        useSpecular = !useSpecular;
                        break;
                    case GLFW_KEY_P:
                        usePersp = !usePersp;
                        break;
                    case GLFW_KEY_L:
                        useLight = !useLight;
                        break;
                    case GLFW_KEY_KP_MULTIPLY:
                        lightHeight += 0.1;
                        break;
                    case GLFW_KEY_KP_DIVIDE:
                        lightHeight -= 0.1;
                        break;
                    case GLFW_KEY_Y:
                        mainObj.setIdentifier(mainObj.getIdentifier()+1);
                        break;
                    case GLFW_KEY_H:
                        if (mainObj.getIdentifier() >= 1) {
                            mainObj.setIdentifier(mainObj.getIdentifier()-1);
                        }
                        break;
                    case GLFW_KEY_KP_8:
                        mainObj.setModel(mainObj.getModel().mul(new Mat4RotX(0.1)));
                        break;
                    case GLFW_KEY_KP_5:
                        mainObj.setModel(mainObj.getModel().mul(new Mat4RotX(-0.1)));
                        break;
                    case GLFW_KEY_KP_4:
                        mainObj.setModel(mainObj.getModel().mul(new Mat4RotY(0.1)));
                        break;
                    case GLFW_KEY_KP_6:
                        mainObj.setModel(mainObj.getModel().mul(new Mat4RotY(-0.1)));
                        break;

                }
            }
        });
    }

}

