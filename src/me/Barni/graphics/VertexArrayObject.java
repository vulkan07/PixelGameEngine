package me.Barni.graphics;

import me.Barni.ResourceManager;
import me.Barni.Utils;
import me.Barni.exceptions.EngineException;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

public class VertexArrayObject {
    private int id;
    private final ArrayList<Integer> attribSizes;
    private final ArrayList<String> attribNames;
    private final VertexBufferObject vbo;
    private final ElementBufferObject ebo;

    public VertexArrayObject() {
        id = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(id);

        vbo = new VertexBufferObject();
        ebo = new ElementBufferObject();
        attribSizes = new ArrayList<>();
        attribNames = new ArrayList<>();
        ResourceManager.registerVertexArrayObject(this);
    }

    public void destroy() {
        ebo.destroy();
        vbo.destroy();
        GL30.glDeleteBuffers(id);
        id = 0;
        ResourceManager.removeVertexArrayObject(this);
    }

    public void setVertexData(float[] vArray) {
        if (id == 0)
            throw new EngineException("Tried to reference deleted VertexArrayObject!");

        vbo.setData(vArray);
        //attribSizes.clear(); //Why?
        //System.out.println("VBO: Buffered Vertices");
    }

    public void setElementData(int[] eArray) {
        if (id == 0)
            throw new EngineException("Tried to reference deleted VertexArrayObject!");

        ebo.setData(eArray);
        //System.out.println("VBO: Buffered Elements");
    }

    /**
     * NOT IN BYTES
     **/
    public void addAttributePointer(int attribLength, String attribName) {
        if (id == 0)
            throw new EngineException("Tried to reference deleted VertexArrayObject!");

        attribSizes.add(attribLength);
        attribNames.add(attribName);
        processAttributePointers();
    }

    public String getAttributePointers() {
        if (id == 0)
            throw new EngineException("Tried to reference deleted VertexArrayObject!");

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < attribSizes.size(); i++) {
            b.append("[\"").append(attribNames.get(i)).append("\" ").append(attribSizes.get(i)).append("] ");
        }
        return b.toString();
    }

    public int getVertexLen() {
        if (id == 0)
            throw new EngineException("Tried to reference deleted VertexArrayObject!");

        return vbo.getArraySize();
    }

    private void processAttributePointers() {

        final int FLOAT = Float.BYTES;
        int offset = 0;

        //Calculate stride (full size in bytes)
        int stride = 0;
        for (int i : attribSizes) {
            stride += i;
        }
        stride *= FLOAT;

        for (int i = 0; i < attribSizes.size(); i++) {
            //Set pointer
            GL30.glVertexAttribPointer(i, attribSizes.get(i), GL30.GL_FLOAT, false, stride, offset);
            //Enable? pointer
            GL30.glEnableVertexAttribArray(i);
            //Add offset
            offset += attribSizes.get(i) * FLOAT;
        }
    }

    public void bind(boolean rebindAll) {
        if (id == 0)
            throw new EngineException("Tried to reference deleted VertexArrayObject!");

        //Bind vertex array (this)
        GL30.glBindVertexArray(id);

        //Enable attrib pointers
        for (int i = 0; i < attribSizes.size(); i++) {
            GL30.glEnableVertexAttribArray(i);
        }

        //Bind vbo & ebo (if rebindAll)
        if (rebindAll) {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo.getId());
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ebo.getId());
        }
    }

    public void unBind() {
        if (id == 0)
            throw new EngineException("Tried to reference deleted VertexArrayObject!");

        //UnBind vertex array
        GL30.glBindVertexArray(0);

        //Disable attrib pointers
        for (int i = 0; i < attribSizes.size(); i++) {
            GL30.glDisableVertexAttribArray(i);
        }
    }

    public int getId() {
        return id;
    }
}
