package me.Barni;

import me.Barni.entity.Entity;
import me.Barni.entity.childs.Player;
import me.Barni.graphics.RenderableText;
import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.TextRenderer;
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

import static me.Barni.Intro.ELEMENT_ARRAY;
import static me.Barni.graphics.GraphicsUtils.SCREEN_VERTEX_ARRAY;
import static me.Barni.graphics.GraphicsUtils.generateVertexArray;

public class Map {

    Game game;
    public Physics physics;

    public int width, height, tileSize;

    private Tile[] tiles;
    private Tile[] backTiles;

    public Entity[] entities = new Entity[16];
    public Decorative[] decoratives = new Decorative[32];

    private int decCount = 0;
    private Player player;

    private String title, fileName;
    public Vec2D playerStartPos = new Vec2D(), playerStartVel = new Vec2D();

    public float deathGray;
    public Camera cam;

    private ShaderProgram frontShader, backShader, entShader, decShader, backImageShader;
    private VertexArrayObject vao;

    private Texture backgroundTexture;
    private String backgroundTexturePath;

    public TextureAtlas atlas;

    //----------------------------
    //-- Getters & Setters -------
    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public int getBackTile(int i) {
        return backTiles[i].id;
    }

    public void setBackTile(int i, Tile t) {
        backTiles[i] = t;
    }
    public void setBackTileID(int i, int id) {
        backTiles[i].id = id;
    }
    public void setTileID(int i, int id) {
        tiles[i].id = id;
    }
    public int getTile(int i) {
        return tiles[i].id;
    }
    public int getTileType(int i) {
        return tiles[i].type;
    }

    public void setTile(int i, Tile t) {
        tiles[i] = t;
    }
    public void setTile(int x, int y, int id) {
        tiles[y * width + x].id = id;
    }

    public int getTile(int x, int y) {
        return tiles[y * width + x].id;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTilesLength() {
        return tiles.length;
    }

    public void setTileArray(Tile[] newTiles) {
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
        tiles = new Tile[width * height];
        backTiles = new Tile[width * height];
        game = g;
        atlas = new TextureAtlas(game, Material.getMatCount(), tileSize);

        game.getLogger().info("[MAP] Initialized new map [" + w + "x" + h + "]");

        physics = new Physics(game, this);

        cam = new Camera(g.getWIDTH(), g.getHEIGHT());
        //cam.setZoom(1.3f, false);
    }

    public void createShaderPrograms() {
        frontShader = new ShaderProgram(game);
        frontShader.create("mapTile");
        frontShader.link();

        backShader = new ShaderProgram(game);
        backShader.create("backgroundTile");
        backShader.link();
        backShader.uploadVec4("uBackColor", new Vector4f(.2f, .2f, .2f, 0));

        entShader = new ShaderProgram(game);
        entShader.create("entityDefault");
        entShader.link();

        decShader = new ShaderProgram(game);
        decShader.create("decorativeDefault");
        decShader.link();

        backImageShader = new ShaderProgram(game);
        backImageShader.create("backImage");
        backImageShader.link();


        vao = new VertexArrayObject();
        float[] vArray = new float[8];
        vao.setVertexData(vArray);
        vao.setElementData(ELEMENT_ARRAY);
        vao.addAttributePointer(2); //Position (x,y)
        vao.addAttributePointer(2); //TX coords (u,v)
    }

    public void dumpCurrentMapIntoFile(String absolutePath) {
        game.getLogger().info("[MAP] Saving...");


        File file = new File(absolutePath);
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
                int pos = y* width + x;
                data += tiles[pos].id + String.valueOf(tiles[pos].type==0?"":tiles[pos].type) + ",";
            }
            grid.put(y, data);
        }
        mapObj.put("grid", grid);


        JSONArray grid2 = new JSONArray();
        for (int y = 0; y < height; y++) {
            data = "";
            for (int x = 0; x < width; x++) {
                int pos = y* width + x;
                data += backTiles[pos].id + String.valueOf(backTiles[pos].type==0?"":backTiles[pos].type) + ",";
            }
            grid2.put(y, data);
        }
        mapObj.put("backGrid", grid2);


        JSONObject objList = new JSONObject();
        objList.put("Decoratives", decsToJSON());
        objList.put("Entities", entsToJSON());

        mapObj.put("ObjectList", objList);
        mapObj.put("backImage", backgroundTexturePath);

        jobj.put("bmap", mapObj);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jobj.toString(4));
            writer.close();
            game.getLogger().info("[MAP] Saved [" + absolutePath + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONArray decsToJSON() {
        JSONArray array = new JSONArray();
        JSONObject decObj;

        int place = -1;
        for (Decorative d : decoratives) {
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
        for (Entity e : entities) {
            if (e == null) continue;

            place++;
            array.put(place, e.serialize());
        }

        return array;
    }

    public void setBackgroundTexture(String path) {
        backgroundTexturePath = path;
        if (backgroundTexture == null)
            backgroundTexture = new Texture();
        backgroundTexture.loadTexture(game, backgroundTexturePath, 1920, 1080, true);
        backgroundTexture.uploadImageToGPU(0);
    }

    public void loadTextures() {

        setBackgroundTexture(backgroundTexturePath);

        for (int i = 1; i < Material.getMatCount(); i++) {
            Texture[] txts = new Texture[Material.getMaxTypes()];
            for (int j = 0; j < Material.getMaxTypes() - 1; j++) {
                if (Material.getPath(i,j) == null)
                    continue;
                Texture t = new Texture();
                t.loadTexture(game, Material.getPath(i,j), 32, 32, true);
                t.uploadImageToGPU(0);
                txts[j] = t;
            }
            atlas.addTexture(txts);
        }
    }

    private void destroyTextures() {
        atlas.destroy();
        game.getLogger().info("[MAP] Deleted textures from GPU");
    }

    public void destroy() {
        destroyTextures();
        vao.unBind();
        GL30.glUseProgram(0); //Unbind shader
    }

    public void render(Camera cam) {

        cam.update();
        vao.bind(false);

        renderBackground(); //Background

        //I should make the render layer "system" better
        //Loop for each layer and render objects only that are on the layer
        for (int layer = 0; layer < RENDER_LAYERS; layer++) {
            if (layer == TILE_RENDER_LAYER) {
                renderTiles(false, cam);
                renderTiles(true, cam);
            }
            if (layer == ENT_RENDER_LAYER) {
                renderEntities();
            }

            renderDecoratives(layer);
        }

        vao.unBind();
        test.setX(player.position.xi()+32);
        test.setY(player.position.yi()-32);
        test.setText("x:"+player.position.x);
        test.render(cam);

    }
    public RenderableText test= new RenderableText("DONÁLD", 200, 500, 15, Color.RED);

    public final int ENT_RENDER_LAYER = 8;
    public final int TILE_RENDER_LAYER = 4;
    public final int RENDER_LAYERS = 12;

    private void renderBackground() {
        if (backgroundTexture == null || !backgroundTexture.isValid())
            return;

        backImageShader.bind();
        backImageShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());
        backImageShader.uploadFloat("uGray", deathGray);
        backImageShader.selectTextureSlot("uTexSampler", 1);
        backgroundTexture.bind();

        vao.setVertexData(SCREEN_VERTEX_ARRAY);
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);

        backImageShader.unBind();
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
        currentShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());
        currentShader.uploadFloat("uGray", deathGray);

        for (int i = 0; i < width * height; i++) {

            if (front)
                if (tiles[i].id == 0)
                    continue;

            if (!front)
                if (backTiles[i].id == 0)
                    continue;

            float[] vArray = generateVertexArray(i % width * 32,
                    i / width * 32, 32, 32);
            vao.setVertexData(vArray);

            if (front) {
                /*Nor
                Texture t = normAtlas.getTexture(tiles[i] - 1);
                if (t != null && t.isValid()) {
                    currentShader.selectTextureSlot("uNorSampler", 1);
                    t.bind();
                }*/
                //Dif
                Texture t = atlas.getTexture(tiles[i].id-1, tiles[i].type);
                if (t != null && t.isValid()) {
                    currentShader.selectTextureSlot("uTexSampler", 1);
                    t.bind();
                }
            } else {
                //Dif (background)
                Texture t = atlas.getTexture(backTiles[i].id-1, backTiles[i].type);
                if (t != null && t.isValid()) {
                    currentShader.selectTextureSlot("uTexSampler", 1);
                    t.bind();
                }


            }
            GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        }
        currentShader.unBind();
    }


    public void renderEntities() {
        entShader.bind();

        entShader.uploadMat4("uProjMat", cam.getProjMat());
        entShader.uploadMat4("uViewMat", cam.getViewMat());
        entShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());

        for (Entity e : entities) {
            if (e != null)
                e.render(vao, entShader);
        }
        entShader.unBind();
    }

    public void renderDecoratives(int layer) {
        decShader.bind();

        decShader.uploadMat4("uProjMat", cam.getProjMat());
        decShader.uploadMat4("uViewMat", cam.getViewMat());
        decShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());

        for (Decorative d : decoratives) {
            if (d != null && d.z == layer) {
                decShader.uploadFloat("uParallax", d.parallax);
                d.render(vao, decShader);
            }
        }
        decShader.unBind();
    }

    public void tick(float dt) {

        atlas.update();

        cam.update();

        for (Entity e : entities) {
            if (e != null)
                e.tick();
        }
        physics.update(dt);

        deathGray = Vec2D.lerp(deathGray, player.alive ? 0 : 0.6f, player.alive ? .06f : .04f);


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
        player = p;
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
