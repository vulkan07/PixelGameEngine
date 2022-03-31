package me.Barni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class Material {

    public static Logger logger;

    public static boolean loadMaterials(String absPath) {
        String rawStr;
        try {
            File file = new File(absPath);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            rawStr = new String(data, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            logger.err("Can't read materials.json!   " + absPath);
            return false;
        }

        JSONObject jsonObject;
        //Try to convert the raw string to JSONObject
        try {
            jsonObject = new JSONObject(rawStr);
        } catch (JSONException e) {
            logger.err("Can't read materials.json! " + e.getMessage());
            return false;
        }

        try {
            //Get "tiles" array
            JSONArray ja = jsonObject.getJSONArray("tiles");

            //Init arrays
            solid = new int[ja.length()];
            translucent = new int[ja.length()];
            path = new String[ja.length()];
            matCount = ja.length();

            //load data
            for(int i = 0; i < ja.length(); i++) {
                //Get data
                int matIndex = ((JSONObject) ja.get(i)).getInt("id");
                if (matIndex >= ja.length())
                {
                    logger.err("[MAT] Material id larger than the array: " + matIndex);
                    return false;
                }

                solid[matIndex] = ((JSONObject) ja.get(i)).getInt("solid");
                translucent[matIndex] = ((JSONObject) ja.get(i)).getInt("translucent");
                path[matIndex] = ((JSONObject) ja.get(i)).getString("path");
            }

        } catch (JSONException e) {

            logger.err("[MAT] Invalid materials.json file! " + e.getMessage());
            return false;
        }

        logger.info("[MAT] Loaded material info");
        return true;
    }

    private static int[] solid;
    private static int[] translucent;
    private static String[] path;
    private static int matCount;

    public static int isSolid(int id) {
        return solid[id];
    }
    public static  int isTranslucent(int id) {
        return translucent[id];
    }
    public static String getPath(int id) {
        return path[id];
    }
    public static  int getMatCount() {
        return matCount;
    }
}
