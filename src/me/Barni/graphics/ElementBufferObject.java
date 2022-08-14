package me.Barni.graphics;

import me.Barni.Utils;
import me.Barni.exceptions.EngineException;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class ElementBufferObject {
    private int id, eSize;
    //private int[] data;

    public void setData(int[] data)
    {
        if (id == 0)
            throw new EngineException("Tried to reference deleted ElementBufferObject!");
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

    public void destroy() {
        Utils.GLClearErrors();
        GL30.glDeleteBuffers(id);
        id = 0;
        Utils.GLCheckError();
    }

    public int getArraySize() {
        return eSize;
    }

    public int getId() {
        return id;
    }
}
