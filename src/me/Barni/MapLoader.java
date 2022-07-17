package me.Barni;

import me.Barni.entity.*;
import me.Barni.entity.childs.*;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class MapLoader {

    private Game game;
    private Logger logger;
    private Map map;
    private String fullPath;
    private boolean silent;

    public static final double VALID_MAP_FILE_VERSION = 1.3;

    public MapLoader(Game game) {
        this.game = game;
        this.logger = game.getLogger();
    }

    //Returns an error as a string if the map is not valid
    //Returns "" if it is valid
    public static String isValidMapFile(String path, Game game) {
        //TRY TO FIND FILE
        File file = new File(path);
        if (!file.exists()) {
            file = new File(game.GAME_DIR + path);
            if (!file.exists()) {
                file = new File(game.MAP_DIR + path);
            }
        }
        if (!file.exists())
            return "File missing";

        //TRY TO READ FILE
        String rawStr;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            rawStr = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Cannot read file";
        }

        //TRY TO CONVERT FILE TO JSON
        JSONObject fullObj, mapObj;
        try {
            fullObj = new JSONObject(rawStr);
            mapObj = fullObj.getJSONObject("bmap");
        } catch (JSONException e) {
            return "Invalid JSON:" + e.getMessage();
        }

        //CHECK IF ESSENTIAL KEYS ARE PRESENT
        String[] essentials = {"sizeX", "sizeY", "version", "grid", "backGrid", "spawnPos", "spawnVel", "ObjectList"};

        for (String key : essentials) {
            if (!mapObj.has(key))
                return "Missing key: '" + key + "'";
        }

        //Check if values are valid
        try {
            mapObj.getInt("sizeX");
            mapObj.getInt("sizeY");
            mapObj.getDouble("version");
            mapObj.getString("spawnPos");
            mapObj.getString("spawnVel");
        } catch (JSONException e) {
            return "Invalid value: " + e.getMessage();
        }

        //Check version
        if (mapObj.getDouble("version") != VALID_MAP_FILE_VERSION) {
            return "Invalid file version: " + mapObj.getDouble("version");
        }

        //TODO finish checking

        return "";
    }

    private static String checkGridValid(JSONObject grid) {
        return "";
    }

    /**
     * Returns null if there's an error
     **/
    public Map loadMap(String relPath, boolean silent) {
        this.silent = silent;

        if (!silent) {
            logger.info("[MAP-LOADER] Loading map: " + relPath);
            logger.increaseIndentation("MAP LOADER");
        }

        File file = new File(relPath);
        if (!file.exists()) {
            file = new File(game.GAME_DIR + relPath);
            if (!file.exists()) {
                file = new File(game.MAP_DIR + relPath);
            }
        }
        fullPath = file.getAbsolutePath();
        try {

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


            String[] pathBroken = fullPath.split("\\\\");
            map = new Map(game, h, w, 32, pathBroken[pathBroken.length - 1]);

            JSONArray grid = mapObj.getJSONArray("grid");
            if (!loadGrid(grid, false))
                return null;

            JSONArray grid2 = mapObj.getJSONArray("backGrid");
            if (!loadGrid(grid2, true))
                return null;

            map.playerStartPos = strToVector(mapObj.getString("spawnPos"));
            map.playerStartVel = strToVector(mapObj.getString("spawnVel"));

            if (mapObj.has("backImage"))
                map.setBackgroundTexture(mapObj.getString("backImage"));
            else
                logger.info("Couldn't find background image");

            logger.increaseIndentation("DECORATIVES");
            JSONObject objList = mapObj.getJSONObject("ObjectList");
            JSONArray decList = objList.getJSONArray("Decoratives");
            loadDecoratives(decList);
            logger.decreaseIndentation("DECORATIVES");

            logger.increaseIndentation("ENTITIES");
            JSONArray entList = objList.getJSONArray("Entities");
            loadEntities(entList);
            logger.decreaseIndentation("ENTITIES");

            logger.info("[MAP-LOADER] Loaded map: " + fullPath);
            logger.decreaseIndentation("MAP LOADER");
            return map;

        } catch (IOException e) {
            errMsg("Can't read map file!");
        } catch (JSONException e) {
            errMsg("Invalid JSON file! : " + e.getMessage());
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
        if (silent) return;
        logger.err("[MAP-LOADER] " + msg + "    In: " + fullPath);
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
                    pp.loadFromEntityData(generalEntityLoader(entObj)); //Common
                    pp.deserialize(entObj); //Class-unique

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
        //TODO: don't load textures here!
        e.texture.loadTexture(texture, (int) w, (int) h);

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
    private boolean loadGrid(JSONArray lines, boolean backGround) {
        String[] tilesRaw;
        //For every row
        for (int y = 0; y < map.getHeight(); y++) {
            try {
                tilesRaw = ((String) lines.get(y)).split(",");
            } catch (NullPointerException e) {
                errMsg("Invalid map grid format: too few lines!");
                return false;
            }
            if (tilesRaw.length > map.getWidth()) {
                errMsg("Invalid map grid format: \"sizeX\" doesn't match row count! Too few rows!");
                return false;
            }

            //For every column
            for (int x = 0; x < map.getWidth(); x++) {
                try {
                    tilesRaw[x] = tilesRaw[x].replace(" ", "");
                } catch (ArrayIndexOutOfBoundsException e) {
                    errMsg("Invalid map grid format: \"sizeX\" doesn't match row count! Too few rows!");
                    return false;
                }

                //TODO error handling
                int id, type;
                if (tilesRaw[x].contains(":")) {
                    id = Integer.parseInt(tilesRaw[x].split(":")[0]);
                    type = Integer.parseInt(tilesRaw[x].split(":")[1]);
                } else {
                    id = Integer.parseInt(tilesRaw[x]);
                    type = 0;
                }

                if (backGround)
                    map.setBackTile(y * map.getWidth() + x, new Tile(id, type));
                else
                    map.setTile(y * map.getWidth() + x, new Tile(id, type));
            }
        }
        return true;
    }

}
