package solids;

public class DefaultTriangle implements ISolid{

    private float[] vBuff = {
            -1,-1,
            1,0,
            0,1
    };
    private int[] iBuff = {0,1,2};

    public DefaultTriangle() {
    }

    @Override
    public float[] getVertexBuffer() {
        return vBuff;
    }

    @Override
    public int[] getIndexBuffer() {
        return iBuff;
    }
}
