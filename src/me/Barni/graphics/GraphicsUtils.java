package me.Barni.graphics;

public class GraphicsUtils {


    public static final int[] QUAD_ELEMENT_ARRAY = {2, 1, 0, 0, 1, 3};
    public static final float[] SCREEN_VERTEX_ARRAY = {-1, -1, 0, 1, 1, 1, 1, 0, 1, -1, 1, 1, -1, 1, 0, 0};
    public static final int PER_VERTEX_LENGTH = 4;
    public static final int VERTEX_COUNT = 4;
    public static final int FULL_VERTEX_LENGTH = PER_VERTEX_LENGTH * VERTEX_COUNT;

    public static float[] generateVertexArray(float x, float y, float w, float h) {
        float[] va = new float[16];
        va[0] = x;    //TL x
        va[1] = y;    //TL y

        va[2] = 0f;   //U
        va[3] = 0f;   //V

        va[4] = x + w;  //BR x
        va[5] = y + h;  //BR y

        va[6] = 1f;   //U
        va[7] = 1f;   //V

        va[8] = x + w;  //TR x
        va[9] = y;    //TR y

        va[10] = 1f;   //U
        va[11] = 0f;   //V

        va[12] = x;    //BL x
        va[13] = y + h;  //BL y

        va[14] = 0f;   //U
        va[15] = 1f;   //V

        return va;
    }
}
