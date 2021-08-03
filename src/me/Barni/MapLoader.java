package me.Barni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;


public class MapLoader {

    Game game;
    Logger logger;
    Map map;
    private String fullPath; //Used by other methods

    public static final double validMapHeader = 1.3;
    public static final int maxLines = 128;

    public MapLoader(Game game) {
        this.game = game;
        this.logger = game.logger;
    }



    /**
     * Returns null if there's an error
     **/
    public Map loadMap(String completePath) {

        System.out.println();
        logger.info("[MAP+] Loading map: " + completePath);
        fullPath = completePath;

        try {
            File file = new File(game.GAME_DIR + "json.map");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, "UTF-8");

            JSONObject jobj = new JSONObject(str);
            JSONObject mapObj = jobj.getJSONObject("bmap");

            if (mapObj.getDouble("version") != validMapHeader)
            {
                fail("Invalid map version!");
                return null;
            }
            //MAP SIZE
            int w, h;
            w = mapObj.getInt("sizeX");
            h = mapObj.getInt("sizeY");

            map = new Map(game, h, w, 32);

            JSONArray grid = mapObj.getJSONArray("grid");
            loadGrid(grid, false);

            JSONArray grid2 = mapObj.getJSONArray("backGrid");
            loadGrid(grid2, true);

            map.playerStartPos = strToVector(mapObj.getString("spawnPos"));
            map.playerStartVel = strToVector(mapObj.getString("spawnVel"));

            JSONObject objList = mapObj.getJSONObject("ObjectList");
            JSONArray decList = objList.getJSONArray("Decoratives");
            loadDecoratives(decList);

            JSONArray entList = objList.getJSONArray("Entities");
            loadEntities(entList);

            return map;

        } catch (IOException e) {
            fail("Can't read map file!");
        } catch (
                JSONException e) {
            fail("Invalid JSON file!\n" + e.getMessage());
        }

        return null;
    }

    private Vec2D strToVector(String str) {
        String[] data = str.split(",");
        return new Vec2D(
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]));
    }

    private void fail(String msg) {
        logger.err("[MAP+] " + msg + "\n   In: " + fullPath);
    }

    private void loadEntities(JSONArray lines) {
        for (int lineIndex = 0; lineIndex < lines.length(); lineIndex++) {
            JSONObject entObj = (JSONObject) lines.get(lineIndex);
            Entity ent = null;
            switch (entObj.getString("class")) {
                case "LevelExit":
                    LevelExit l = new LevelExit(game, null, new Vec2D(), null);
                    l.loadFromEntityData(generalEntityLoader(entObj));
                    l.setNextMap(entObj.getString("nextLevel"));
                    map.addEntity(l);
                    break;

                case "PressurePlate":
                    PressurePlate pp = new PressurePlate(game, null, new Vec2D());
                    pp.loadFromEntityData(generalEntityLoader(entObj));
                    pp.recharge = entObj.getInt("recharge");
                    pp.force = entObj.getFloat("force");
                    map.addEntity(pp);
                    break;

                case "Entity":
                    fail("Entity class cannot be instantiated!");
                    break;
                default:
                    fail("Unknown entity class: " + entObj.getString("class") + "!");
                    return;
            }

        }
    }

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

    private void loadDecoratives(JSONArray lines) {
        //loop trough lines
        for (int lineIndex = 0; lineIndex < lines.length(); lineIndex++) {
            JSONObject decObj = (JSONObject) lines.get(lineIndex);

            String path = decObj.getString("texture");
            int x, y, z, w, h;
            x = decObj.getInt("x");
            y = decObj.getInt("y");
            z = decObj.getInt("z-layer");

            w = decObj.getInt("w");
            h = decObj.getInt("h");

            Decorative d = new Decorative(game, x, y, z, 0, w, h, path);
            d.parallax = decObj.getFloat("parallax");


            map.addDecorative(d);
        }
    }


    private void loadGrid(JSONArray lines, boolean backGround) {
        String[] tilesRaw;
        //For every row
        for (int y = 0; y < map.height; y++) {
            try {
                tilesRaw = ((String) lines.get(y)).split(",");
            } catch (NullPointerException e) {
                fail("Invalid map grid format: too few lines!");
                return;
            }
            if (tilesRaw.length > map.width) {
                fail("Invalid map grid format: too much tiles!");
                return;
            }

            //For every column
            for (int x = 0; x < map.width; x++) {
                tilesRaw[x] = tilesRaw[x].replace(" ", "");
                if (backGround)
                    map.setBackTile(y * map.width + x, Integer.parseInt(tilesRaw[x]));
                else
                    map.setTile(y * map.width + x, Integer.parseInt(tilesRaw[x]));
            }
        }
    }

}
