package me.Barni;

import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.VertexArrayObject;
import me.Barni.texture.Texture;

import java.util.ArrayList;

public abstract class ResourceManager {

    private static Game game;
    private static ArrayList<Texture> textures = new ArrayList<>();
    private static ArrayList<ShaderProgram> shaders = new ArrayList<>();
    private static ArrayList<VertexArrayObject> vertexArrays = new ArrayList<>();


    public static void init(Game g) {
        game = g;
    }

    public static void reload() {

    }

    //Texture
    public static void registerTexture(Texture t) {
        textures.add(t);
    }
    public static void removeTexture(Texture t) {
        textures.remove(t);
    }

    //Shader
    public static void registerShader(ShaderProgram t) {
        shaders.add(t);
    }
    public static void removeShader(ShaderProgram t) {
        shaders.remove(t);
    }

    //OpenGL Buffers
    public static void registerVertexArrayObject(VertexArrayObject vao) {
        vertexArrays.add(vao);
    }
    public static void removeVertexArrayObject(VertexArrayObject vao) {
        vertexArrays.remove(vao);
    }

    public static void cleanUp() {
        info("Deleting Resources...");

        String out = "Deleted ";

        //Delete remaining Textures
        int s = textures.size();
        while (!textures.isEmpty())
            textures.get(0).destroy();
        if (s > 0)
            out += s + " Textures, ";

        //Delete remaining Shaders
        s = shaders.size();
        while (!shaders.isEmpty())
            shaders.get(0).destroy();
        if (s > 0)
            out += s + " ShaderPrograms, ";

        //Delete remaining VertexArrays
        s = vertexArrays.size();
        while (!vertexArrays.isEmpty())
            vertexArrays.get(0).destroy();
        if (s > 0)
            out += s + " VertexArrayObjects";
        info(out);
    }

    private static void info(String msg) {
        game.getLogger().info("[RESOURCE MANAGER] " + msg);
    }

    private static void warn(String msg) {
        game.getLogger().warn("[RESOURCE MANAGER] " + msg);
    }

    private static void err(String msg) {
        game.getLogger().err("[RESOURCE MANAGER] " + msg);
    }

}
