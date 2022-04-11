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
            try {
                maxTypes = jsonObject.getInt("maxTypes");
            } catch (Exception e) {
                logger.err("Invalid field \"maxTypes\"!");
                maxTypes = 8;
            }

            matCount=0;
            //Find the biggest material value
            for (int i = 0; i < ja.length(); i++) {
                int cid = ((JSONObject) ja.get(i)).getInt("id");
                if (cid > matCount)
                    matCount = cid;
            }
            matCount++; //Shift value because arrays start at 0

            //Init arrays
            solid = new int[matCount][maxTypes];
            translucent = new int[matCount][maxTypes];
            path = new String[matCount][maxTypes];

            //load data
            for (int i = 0; i < ja.length(); i++) {
                //Get data
                int matIndex = ((JSONObject) ja.get(i)).getInt("id");
                int typeIndex = ((JSONObject) ja.get(i)).getInt("type");
                if (matIndex >= ja.length()) {
                    logger.err("[MAT] Material id larger than the array: " + matIndex);
                    return false;
                }

                solid[matIndex][typeIndex] = ((JSONObject) ja.get(i)).getInt("solid");
                translucent[matIndex][typeIndex] = ((JSONObject) ja.get(i)).getInt("translucent");
                path[matIndex][typeIndex] = ((JSONObject) ja.get(i)).getString("path");
            }

        } catch (JSONException e) {

            logger.err("[MAT] Invalid materials.json file! " + e.getMessage());
            return false;
        }

        logger.info("[MAT] Loaded material info");
        return true;
    }

    private static int[][] solid;
    private static int[][] translucent;
    private static String[][] path;
    private static int matCount;


    private static int maxTypes;

    public static int isSolid(int id, int type) {
        return solid[id][type];
    }

    public static int isTranslucent(int id, int type) {
        return translucent[id][type];
    }

    public static String getPath(int id, int type) {
        return path[id][type];
    }

    public static int getMatCount() {
        return matCount;
    }

    public static int getMaxTypes() {
        return maxTypes;
    }
}
