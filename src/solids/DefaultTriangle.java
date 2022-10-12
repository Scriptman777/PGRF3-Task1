package solids;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class DefaultTriangle extends AbstractRenderable {



    public DefaultTriangle() {
        super();
        vertexBuffer = new float[] {
                -1, -1,
                1, 0,
                0, 1
        };
        indexBuffer = new int[] {0, 1, 2};
        preferredRenderMode = GL_TRIANGLES;
        initBuffers();
    }

}
