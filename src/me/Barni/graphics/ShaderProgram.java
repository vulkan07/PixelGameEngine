package me.Barni.graphics;

import me.Barni.Game;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

public class ShaderProgram {

    Shader fragment, vertex;

    public ShaderProgram(Game game) {
        this.game = game;
    }

    Game game;

    public int getId() {
        return id;
    }

    private int id;

    private String vertShader;
    private String fragShader;
    private String shaderName;

    private void loadShadersFromFile(String sFileName) {
        shaderName = sFileName;
        String lines = "";
        try {
            File file = new File( game.SHADER_DIR + sFileName + ".glsl");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            lines = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] programs = lines.split("#type fragment");
        fragShader = programs[1];
        vertShader = programs[0].split("#type vertex")[1];
    }

    public void create(String shaderName) {
        loadShadersFromFile(shaderName);

        vertex = new Shader(Shader.TYPE_VERTEX, vertShader, shaderName+" {V}");
        fragment = new Shader(Shader.TYPE_FRAGMENT, fragShader, shaderName+" {F}");

        vertex.compile();
        fragment.compile();

        id = GL30.glCreateProgram();
        GL30.glAttachShader(id, vertex.getId());
        GL30.glAttachShader(id, fragment.getId());
        game.getLogger().subInfo("[SHADER] ShaderProgram created: \"" + shaderName + "\"" );
    }

    public void link() {
        GL30.glLinkProgram(id);

        int success = GL30.glGetProgrami(id, GL30.GL_LINK_STATUS);
        if (success == GL30.GL_FALSE) {
            System.out.println("ShaderProgram linking failed!");
            System.out.println(GL30.glGetShaderInfoLog(id));
            throw new RuntimeException("Can't link shaderProgram!");
        }
    }

    public void bind() {
        GL30.glUseProgram(id);
    }

    public void unBind() {
        GL30.glUseProgram(0);
    }

    public void uploadMat4(String varName, Matrix4f mat4) {
        bind();
        int varLoc = GL30.glGetUniformLocation(id, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16); //4*4
        mat4.get(matBuffer); //Loads mat4 into matBuffer
        GL30.glUniformMatrix4fv(varLoc, false, matBuffer);
    }

    public void uploadVec2(String varName, Vector2f vec2) {
        bind();
        int varLoc = GL30.glGetUniformLocation(id, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(2); //4*4
        vec2.get(matBuffer); //Loads mat4 into matBuffer
        GL30.glUniform2fv(varLoc, matBuffer);
    }
    public void uploadVec4(String varName, Vector4f vec4) {
        bind();
        int varLoc = GL30.glGetUniformLocation(id, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(4); //4*4
        vec4.get(matBuffer); //Loads mat4 into matBuffer
        GL30.glUniform4fv(varLoc, matBuffer);
    }

    public void uploadFloat(String varName, float f) {
        bind();
        int varLoc = GL30.glGetUniformLocation(id, varName);
        GL30.glUniform1f(varLoc, f);
    }
    public void uploadBool(String varName, boolean b) {
        bind();
        int varLoc = GL30.glGetUniformLocation(id, varName);
        GL30.glUniform1i(varLoc, b ? 1 : 0);
    }

    public void selectTextureSlot(String varName, int slot) {
        bind();
        int varLoc = GL30.glGetUniformLocation(id, varName);
        GL30.glUniform1i(varLoc, slot);
        GL30.glActiveTexture(GL30.GL_TEXTURE0+slot);
    }

}
