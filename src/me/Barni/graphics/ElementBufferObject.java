package me.Barni.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class ElementBufferObject {
    private int id, eSize;
    //private int[] data;

    public void setData(int[] data)
    {
        //this.data = data;
        this.eSize = data.length;

        IntBuffer eBuffer = BufferUtils.createIntBuffer(data.length);
        eBuffer.put(data).flip();

        //Upload data to GPU
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, id);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, eBuffer, GL30.GL_DYNAMIC_DRAW);
    }

    public ElementBufferObject() {
        id = GL30.glGenBuffers();
    }

    public int getArraySize() {
        return eSize;
    }

    public int getId() {
        return id;
    }
}
