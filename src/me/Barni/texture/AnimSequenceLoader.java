package me.Barni.texture;

import me.Barni.Game;
import me.Barni.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


//-----------------------------------
//Purpose:  Creates an array AnimSequences for a Texture from a json file.
//          Called by the Texture.loadTexture();
//-----------------------------------

public class AnimSequenceLoader {

    //Only read files that are >= VALID_VERSION
    public static final float VALID_VERSION = 1.0f;
    private static Logger logger;
    private static Game game;

    private static void errMsg(String msg, String fullPath)
    {
        logger.err("[SeqLoader] " + msg + "  At:" + fullPath);
    }

    //Initialize (right now, only retrieves logger)
    public static void init(Game g) {
        game = g;
        logger = game.getLogger();
    }

    //Returns the sequence(s); returns null on error
    static AnimSequence[] loadSequences(String fullPath, Texture txt) {

        String rawStr; //Raw file text

        //Read file
        try {
            //Open file
            File file = new File(fullPath);
            FileInputStream fis = new FileInputStream(file);

            //Read file
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            rawStr = new String(data, StandardCharsets.UTF_8);

        } catch (FileNotFoundException e) {
            errMsg("Couldn't find file!", fullPath);
            return null;
        } catch (IOException e) {

            errMsg("Couldn't read file!", fullPath);
            return null;
        }

        //temporary sequence list, later will be converted to array
        ArrayList<AnimSequence> sequenceArrayList = new ArrayList<>();

        //The main JSON object
        JSONObject jsonObject;

        //Try to convert the raw string to JSONObject
        try {
            jsonObject = new JSONObject(rawStr);
        } catch (JSONException e) {
            errMsg("Invalid JSON file! (" + e.getMessage() + ")", fullPath);
            return null;
        }

        //Check map version, return if less than VALID_VERSION
        try {
            if (jsonObject.getFloat("version") < VALID_VERSION) {
                errMsg("Invalid file verison!", fullPath);
                return null;
            }
        } catch (JSONException e) {
            errMsg("Missing value: 'version' ", fullPath);
            return null;
        }

        //Get the image's frame count
        //And sets it to the Texture.frameCount
        try {
            txt.frameCount = jsonObject.getInt("frames");
        } catch (JSONException e) {
            errMsg("Missing value: 'frames' ", fullPath);
            return null;
        }

        //The json array object that holds the sequences
        JSONArray seqArray;
        //Try to read it from the main object
        try {
            seqArray = jsonObject.getJSONArray("sequences");
        } catch (JSONException e) {
            errMsg("Missing or invalid: 'sequences' ", fullPath);
            return null;
        }

        //Process every sequence
        for (int i = 0; i < seqArray.length(); i++) {

            //Current json object
            JSONObject jo = (JSONObject) seqArray.get(i);


            //If object has a "frame" tag, only read that, make a "static" sequence
            //And continue
            if (jo.has("frame")) {
                try {
                    //Generate object from processed data
                    AnimSequence currentSeq = new AnimSequence(
                            txt,
                            jo.getString("name"),
                            jo.getInt("frame")
                    );
                    //Add the new object to sequence array
                    sequenceArrayList.add(currentSeq);
                    continue;

                } catch (JSONException e) {
                    errMsg("Invalid \"frame\" tag!", fullPath);
                    return  null;
                }
            }

            //----------------------------------------------
            //"frames" array in JSON object
            JSONArray dataJSONArray = jo.getJSONArray("frames");

            //Copy dataJSONArray to frames array
            int[] framesArray = new int[dataJSONArray.length()];
            for (int j = 0; j < dataJSONArray.length(); j++) {
                //If the value is invalid, correct value to 1, and continue

                if (dataJSONArray.getInt(j) < 0 || dataJSONArray.getInt(j) >= txt.frameCount) {
                    errMsg("Invalid frame index: " + dataJSONArray.getInt(j), fullPath);
                    framesArray[j] = 1;
                }
                framesArray[j] = dataJSONArray.getInt(j);
            }

            //----------------------------------------------
            //"delays" array in JSON object
            dataJSONArray = jo.getJSONArray("delays");
            //If the array's length is 1, give warning
            if (dataJSONArray.length() == 1) {
                logger.warn("[SeqLoader] Possibly could be shortened to a \"frame\" tag;   At: " + fullPath);
            }

            //Copy dataJSONArray to delays array
            int[] delaysArray = new int[dataJSONArray.length()];
            for (int j = 0; j < dataJSONArray.length(); j++) {
                //If the value is invalid, correct value to 1, and continue
                if (dataJSONArray.getInt(j) < 0) {
                    errMsg("Invalid delay value: " + delaysArray[j], fullPath);
                    delaysArray[j] = 1;
                }
                delaysArray[j] = dataJSONArray.getInt(j);
            }

            //If the 2 array's length doesn't match, give error
            if (framesArray.length != delaysArray.length) {
                logger.err("[ANIM] Sequence \"" + jo.getString("name") +
                        "\" has different FRAME and DELAY length!"
                        + logger.getIndentStr() + "   At: " + fullPath);
                return null;
            }

            //Generate object from processed data
            AnimSequence currentSeq = new AnimSequence(
                    txt,
                    jo.getString("name"),
                    jo.getString("next"),
                    framesArray,
                    delaysArray,
                    framesArray.length
            );

            //Add the new object to sequence array
            sequenceArrayList.add(currentSeq);
        }

        //Copy Arraylist sequences to primitive array
        AnimSequence[] finalSeqArray = new AnimSequence[sequenceArrayList.size()];
        for (int i = 0; i < sequenceArrayList.size(); i++) {
            finalSeqArray[i] = sequenceArrayList.get(i);
        }

        logger.subInfo("[ANIM] Loaded anim sequences for: " + fullPath);
        return finalSeqArray;
    }
}
