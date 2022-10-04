package solids;

public class GridTriangles implements ISolid {

    public GridTriangles(int m, int n) {
        vertexBuffer = new float[m*n*2];
        indexBuffer = new int[6*(m-1)*(n-1)];

        //Verts
        int index = 0;
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                System.out.println(j / (float) (n-1));
                System.out.println(i / (float) (m-1));
                vertexBuffer[index] = j / (float) (n-1);
                vertexBuffer[index+1] = i / (float) (n-1);
                index += 2;
            }
        }

        //Indexes
        index = 0;
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                System.out.println(j+i*m);
                System.out.println(j+n+i*m);
                System.out.println(j+1+i*m);
                index += 2;
                System.out.println(j+1+i*m);
                System.out.println(j+n+i*m);
                System.out.println(j+n+1+i*m);
            }
        }



    }

    private float[] vertexBuffer;
    private int[] indexBuffer;

    @Override
    public float[] getVertexBuffer() {
        return vertexBuffer;
    }

    @Override
    public int[] getIndexBuffer() {
        return indexBuffer;
    }
}
