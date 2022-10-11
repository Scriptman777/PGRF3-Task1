package solids;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class Pyramid extends AbstractRenderable {

    public Pyramid() {
        super();
        vertexBuffer = new float[]{
                -0.8f, -0.8f, -0.8f,
                -0.8f, 0.8f, -0.8f,
                0.8f, -0.8f, -0.8f,
                0, 0, 0
        };
        indexBuffer = new int[]{
                0, 1, 2,
                1, 2, 3,
                2, 3, 4,
                4, 0, 1
        };
        preferredRenderMode = GL_LINES;
    }

}
