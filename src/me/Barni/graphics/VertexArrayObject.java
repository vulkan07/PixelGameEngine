package me.Barni.graphics;

import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

public class VertexArrayObject {
    private int id;
    private ArrayList<Integer> attribSizes;
    private VertexBufferObject vbo;
    private ElementBufferObject ebo;

    public VertexArrayObject() {
        id = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(id);

        vbo = new VertexBufferObject();
        ebo = new ElementBufferObject();
        attribSizes = new ArrayList<>();
        System.out.println("New VBO generated");
    }

    public void setVertexData(float[] vArray) {
        vbo.setData(vArray);
        attribSizes.clear();
        //System.out.println("VBO: Buffered Vertices");
    }

    public void setElementData(int[] eArray) {
        ebo.setData(eArray);
        //System.out.println("VBO: Buffered Elements");
    }

    /**
     * NOT IN BYTES
     **/
    public void addAttributePointer(int attribLength) {
        attribSizes.add(attribLength);
        processAttributePointers();
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
            System.out.println("Attribute pointer set at " + i + " (" + attribSizes.get(i) + " bytes, shift: " + offset + ")");
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
