package me.Barni.graphics;

import java.util.Arrays;

public class GraphicsUtils {


    public static final int[] QUAD_ELEMENT_ARRAY = {0, 1, 2, 0, 3, 1};
    public static final float[] SCREEN_VERTEX_ARRAY = {-1, -1, 0, 1, 1, 1, 1, 0, 1, -1, 1, 1, -1, 1, 0, 0};
    public static final int PER_VERTEX_LENGTH = 4;
    public static final int VERTEX_COUNT = 4;
    public static final int FULL_VERTEX_LENGTH = PER_VERTEX_LENGTH * VERTEX_COUNT;

    public static int[] getQuadElementArray(int num) {
        int[] ea = new int[QUAD_ELEMENT_ARRAY.length * num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < QUAD_ELEMENT_ARRAY.length; j++) {
                ea[j + (i * QUAD_ELEMENT_ARRAY.length)] = QUAD_ELEMENT_ARRAY[j] + (i * 4);
            }
        }

        return ea;
    }

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


    //DO NOT TOUCH THIS DEVILISH SH1T
    public static float[] generateBatchVertexArray(float[] positions, float[] texCoords) {
        //positions = [xywh ; ...]
        //texCoords = [uv, uv, uv, uv ; ...]
        float[] va = new float[16 * positions.length/4];
        for (int i = 0; i < positions.length / 4; i++) {
            int quadOffset = i*16;
            int vertexOffset = i*4;

            float x = positions[vertexOffset];
            float y = positions[vertexOffset + 1];
            float w = positions[vertexOffset + 2];
            float h = positions[vertexOffset + 3];


            va[quadOffset + 0] = x;    //TL x
            va[quadOffset + 1] = y;    //TL y

            va[quadOffset + 2] = texCoords[vertexOffset*2+ 0];   //U
            va[quadOffset + 3] = texCoords[vertexOffset*2+ 1];   //V

            va[quadOffset + 4] = x + w;  //BR x
            va[quadOffset + 5] = y + h;  //BR y

            va[quadOffset + 6] = texCoords[vertexOffset*2+ 2];   //U
            va[quadOffset + 7] = texCoords[vertexOffset*2+ 3];   //V

            va[quadOffset + 8] = x + w;  //TR x
            va[quadOffset + 9] = y;    //TR y

            va[quadOffset + 10] = texCoords[vertexOffset*2 + 4];   //U
            va[quadOffset + 11] = texCoords[vertexOffset*2 + 5];   //V

            va[quadOffset + 12] = x;    //BL x
            va[quadOffset + 13] = y + h;  //BL y

            va[quadOffset + 14] = texCoords[vertexOffset*2 + 6];   //U
            va[quadOffset + 15] = texCoords[vertexOffset*2 + 7];   //V

        }
        return va;
    }
}
