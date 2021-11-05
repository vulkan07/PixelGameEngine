package me.Barni;

import me.Barni.entity.Entity;
import me.Barni.entity.childs.LevelExit;
import me.Barni.entity.childs.Player;
import me.Barni.entity.childs.PressurePlate;
import me.Barni.physics.Physics;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import me.Barni.texture.TextureAtlas;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Map {
    Game game;
    public Physics physics;
    public TextureAtlas atlas;

    public int width, height, tileSize;
    private byte[] tiles;
    private byte[] backTiles;
    public Entity[] entities = new Entity[16];
    public Decorative[] decoratives = new Decorative[32];

    public int getDecCount() {
        return decCount;
    }

    private int decCount = 0;

    private String title;
    private Color bgColor;
    public Vec2D playerStartPos = new Vec2D(), playerStartVel = new Vec2D();


    Camera cam;
    BufferedImage txt;


    public String getTitle() {
        return title;
    }

    public void setBackGroundColor(Color c) {
        bgColor = c;
        for (int i = 0; i < game.clearBuffer.length; i++)
            game.clearBuffer[i] = bgColor.getRGB();
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


    public Map(Game g, int w, int h, int tSize) {
        width = h;
        height = w;
        tileSize = tSize;
        tiles = new byte[width * height];
        backTiles = new byte[width * height];
        game = g;
        atlas = new TextureAtlas(game, Material.materialPath.length, tileSize);

        game.logger.info("[MAP] Initialized new map, size: " + w + ", " + h);

        physics = new Physics(game, this);

        cam = new Camera(game, this);

        //TEST
        for (int i = 0; i < backTiles.length; i++)
            backTiles[i] = 0;
    }

    public void dumpCurrentMapIntoFile(String path) {
        game.logger.info("[MAP] Writing out current map");


        File file = new File(game.GAME_DIR + path + ".json");
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
        if (bgColor.getRGB() != game.white)
        {
            String cData = bgColor.getRed() + "," + bgColor.getGreen() + "," +bgColor.getBlue();
            mapObj.put("backGround", cData);
        }

        mapObj.put("spawnPos", playerStartPos.xi() + "," + playerStartPos.yi());
        mapObj.put("spawnVel", playerStartVel.xi() + "," + playerStartVel.yi());

        String data = "";
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
            game.logger.info("[MAP] Dumped map file into " + game.GAME_DIR + path + ".json");
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
        JSONObject entObj;

        int place = -1;
        for (int i = 0; i < entities.length; i++) {
            Entity e = entities[i];

            if (e == null) continue;
            if (e.name.equals("player") || e.name.equals("playerDieParticle")) continue;

            entObj = new JSONObject();

            place++;
            String[] className = e.getClass().toString().split("\\.");
            entObj.put("class", className[className.length - 1]);
            entObj.put("name", e.name);
            entObj.put("x", e.position.xi());
            entObj.put("y", e.position.yi());
            entObj.put("w", e.size.xi());
            entObj.put("h", e.size.yi());
            entObj.put("texture", e.texture.getPath());

            entObj.put("visible", e.visible);
            entObj.put("active", e.active);
            entObj.put("solid", e.solid);
            entObj.put("locked", e.locked);
            entObj.put("collidesWithMap", e.collidesWithMap);
            entObj.put("alive", e.alive);

            if (e instanceof PressurePlate) {
                entObj.put("force", ((PressurePlate) e).force);
                entObj.put("recharge", ((PressurePlate) e).recharge);
            }
            if (e instanceof LevelExit) {
                entObj.put("nextLevel", ((LevelExit) e).getNextMap());
            }

            array.put(place, entObj);
        }

        return array;
    }

    public void loadTextures() {

        for (int i = 1; i < Material.materialPath.length; i++) {
            Texture t = new Texture();
            t.loadTexture(game, Material.materialPath[i], 32, 32, true);
            atlas.addTexture(t);
        }
    }


    public void renderTiles(BufferedImage img) {

        Graphics g = img.getGraphics();
        g.setColor(new Color(0, 0, 20, 100));


        for (int i = 0; i < tiles.length; i++) {


            int x = i % width; //x
            int y = i / width; //Y
            if (
                    x * tileSize + tileSize < cam.scroll.x ||
                            x * tileSize > cam.scroll.x + game.WIDTH ||
                            y * tileSize + tileSize < cam.scroll.y ||
                            y * tileSize > cam.scroll.y + game.WIDTH
            ) continue;

            //BG
            if (backTiles[i] != 0) {
                txt = atlas.getTexture(backTiles[i] - 1);
                if (backTiles[i] != 0) {
                    g.drawImage(txt,
                            x * tileSize - cam.scroll.xi(),
                            y * tileSize - cam.scroll.yi(),
                            null);
                    if (!Material.translucent[backTiles[i]])
                        g.fillRect(x * tileSize - cam.scroll.xi(),
                                y * tileSize - cam.scroll.yi(),
                                tileSize,
                                tileSize);
                }
            }

            //FG
            if (tiles[i] == 0) continue;

            txt = atlas.getTexture(tiles[i] - 1);

            g.drawImage(txt,
                    x * tileSize - cam.scroll.xi(),
                    y * tileSize - cam.scroll.yi(),
                    null);
            //img.getGraphics().drawRect(x*tileSize,y*tileSize,tileSize,tileSize);
        }

        if (game.mapEditing)
            g.drawRect(
                    -cam.scroll.xi(),
                    -cam.scroll.yi(),
                    width * tileSize,
                    height * tileSize
            );
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

        if (!game.decorativeEditor.editing)
            if (game.player.position.dist(cam.view) > 50 && game.player.alive)
                cam.lookAt(game.player.position);
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


    public void addDecorative(Decorative dec) {
        if (decCount >= decoratives.length) {
            game.logger.err("Decoratives array is full!");
            return;
        }
        decoratives[decCount] = dec;
        decCount++;
    }

    public void addEntity(Entity e) {
        if (e instanceof Player)
            initPlayer((Player) e);
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == null) {
                if (e.name == null || e.name.equals(""))
                    game.logger.warn("[MAP] Entity added without name: " + e.getClass());
                entities[i] = e;
                e.setID(i);
                physics.init();
                game.logger.subInfo("[MAP] Added entity: " + e.getClass());
                return;
            }
        }
        game.logger.err("[MAP] Entity array is full!!");
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
            else if (e.name == name) return e;
        return null;
    }

    public void removeEntity(String name) {
        physics.init();
        for (int i = 0; i < entities.length; i++)
            if (entities[i] != null)
                if (entities[i].name == name) entities[i] = null;
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
