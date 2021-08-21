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

public class AnimSequenceLoader {

    public static final float VALID_VERSION = 1.0f;

    static AnimSequence[] loadSequences(Logger logger, String fullPath, Texture txt) {

        try {
            File file = new File(fullPath);
            FileInputStream fis = new FileInputStream(file);

            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, "UTF-8");

            ArrayList<AnimSequence> sequenceArrayList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(str);

            if (jsonObject.getFloat("version") != VALID_VERSION) {
                logger.err("[ANIM] Invalid file version! - " + fullPath);
                return null;
            }
            txt.frameCount = jsonObject.getInt("frames");

            JSONArray seqArray = jsonObject.getJSONArray("sequences");

            for (int i = 0; i < seqArray.length(); i++) {

                //Current json object
                JSONObject jo = (JSONObject) seqArray.get(i);

                //frames
                JSONArray fa = jo.getJSONArray("frames");
                int[] frames = new int[fa.length()];
                for (int j = 0; j < fa.length(); j++) {
                    frames[j] = fa.getInt(j);

                }

                //delays
                fa = jo.getJSONArray("delays");
                int[] delays = new int[fa.length()];
                for (int j = 0; j < fa.length(); j++) {
                    delays[j] = fa.getInt(j);
                }

                /*if (frames.length != delays.length) {
                    logger.err("[ANIM] Delay and frame list count doesn't match!");
                    return null;
                }*/

                AnimSequence currentSeq = new AnimSequence(
                        jo.getString("name"),
                        jo.getString("next"),
                        frames,
                        delays,
                        txt.frameCount
                );
                sequenceArrayList.add(currentSeq);
            }

            //Copy Arraylist to array
            AnimSequence[] finalSeqArray = new AnimSequence[sequenceArrayList.size()];
            for (int i = 0; i < sequenceArrayList.size(); i++) {
                finalSeqArray[i] = sequenceArrayList.get(i);
            }

            System.out.println("loaded");
            return finalSeqArray;

        } catch (FileNotFoundException e) {
            logger.err("[AnimSeqLoader] Can't find file! " + fullPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("jerr");
            //e.printStackTrace();
        }
        return null;
    }
}
