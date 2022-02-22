package me.Barni;

import me.Barni.entity.*;
import me.Barni.entity.childs.Checkpoint;
import me.Barni.entity.childs.Collectable;
import me.Barni.entity.childs.LevelExit;
import me.Barni.entity.childs.PressurePlate;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class MapLoader {

    Game game;
    Logger logger;
    Map map;
    private String fullPath; //Used by other methods

    public static final double VALID_MAP_FILE_VERSION = 1.3;

    public MapLoader(Game game) {
        this.game = game;
        this.logger = game.getLogger();
    }


    /**
     * Returns null if there's an error
     **/
    public Map loadMap(String completePath) {

        logger.increaseIndention("MAP LOADER");
        logger.info("[MAP+] Loading map: " + completePath);
        fullPath = completePath;

        try {
            File file = new File(completePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, StandardCharsets.UTF_8);

            JSONObject jobj = new JSONObject(str);
            JSONObject mapObj = jobj.getJSONObject("bmap");

            if (mapObj.getDouble("version") != VALID_MAP_FILE_VERSION) {
                errMsg("Invalid map version!");
                return null;
            }
            //MAP SIZE
            int w, h;
            w = mapObj.getInt("sizeX");
            h = mapObj.getInt("sizeY");


            String[] pathBroken = completePath.split("\\\\");
            map = new Map(game, h, w, 32, pathBroken[pathBroken.length-1]);

            JSONArray grid = mapObj.getJSONArray("grid");
            loadGrid(grid, false);

            JSONArray grid2 = mapObj.getJSONArray("backGrid");
            loadGrid(grid2, true);

            map.playerStartPos = strToVector(mapObj.getString("spawnPos"));
            map.playerStartVel = strToVector(mapObj.getString("spawnVel"));

            if (mapObj.has("backImage"))
                map.setBackgroundTexture(mapObj.getString("backImage"));
            else
                logger.info("Couldn't find background image");

            logger.increaseIndention("DECORATIVES");
            JSONObject objList = mapObj.getJSONObject("ObjectList");
            JSONArray decList = objList.getJSONArray("Decoratives");
            loadDecoratives(decList);
            logger.decreaseIndention("DECORATIVES");

            logger.increaseIndention("ENTITIES");
            JSONArray entList = objList.getJSONArray("Entities");
            loadEntities(entList);
            logger.decreaseIndention("ENTITIES");


            logger.info("[MAP+] Loaded map: " + fullPath);
            logger.decreaseIndention("MAP LOADER");
            return map;

        } catch (IOException e) {
            errMsg("Can't read map file!");
        } catch (JSONException e) {
            errMsg("Invalid JSON file!\n" + e.getMessage());
        }

        return null;
    }

    private Vec2D strToVector(String str) {
        String[] data = str.split(",");
        return new Vec2D(
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]));
    }


    private void errMsg(String msg) {
        logger.err("[MAP+] " + msg + "\n   In: " + fullPath);
        logger.decreaseIndention("MAP LOADER");
    }


    //*Load entities into Map entity from JSON*//
    //*Abstracted specific load to every entity class*//
    private void loadEntities(JSONArray lines) {

        for (int lineIndex = 0; lineIndex < lines.length(); lineIndex++) {
            JSONObject entObj = (JSONObject) lines.get(lineIndex);

            //*Entity-unique load*//
            switch (entObj.getString("class")) {
                case "LevelExit":
                    LevelExit l = new LevelExit(game, null, new Vec2D(), null); //Entity
                    l.loadFromEntityData(generalEntityLoader(entObj)); //Set common data
                    l.deserialize(entObj); //class-unique data
                    map.addEntity(l);
                    break;

                case "PressurePlate":
                    PressurePlate pp = new PressurePlate(game, null, new Vec2D());
                    pp.loadFromEntityData(generalEntityLoader(entObj));
                    pp.recharge = entObj.getInt("recharge");
                    pp.force = entObj.getFloat("force");
                    try {
                        pp.strictTrigger = entObj.getBoolean("strictTrigger");
                    } catch (JSONException e) {}
                    map.addEntity(pp);
                    break;

                case "Checkpoint":
                    Checkpoint cp = new Checkpoint(game, null, new Vec2D());
                    cp.loadFromEntityData(generalEntityLoader(entObj));
                    map.addEntity(cp);
                    break;

                case "Collectable":
                    Collectable c = new Collectable(game, null, new Vec2D());
                    c.loadFromEntityData(generalEntityLoader(entObj));
                    map.addEntity(c);
                    break;

                case "Entity":
                    errMsg("Entity class cannot be instantiated!");
                    break;
                default:
                    errMsg("Unknown entity class: " + entObj.getString("class") + "!");
                    return;
            }

        }
    }

    //*Loads an entity's common data*//
    private EntityData generalEntityLoader(JSONObject entObj) {
        String texture;
        float x, y, w, h;

        EntityData e = new EntityData();

        e.name = entObj.getString("name");
        texture = entObj.getString("texture");

        x = entObj.getFloat("x");
        y = entObj.getFloat("y");
        e.position = new Vec2D(x, y);

        w = entObj.getInt("w");
        h = entObj.getInt("h");
        e.size = new Vec2D(w, h);

        e.texture = new Texture();
        e.texture.loadTexture(game, texture, (int) w, (int) h, true);

        if (entObj.has("visible"))
            e.visible = entObj.getBoolean("visible");

        if (entObj.has("solid"))
            e.solid = entObj.getBoolean("solid");

        if (entObj.has("locked"))
            e.locked = entObj.getBoolean("locked");

        if (entObj.has("collidesWithMap"))
            e.collidesWithMap = entObj.getBoolean("collidesWithMap");

        if (entObj.has("active"))
            e.active = entObj.getBoolean("active");

        if (entObj.has("alive"))
            e.alive = entObj.getBoolean("alive");

        return e;
    }

    //*Load decoratives into Map entity from JSON*//
    private void loadDecoratives(JSONArray lines) {
        //loop trough lines
        for (int lineIndex = 0; lineIndex < lines.length(); lineIndex++) {
            JSONObject decObj = (JSONObject) lines.get(lineIndex);

            String path = decObj.getString("texture");
            float x, y;
            int z, w, h;
            x = decObj.getFloat("x");
            y = decObj.getFloat("y");
            z = decObj.getInt("z-layer");

            w = decObj.getInt("w");
            h = decObj.getInt("h");

            Decorative d = new Decorative(game, x, y, z, 0, w, h, path);

            d.parallax = decObj.getFloat("parallax");
            d.id = map.addDecorative(d); //TODO make this better look
        }
    }

    //*Moves JSON grid text data into Map entity *//
    private void loadGrid(JSONArray lines, boolean backGround) {
        String[] tilesRaw;
        //For every row
        for (int y = 0; y < map.height; y++) {
            try {
                tilesRaw = ((String) lines.get(y)).split(",");
            } catch (NullPointerException e) {
                errMsg("Invalid map grid format: too few lines!");
                return;
            }
            if (tilesRaw.length > map.width) {
                errMsg("Invalid map grid format: \"sizeX\" doesn't match row count! Too few rows!");
                return;
            }

            //For every column
            for (int x = 0; x < map.width; x++) {
                try {
                    tilesRaw[x] = tilesRaw[x].replace(" ", "");
                } catch (ArrayIndexOutOfBoundsException e) {
                    errMsg("Invalid map grid format: \"sizeX\" doesn't match row count! Too few rows!");
                    return;
                }
                if (backGround)
                    map.setBackTile(y * map.width + x, Integer.parseInt(tilesRaw[x]));
                else
                    map.setTile(y * map.width + x, Integer.parseInt(tilesRaw[x]));
            }
        }
    }

}
