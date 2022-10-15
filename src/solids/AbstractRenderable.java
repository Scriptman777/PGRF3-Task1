package solids;

import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;

import static org.lwjgl.opengl.GL20.glUseProgram;

public abstract class AbstractRenderable{
    private OGLBuffers buffers;

    protected int[] indexBuffer;

    protected float[] vertexBuffer;

    protected int preferredRenderMode;

    protected int identifier;

    public AbstractRenderable() {

    }

    public void draw(int shaderLoc){
        buffers.draw(this.getPreferedRenderMode(), shaderLoc);
    }

    public void initBuffers() {
        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[] {
                new OGLBuffers.Attrib("inPos",2)
                //,new OGLBuffers.Attrib("inColor",3)
        };

        buffers = new OGLBuffers(
                vertexBuffer,
                attribs,
                indexBuffer
        );
    }


    /**
     * Gets the indentifier for use in vertex shader
     * Specifies shape of the object
     * @return identifier as int
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the indentifier for use in vertex shader
     * Specifies shape of the object
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the vertex buffer for the Renderable
     *
     * @return vertexBuffer
     */
    public float[] getVertexBuffer() {
        return vertexBuffer;
    }

    /**
     * Gets the index buffer for the Renderable
     *
     * @return indexBuffer
     */
    public int[] getIndexBuffer() {
        return indexBuffer;
    }

    /**
     * Gets the render mode used for the Renderable
     *
     * @return Render mode
     */
    public int getPreferedRenderMode() {
        return preferredRenderMode;
    }

}
