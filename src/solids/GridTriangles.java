package solids;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GridTriangles implements IRenderable {

    private float[] vertexBuffer;
    private int[] indexBuffer;

    public GridTriangles(int totalCols, int totalRows) {
        vertexBuffer = new float[totalRows*totalCols*2];
        indexBuffer = new int[6*(totalRows-1)*(totalCols-1)];

        //Verts
        int index = 0;
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {

                float newX = col / (float) (totalCols-1);
                vertexBuffer[index] = newX;

                float newY = row / (float) (totalRows-1);
                vertexBuffer[index+1] = newY;

                index +=2;
            }
        }

        //Indices
        index = 0;
        for (int row = 0; row < totalRows-1; row++) {
            for (int col = 0; col < totalCols-1; col++) {

                int wholeRows = row*totalCols;

                // Triangle 1

                int newIn = col + wholeRows;
                indexBuffer[index++] = newIn;

                newIn = col + totalCols + wholeRows;
                indexBuffer[index++] = newIn;

                newIn = col + 1 + wholeRows;
                indexBuffer[index++] = newIn;

                // Triangle 2

                newIn = col + 1 + wholeRows;
                indexBuffer[index++] = newIn;

                newIn = col + totalCols + wholeRows;
                indexBuffer[index++] = newIn;

                newIn = col + totalCols + wholeRows + 1;
                indexBuffer[index++] = newIn;

            }
        }


    }

    @Override
    public float[] getVertexBuffer() {
        return vertexBuffer;
    }

    @Override
    public int[] getIndexBuffer() {
        return indexBuffer;
    }

    @Override
    public int getPreferedRenderMode() {
        return GL_TRIANGLES;
    }

}
