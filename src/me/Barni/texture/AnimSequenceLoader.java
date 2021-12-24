package me.Barni.texture;

import me.Barni.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AnimSequenceLoader {

    public static final float VALID_VERSION = 1.0f;

    static AnimSequence[] loadSequences(Logger logger, String fullPath, Texture txt) {

        try {
            //Open file
            File file = new File(fullPath);
            FileInputStream fis = new FileInputStream(file);

            //Read file
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, "UTF-8");

            //temporary sequence list, later will be converted to array
            ArrayList<AnimSequence> sequenceArrayList = new ArrayList<>();
            //The file JSON object
            JSONObject jsonObject = new JSONObject(str);

            //Check map version
            if (jsonObject.getFloat("version") != VALID_VERSION) {
                logger.err("[ANIM] Invalid file version! - " + fullPath);
                return null;
            }
            //Get texture frame count
            txt.frameCount = jsonObject.getInt("frames");

            JSONArray seqArray = jsonObject.getJSONArray("sequences");

            //Process every sequence
            for (int i = 0; i < seqArray.length(); i++) {

                //Current json object
                JSONObject jo = (JSONObject) seqArray.get(i);

                //Frames array in JSON object
                JSONArray dataJSONArray = jo.getJSONArray("frames");

                //Copy dataJSONArray to frames array
                int[] framesArray = new int[dataJSONArray.length()];
                for (int j = 0; j < dataJSONArray.length(); j++) {
                    framesArray[j] = dataJSONArray.getInt(j);
                }

                //Delays array in JSON object
                dataJSONArray = jo.getJSONArray("delays");

                //Copy dataJSONArray to delays array
                int[] delaysArray = new int[dataJSONArray.length()];
                for (int j = 0; j < dataJSONArray.length(); j++) {
                    delaysArray[j] = dataJSONArray.getInt(j);
                }


                if (framesArray.length != delaysArray.length) {
                    logger.err("[ANIM] Sequence \"" + jo.getString("name") +
                            "\" has different FRAME and DELAY length!"
                            + logger.getIndentStr() + "\n    At:   " + fullPath);
                    return null;
                }
                if (framesArray.length != txt.frameCount) {
                    logger.err("[ANIM] Sequence \"" + jo.getString("name") + "\" uses more frames, than defined above!"
                            + logger.getIndentStr() + "\n    At:   " + fullPath);
                    return null;
                }

                //Generate object from processed data
                AnimSequence currentSeq = new AnimSequence(
                        txt,
                        jo.getString("name"),
                        jo.getString("next"),
                        framesArray,
                        delaysArray,
                        txt.frameCount
                );

                //Add the new object to sequence array
                sequenceArrayList.add(currentSeq);
            }

            //Copy Arraylist sequences to primitive array
            AnimSequence[] finalSeqArray = new AnimSequence[sequenceArrayList.size()];
            for (int i = 0; i < sequenceArrayList.size(); i++) {
                finalSeqArray[i] = sequenceArrayList.get(i);
            }

            logger.info("[ANIM] Loaded anim sequences for: " + fullPath);
            return finalSeqArray;

        } catch (FileNotFoundException e) {
            logger.err("[AnimSeqLoader] Can't find file!" + logger.getIndentStr() + "\n    At:   " + fullPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            logger.err("[AnimSeqLoader] JSON error occurred!" + logger.getIndentStr() + "\n    At:   " + fullPath + "\n    Info: " + e.getMessage());
        }
        return null;
    }
}
