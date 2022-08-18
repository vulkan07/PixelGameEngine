package me.Barni.graphics;

import me.Barni.physics.Vec2D;
import org.joml.Vector4f;

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
        float[] va = new float[16 * positions.length / 4];
        for (int i = 0; i < positions.length / 4; i++) {
            int quadOffset = i * 16;
            int vertexOffset = i * 4;

            float x = positions[vertexOffset];
            float y = positions[vertexOffset + 1];
            float w = positions[vertexOffset + 2];
            float h = positions[vertexOffset + 3];


            va[quadOffset + 0] = x;    //TL x
            va[quadOffset + 1] = y;    //TL y

            va[quadOffset + 2] = texCoords[vertexOffset * 2 + 0];   //U
            va[quadOffset + 3] = texCoords[vertexOffset * 2 + 1];   //V

            va[quadOffset + 4] = w;  //BR x
            va[quadOffset + 5] = h;  //BR y

            va[quadOffset + 6] = texCoords[vertexOffset * 2 + 2];   //U
            va[quadOffset + 7] = texCoords[vertexOffset * 2 + 3];   //V

            va[quadOffset + 8] = w;  //TR x
            va[quadOffset + 9] = y;    //TR y

            va[quadOffset + 10] = texCoords[vertexOffset * 2 + 4];   //U
            va[quadOffset + 11] = texCoords[vertexOffset * 2 + 5];   //V

            va[quadOffset + 12] = x;    //BL x
            va[quadOffset + 13] = h;  //BL y

            va[quadOffset + 14] = texCoords[vertexOffset * 2 + 6];   //U
            va[quadOffset + 15] = texCoords[vertexOffset * 2 + 7];   //V

        }
        return va;
    }

    private static float[] positions = new float[9*4];
    private static float[] texCoords = new float[9*8];


    /**
     * Slices the batch's texture into 9 segments to avoid corner stretch (used for UI)
     */
    public static void nonaSlice(QuadBatch b, float x, float y, float w, float h) {

        int numCols = 3;
        int numRows = 3;
        int cellWidth = b.getTexture().getWidth()/numRows;
        int cellHeight = b.getTexture().getHeight()/numCols;

        float centerLenW = w - cellWidth*2;
        float centerLenH = h - cellHeight*2;

        for (int i = 0; i < 9; i++) {
            int offset = i * 4;
            float col = i / numCols;
            float row = i % numCols;


            //If on corners -> cellwidth
            //If on corners ==> row !=1
            //If center -> width-cellwidth*2

            float x_ = x;
            float y_ = y+32;


            switch ((int) row){
                case 0: x_ += 0; break;
                case 1: x_ += cellWidth; break;
                case 2: x_ += cellWidth + centerLenW; break;
            }
            switch ((int) col){
                case 0: y_ += 0; break;
                case 1: y_ += cellHeight; break;
                case 2: y_ += cellHeight + centerLenH; break;
            }

            //X
            positions[offset] = x_;   //x
            positions[offset + 1] = y_;             //y
            positions[offset + 2] = x_ + (row==1?centerLenW:cellWidth);     //w
            positions[offset + 3] = y_ + (col==1?centerLenH:cellHeight);      //h


            texCoords[offset * 2]     = row / numCols;                //u
            texCoords[offset * 2 + 1] = col / numRows;            //v

            texCoords[offset * 2 + 2] = (row + 1) / numCols;      //u
            texCoords[offset * 2 + 3] = (col + 1) / numRows;      //v

            texCoords[offset * 2 + 4] = (row + 1) / numCols;      //u
            texCoords[offset * 2 + 5] = col / numRows;            //v

            texCoords[offset * 2 + 6] = row / numCols;            //u
            texCoords[offset * 2 + 7] = (col + 1) / numRows;      //v
        }

        b.updateData(positions, texCoords);
    }

    public static Vector4f remapVec4f(Vector4f v, float low1, float high1, float low2, float high2) {
        Vector4f v2 = new Vector4f();
        v2.x = remap(v.x, low1, high1, low2, high2);
        v2.y = remap(v.y, low1, high1, low2, high2);
        v2.z = remap(v.z, low1, high1, low2, high2);
        v2.w = remap(v.w, low1, high1, low2, high2);
        return v2;
    }

    public static float remap(float value, float low1, float high1, float low2, float high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }

    public static float lerp(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }
}