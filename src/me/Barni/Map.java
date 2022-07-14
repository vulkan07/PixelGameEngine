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

import java.io.*;

import static me.Barni.Intro.ELEMENT_ARRAY;
import static me.Barni.graphics.GraphicsUtils.SCREEN_VERTEX_ARRAY;
import static me.Barni.graphics.GraphicsUtils.generateVertexArray;

public class Map {

    private final Game game;
    private Physics physics;

    private int width, height, tileSize;

    private Tile[] tiles;
    private Tile[] backTiles;

    public Entity[] entities = new Entity[16];
    public Decorative[] decoratives = new Decorative[32];
    private int decCount, entCount;

    private String title, fileName;

    private Camera cam;
    private Player player;
    public Vec2D playerStartPos = new Vec2D(), playerStartVel = new Vec2D();

    public float deathGray;

    private ShaderProgram frontShader, backShader, entShader, decShader, backImageShader;
    private VertexArrayObject vao;

    private Texture backgroundTexture;
    private String backgroundTexturePath;

    public TextureAtlas atlas;

    //-----------------------------------------
    //----------- Getters & Setters -----------
    //-----------------------------------------

    //Get Title & FileName
    public String getTitle() {
        return title;
    }
    public String getFileName() {
        return fileName;
    }

    //Get width, height, tileSize
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getTileSize() {
        return tileSize;
    }

    //Get tiles
    public Tile getBackTile(int i) {
        return backTiles[i];
    }
    public Tile getTile(int i) {
        return tiles[i];
    }

    //Set tiles as Tiles
    public void setTile(int i, Tile t) {
        tiles[i] = t;
    }
    public void setBackTile(int i, Tile t) {
        backTiles[i] = t;
    }

    //Set tile data (with variables instead of new Tile object)
    public void setTileData(int i, int id, int type) {
        tiles[i].id = id;
        tiles[i].type = type;
    }
    public void setBackTileData(int i, int id, int type) {
        backTiles[i].id = id;
        backTiles[i].type = type;
    }

    //Get Player & Camera
    public Player getPlayer() {
        return player;
    }
    public Camera getCamera() {
        return cam;
    }

    //Get tile length
    public int getTilesLength() {
        return tiles.length;
    }

    //Set tiles with arrays
    public void setTileArray(Tile[] newTiles) {
        tiles = newTiles;
    }
    public void setBackTiles(Tile[] newTiles) {
        backTiles = newTiles;
    }

    public int getDecorativeCount() {
        return decCount;
    }
    public int getEntityCount() {
        return entCount;
    }
    //-----------------------------------------
    //-----------                   -----------
    //-----------------------------------------

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

    public void initPlayer(Player p) {
        p.setLevel(1);
        p.spawnLocation = playerStartPos.copy();
        p.position = playerStartPos.copy();
        p.velocity = playerStartVel.copy();
        player = p;
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
        vao.addAttributePointer(2, "pos"); //Position (x,y)
        vao.addAttributePointer(2, "tex"); //TX coords (u,v)
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
        backgroundTexture.loadTexture(backgroundTexturePath, 1920, 1080);
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
                t.loadTexture(Material.getPath(i,j), 32, 32);
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
    public final int ENT_RENDER_LAYER = 8;
    public final int TILE_RENDER_LAYER = 4;
    public final int RENDER_LAYERS = 12;

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

    }

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

            Texture t;
            if (front) {
                t = atlas.getTexture(tiles[i].id - 1, tiles[i].type); //Foreground texture
            } else {
                t = atlas.getTexture(backTiles[i].id - 1, backTiles[i].type); //Background texture
            }
            if (t != null && t.isValid()) {
                currentShader.selectTextureSlot("uTexSampler", 1);
                t.bind();
            }
            Utils.GLClearError();
            GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
            Utils.GLCheckError();
        }
        currentShader.unBind();
    }

    public void renderEntities() {
        entShader.bind();

        entShader.uploadMat4("uProjMat", getCamera().getProjMat());
        entShader.uploadMat4("uViewMat", getCamera().getViewMat());
        entShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());

        for (Entity e : entities) {
            if (e != null)
                e.render(vao, entShader);
        }
        entShader.unBind();
    }

    public void renderDecoratives(int layer) {
        decShader.bind();

        decShader.uploadMat4("uProjMat", getCamera().getProjMat());
        decShader.uploadMat4("uViewMat", getCamera().getViewMat());
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

        getCamera().update();

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


    //--------------------------------
    //Entity & decorative manipulation
    //--------------------------------

    //ADDERS
    /**Returns the ID of the new decorative.**/
    public int addDecorative(Decorative dec) {
        if (decCount >= decoratives.length) {
            game.getLogger().err("Decoratives array is full! Resizing...");

            //Create new array with size+2, copy decoratives, and set it as the new array
            Decorative[] newDecs = new Decorative[decoratives.length+2];
            System.arraycopy(decoratives, 0, newDecs, 0, decoratives.length);
            decoratives = newDecs;
        }
        decoratives[decCount] = dec;
        decCount++;
        return decCount - 1;
    }
    /**Returns the ID of the new entity.**/
    public int addEntity(Entity e) {
        if (e instanceof Player)
            initPlayer((Player) e);

        if (entCount >= entities.length) {
            game.getLogger().err("Entity array is full! Resizing...");

            //Create new array with size+2, copy decoratives, and set it as the new array
            Entity[] newEnts = new Entity[entities.length+2];
            System.arraycopy(entities, 0, newEnts, 0, entities.length);
            entities = newEnts;
        }
        entities[entCount] = e;
        entCount++;
        return entCount - 1;
    }

    //GETTERS
    public Entity getEntity(String name) {
        for (Entity e : entities)
            if (e != null && e.name.equals(name)) return e;
        return null;
    }
    public Entity getEntity(int i) {
        return entities[i];
    }
    public Decorative getDecorative(int i) {
        return decoratives[i];
    }

    //REMOVERS
    public void removeEntity(String name) {
        physics.init();
        int index = -1;
        for (int i = 0; i < entities.length; i++)
            if (entities[i] != null) {
                if (entities[i].name.equals(name)) entities[i] = null;
                index = i;
            }

        //Couldn't find entity
        if (index == -1)
            return;

        entCount--;
        //Shift entity array back from the deletion
        if (entities.length - 1 - index >= 0)
            System.arraycopy(entities, index + 1, entities, index, entities.length - 1 - index);
    }
    public void removeDecorative(int i) {
        decoratives[i] = null;
        decCount--;

        //Shift decorative array back from the deletion
        if (decoratives.length - 1 - i >= 0)
            System.arraycopy(decoratives, i + 1, decoratives, i, decoratives.length - 1 - i);
    }
}