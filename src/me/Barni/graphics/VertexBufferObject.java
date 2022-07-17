package me.Barni.graphics;

import me.Barni.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class VertexBufferObject {
    private int id, vSize;
    FloatBuffer vBuffer;

    public void setData(float[] data) {

        //Only recreate buffer if size changes
        if (vSize != data.length)
            vBuffer = BufferUtils.createFloatBuffer(data.length);

        this.vSize = data.length;

        //Put data in float buffer
        vBuffer.put(data).flip();

        //Upload data to GPU
        Utils.GLClearErrors();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, id);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vBuffer, GL30.GL_DYNAMIC_DRAW);
        Utils.GLCheckError();
    }

    public VertexBufferObject() {
        //Generate & bind
        Utils.GLClearErrors();
        id = GL30.glGenBuffers();
        Utils.GLCheckError();
    }

    public int getArraySize() {
        return vSize;
    }

    public int getId() {
        return id;
    }
}
