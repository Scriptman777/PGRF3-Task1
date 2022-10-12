package solids;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class DefaultTriangleColor extends AbstractRenderable {
    public DefaultTriangleColor() {
        super();
        vertexBuffer = new float[]{
                -1, -1, 1, 0, 0,
                1, 0, 0, 1, 0,
                0, 1, 0, 0, 1
        };
        indexBuffer = new int[]{0, 1, 2};
        preferredRenderMode = GL_TRIANGLES;
    }

}
