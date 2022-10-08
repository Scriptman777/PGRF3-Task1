package solids;

public interface IRenderable {

    /**
     * Gets the vertex buffer for the Renderable
     * @return vertexBuffer
     */
    float[] getVertexBuffer();

    /**
     * Gets the index buffer for the Renderable
     * @return indexBuffer
     */
    int[] getIndexBuffer();

    /**
     * Gets the render mode used for the Renderable
     * @return Render mode
     */
    int getPreferedRenderMode();


}
