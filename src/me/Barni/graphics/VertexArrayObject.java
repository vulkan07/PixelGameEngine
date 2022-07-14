package me.Barni.graphics;

import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

public class VertexArrayObject {
    private final int id;
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
    }

    public void setVertexData(float[] vArray) {
        vbo.setData(vArray);
        //attribSizes.clear(); //Why?
        //System.out.println("VBO: Buffered Vertices");
    }

    public void setElementData(int[] eArray) {
        ebo.setData(eArray);
        //System.out.println("VBO: Buffered Elements");
    }

    /**
     * NOT IN BYTES
     **/
    public void addAttributePointer(int attribLength, String attribName) {
        attribSizes.add(attribLength);
        attribNames.add(attribName);
        processAttributePointers();
    }

    public String getAttributePointers() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < attribSizes.size(); i++) {
            b.append("[\"").append(attribNames.get(i)).append("\" ").append(attribSizes.get(i)).append("] ");
        }
        return b.toString();
    }

    public int getVertexLen() {
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
