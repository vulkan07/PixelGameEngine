package me.Barni.graphics;

import org.lwjgl.opengl.GL30;

public class Shader {


    private int id;
    private int type;
    private String srcCode, name;
    public static final int TYPE_VERTEX = 0;
    public static final int TYPE_FRAGMENT = 1;

    public Shader(int type, String srcCode, String name) {
        if (type < 0 || type > TYPE_FRAGMENT)
            throw new IllegalArgumentException("Illegal shader type ID: " + type);
        this.type = type;
        this.srcCode = srcCode;
        this.id = 0;
        this.name = name;
    }

    public void compile() {
        //System.out.println("Compiling type " + type + " shader...");
        if (type == TYPE_VERTEX)
            id = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
        if (type == TYPE_FRAGMENT)
            id = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);

        GL30.glShaderSource(id, srcCode);
        GL30.glCompileShader(id);

        int success = GL30.glGetShaderi(id, GL30.GL_COMPILE_STATUS);

        if (success == GL30.GL_FALSE)
        {
            System.out.println(srcCode);
            System.out.printf("[SHADER] Failed Compiling: %s%n", name);
            System.out.println(GL30.glGetShaderInfoLog(id));
            throw new RuntimeException("Can't compile shaders!");
        }
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }
}
