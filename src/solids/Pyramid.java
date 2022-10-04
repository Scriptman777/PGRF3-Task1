package solids;

public class Pyramid implements ISolid{

    private float[] vBuff = {
                            -0.8f,-0.8f,-0.8f,
                            -0.8f,0.8f,-0.8f,
                            0.8f,-0.8f,-0.8f,
                            0,0,0
    };

    private int[] iBuff = {
            0,1,2,
            1,2,3,
            2,3,4,
            4,0,1
    };


    @Override
    public float[] getVertexBuffer() {
        return vBuff;
    }

    @Override
    public int[] getIndexBuffer() {
        return iBuff;
    }
}