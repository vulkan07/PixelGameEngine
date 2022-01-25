package me.Barni;

import me.Barni.entity.Entity;
import me.Barni.entity.childs.Player;
import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.VertexArrayObject;
import me.Barni.physics.Physics;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import me.Barni.texture.TextureAtlas;

import org.joml.Vector4f;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Map {

    Game game;
    public Physics physics;

    public int width, height, tileSize;

    private byte[] tiles;
    private byte[] backTiles;

    public Entity[] entities = new Entity[16];
    public Decorative[] decoratives = new Decorative[32];

    private int decCount = 0;

    private String title, fileName;
    public Vec2D playerStartPos = new Vec2D(), playerStartVel = new Vec2D();


    public Camera cam;

    ShaderProgram frontShader, backShader;
    VertexArrayObject vao;
    public TextureAtlas atlas, normAtlas;
    public static final int[] ELEMENT_ARRAY = {2, 1, 0, 0, 1, 3};


    //----------------------------
    //-- Getters & Setters -------
    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setBackGroundColor(Color c) {
        game.bgColor = c.getRGB();
    }

    public byte getBackTile(int i) {
        return backTiles[i];
    }

    public void setBackTile(int i, int id) {
        backTiles[i] = (byte) id;
    }

    public byte getTile(int i) {
        return tiles[i];
    }

    public void setTile(int i, int id) {
        tiles[i] = (byte) id;
    }

    public void setTile(int x, int y, int id) {
        tiles[y * width + x] = (byte) id;
    }

    public byte getTile(int x, int y) {
        return tiles[y * width + x];
    }

    public int getTilesLength() {
        return tiles.length;
    }

    public void setTileArray(byte[] newTiles) {
        tiles = newTiles;
    }

    public int getDecCount() {
        return decCount;
    }
    //-- Getters & Setters -------
    //----------------------------

    public Map(Game g, int w, int h, int tSize, String fName) {
        fileName = fName;
        width = h;
        height = w;
        tileSize = tSize;
        tiles = new byte[width * height];
        backTiles = new byte[width * height];
        game = g;
        atlas = new TextureAtlas(game, Material.materialPath.length, tileSize);
        normAtlas = new TextureAtlas(game, Material.materialPath.length, tileSize);

        game.getLogger().info("[MAP] Initialized new map, size: " + w + ", " + h);

        physics = new Physics(game, this);

        cam = new Camera(g.getWIDTH(), g.getHEIGHT());
    }

    public void createShaderPrograms() {
        frontShader = new ShaderProgram(game);
        frontShader.create("mapTile");
        frontShader.link();

        backShader = new ShaderProgram(game);
        backShader.create("backgroundTile");
        backShader.link();
        backShader.uploadVec4("uBackColor", new Vector4f(.2f, .2f, .2f, 0));


        vao = new VertexArrayObject();
        float[] vArray = new float[8];
        vao.setVertexData(vArray);
        vao.setElementData(ELEMENT_ARRAY);
        vao.addAttributePointer(2); //Position (x,y)
        vao.addAttributePointer(2); //TX coords (u,v)
    }

    public void dumpCurrentMapIntoFile(String path) {
        game.getLogger().info("[MAP] Writing out current map");


        File file = new File(game.GAME_DIR + path + ".map");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject();
        JSONObject mapObj = new JSONObject();
        mapObj.put("version", MapLoader.VALID_MAP_FILE_VERSION);
        mapObj.put("sizeX", width);
        mapObj.put("sizeY", height);
        mapObj.put("spawnPos", playerStartPos.xi() + "," + playerStartPos.yi());
        mapObj.put("spawnVel", playerStartVel.xi() + "," + playerStartVel.yi());

        String data;
        JSONArray grid = new JSONArray();
        for (int y = 0; y < height; y++) {
            data = "";
            for (int x = 0; x < width; x++) {
                data += tiles[y * width + x] + ",";
            }
            grid.put(y, data);
        }
        mapObj.put("grid", grid);


        JSONArray grid2 = new JSONArray();
        for (int y = 0; y < height; y++) {
            data = "";
            for (int x = 0; x < width; x++) {
                data += backTiles[y * width + x] + ",";
            }
            grid2.put(y, data);
        }
        mapObj.put("backGrid", grid2);


        JSONObject objList = new JSONObject();
        objList.put("Decoratives", decsToJSON());
        objList.put("Entities", entsToJSON());

        mapObj.put("ObjectList", objList);

        jobj.put("bmap", mapObj);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jobj.toString(4));
            writer.close();
            game.getLogger().info("[MAP] Dumped map file into " + game.GAME_DIR + path + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONArray decsToJSON() {
        JSONArray array = new JSONArray();
        JSONObject decObj;

        int place = -1;
        for (int i = 0; i < decoratives.length; i++) {
            Decorative d = decoratives[i];

            if (d == null) continue;
            decObj = new JSONObject();

            place++;
            decObj.put("x", d.x);
            decObj.put("y", d.y);
            decObj.put("w", d.w);
            decObj.put("h", d.h);
            decObj.put("z-layer", d.z);
            decObj.put("parallax", d.parallax);
            decObj.put("texture", d.texture.getPath());
            array.put(place, decObj);
        }

        return array;
    }

    private JSONArray entsToJSON() {
        JSONArray array = new JSONArray();
        //JSONObject entObj;

        int place = -1;
        for (int i = 0; i < entities.length; i++) {
            Entity e = entities[i];

            if (e == null) continue;

            place++;
            array.put(place, e.serialize());
        }

        return array;
    }

    public void loadTextures() {

        for (int i = 1; i < Material.materialPath.length; i++) {
            Texture t = new Texture();
            t.loadTexture(game, Material.materialPath[i], 32, 32, true);
            t.uploadImageToGPU(true);
            atlas.addTexture(t);

            //Normal texture
            Texture normalT = new Texture();
            normalT.loadTexture(game, Material.materialPath[i] + "_nor", 32, 32, true);
            normalT.uploadImageToGPU(true);
            normAtlas.addTexture(normalT);
        }
    }

    public void render(Camera cam) {
        vao.bind(false);

        renderTiles(false, cam);
        renderTiles(true, cam);

        vao.unBind();
    }

    private void renderTiles(boolean front, Camera camera) {
        ShaderProgram currentShader;
        if (front)
            currentShader = frontShader;
        else
            currentShader = backShader;

        currentShader.bind();

        currentShader.uploadMat4("uProjMat", camera.getProjMat());
        currentShader.uploadMat4("uViewMat", camera.getViewMat());
        currentShader.uploadFloat("uAlpha", 0);

        for (int i = 0; i < width * height; i++) {

            if (front) {
                if (tiles[i] == 0)
                    continue;
            } else if (backTiles[i] == 0)
                continue;

            float[] vArray = generateVertexArray(i % width * 32,
                    i / width * 32, 32, 32);
            vao.setVertexData(vArray);

            if (front) {
                if (normAtlas.getImage(tiles[i] - 1) != null) {
                    currentShader.selectTextureSlot("uNorSampler", 1);
                    normAtlas.getTexture(tiles[i] - 1).bind();
                }
                currentShader.selectTextureSlot("uTexSampler", 0);
                atlas.getTexture(tiles[i] - 1).bind();
            } else {
                if (normAtlas.getImage(backTiles[i] - 1) != null) {
                    currentShader.selectTextureSlot("uTexSampler", 0);
                    atlas.getTexture(backTiles[i] - 1).bind();
                }


            }
            GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
            currentShader.unBind();
        }
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

    public void renderEntities(BufferedImage img) {
        for (Entity e : entities) {
            if (e != null)
                e.render(img, cam);
        }

    }

    public void renderDecoratives(BufferedImage img, int zPlane) {
        for (Decorative d : decoratives) {
            if (d != null)
                if (d.z == zPlane)
                    d.render(img, cam);
        }
    }


    public void tick() {

        atlas.update();

        cam.update();

        for (Entity e : entities) {
            if (e != null)
                e.tick();
        }
        physics.update();

        for (Decorative d : decoratives) {
            if (d != null)
                d.tick();
        }
    }


    public int addDecorative(Decorative dec) {
        if (decCount >= decoratives.length) {
            game.getLogger().err("Decoratives array is full!");
            return -1;
        }
        decoratives[decCount] = dec;
        decCount++;
        return decCount - 1;
    }

    public void addEntity(Entity e) {
        if (e instanceof Player)
            initPlayer((Player) e);
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == null) {
                if (e.name == null || e.name.equals(""))
                    game.getLogger().warn("[MAP] Entity added without name: " + e.getClass());
                entities[i] = e;
                e.setID(i);
                physics.init();
                game.getLogger().subInfo("[MAP] Added entity: " + e.getClass());
                return;
            }
        }
        game.getLogger().err("[MAP] Entity array is full!!");
    }

    public void initPlayer(Player p) {
        p.setLevel(1);
        p.spawnLocation = playerStartPos.copy();
        p.position = playerStartPos.copy();
        p.velocity = playerStartVel.copy();
    }

    public Entity getEntity(String name) {
        for (Entity e : entities)
            if (e == null) continue;
            else if (e.name.equals(name)) return e;
        return null;
    }

    public void removeEntity(String name) {
        physics.init();
        for (int i = 0; i < entities.length; i++)
            if (entities[i] != null)
                if (entities[i].name.equals(name)) entities[i] = null;
    }

    /**
     * Only for editor! Don't use it!
     **/
    public void removeDecorative(int i) {
        decoratives[i] = null;
        decCount--;

        for (int j = i; j < decoratives.length - 1; j++) {
            decoratives[j] = decoratives[j + 1];
        }
    }

}
