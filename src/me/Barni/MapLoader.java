package me.Barni;

import java.io.*;
import java.util.stream.Stream;

public class MapLoader {

    Game game;
    Logger logger;
    Map map;
    private String fullPath; //Used by other methods

    public static final String validMapHeader = "#bmap-v1.1";
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

        int lineIndex = 0;
        fullPath = completePath;

        try {

            //Open file, create vars
            BufferedReader istream = new BufferedReader(new FileReader(new File(fullPath)));
            String[] lines = new String[maxLines];
            String buffer;

            //Load file into lines with buffer
            for (int i = 0; i < maxLines; i++) {
                buffer = istream.readLine();
                if (buffer == null) continue;
                lines[i] = buffer;
            }
            istream.close();

            //Test header
            if (!lines[0].equalsIgnoreCase(validMapHeader)) {
                fail("Invalid map header!", 0);
                return null;
            }

            //Test footer "#end"
            /* TODO
            if (!lines[maxLines-1].equalsIgnoreCase("#end"))
            {
                fail("Invalid map footer!", maxLines-1);
                return null;
            }
            */


            boolean foundSize = false;
            int newW = 0, newH = 0;
            this.map = null;


            //Read file array
            for (lineIndex = 1; lineIndex < maxLines; lineIndex++) {
                if (lines[lineIndex] == null || lines[lineIndex].equalsIgnoreCase("")) continue;
                if (lines[lineIndex].equalsIgnoreCase("#end")) {
                    logger.info("[MAP+] Loaded map\n");
                    return this.map;
                }

                String line = lines[lineIndex];

                //If there's no dot at [0] -> error
                if (line.charAt(0) != '.') {
                    fail("Invalid line - not an Entry! ", lineIndex);
                    return null;
                }

                //Remove dot
                line = line.replace(".", "");

                //Split entry from it's value at =
                String entry = line.split("=")[0];
                entry = entry.toLowerCase();
                //ENTRY HANDLING
                switch (entry) {

                    case "map":
                        if (!foundSize) {
                            fail("\".size\" has to be before \".map\"", lineIndex);
                            return null;
                        }
                        this.map = new Map(game, newH, newW, 32);
                        loadGrid(lines, lineIndex + 1, newW, newH);
                        logger.info("[MAP+] Grid data loaded");
                        lineIndex += newH;
                        break;

                    case "size":
                        foundSize = true;
                        String[] data = line.split("=")[1].split(",");
                        newW = Integer.parseInt(data[0]);
                        newH = Integer.parseInt(data[1]);
                        break;

                    case "startpos":
                        String[] startPos = line.split("=")[1].split(",");
                        map.playerStartPos = new Vec2D();
                        map.playerStartPos.x = Integer.parseInt(startPos[0]);
                        map.playerStartPos.y = Integer.parseInt(startPos[1]);
                        break;

                    case "startvel":
                        String[] startVel = line.split("=")[1].split(",");
                        map.playerStartVel = new Vec2D();
                        map.playerStartVel.x = Integer.parseInt(startVel[0]);
                        map.playerStartVel.y = Integer.parseInt(startVel[1]);
                        break;


                    case "objectlist":
                        int count = Integer.parseInt(line.split("=")[1]);
                        loadObjects(lines, lineIndex + 1, count);
                        lineIndex += count; //Skip reading of obj list
                        break;

                    default:
                        fail("Unknown entry: " + entry, lineIndex);
                        return null;
                }
            }
        } catch (IOException e0) {
            fail("Can't read map!", -1);
            return null;
        } catch (NumberFormatException e1) {
            fail("Invalid number format!", lineIndex);
            return null;
        }
        return null;
    }

    private void loadObjects(String[] lines, int offset, int count) {
        if (map == null) {
            fail("Object list should be after map!", offset);
            return;
        }
        //loop trough lines
        for (int lineIndex = offset; lineIndex < count + offset; lineIndex++) {
            String line = lines[lineIndex];

            //DECORATIVE
            if (line.toLowerCase().contains("dec ")) {
                String[] params = line.toLowerCase().replace("dec ", "").split(",");
                loadDecorative(params, lineIndex);
                continue;
            }

            //ENTITY
            if (line.toLowerCase().contains("ent ")) {
                System.out.println("ent");
                continue;
            }

            logger.warn("[MAP+] Unknown object in object list: " + line);
        }
    }

    private void loadDecorative(String[] params, int lineIndex) {
        if (params.length < 7 || params.length > 7) {
            game.logger.err("Invalid object notation! Object list item line: " + lineIndex);
            return;
        }

        //Remove spaces
        for (int i = 0; i < params.length; i++)
            params[i] = params[i].replace(" ", "");

        //Construct decorative
        Decorative newDec = new Decorative(
                game,
                Integer.parseInt(params[0]),
                Integer.parseInt(params[1]),
                Integer.parseInt(params[2]),
                Float.parseFloat(params[3]),
                Integer.parseInt(params[4]),
                Integer.parseInt(params[5]),
                params[6]);
        map.addDecorative(newDec);
    }

    private void loadGrid(String[] lines, int offset, int xSize, int ySize) {
        String[] tilesRaw;
        //For every row
        for (int y = 0; y < ySize; y++) {
            try {
                tilesRaw = lines[y + offset].split(",");
            } catch (NullPointerException e) {
                fail("Invalid map grid format: too few lines!", y + offset);
                return;
            }

            if (tilesRaw.length > xSize) {
                fail("Invalid map grid format: too much tiles!", y + offset);
                return;
            }

            //For every column
            for (int x = 0; x < xSize; x++) {
                if (tilesRaw[x].contains("b")) {
                    tilesRaw[x] = tilesRaw[x].replace("b", "");
                    map.solidTiles[y * xSize + x] = false;
                } else
                    tilesRaw[x] = tilesRaw[x].replace(" ", "");
                map.setTile(y * xSize + x, Integer.parseInt(tilesRaw[x]));
            }
        }
    }

    private void fail(String msg, int line) {
        logger.err("[MAP+] " + msg + "\n   In: " + fullPath + "\n   At line: " + (line + 1));
    }

/*
    public void loadMap(String path) {
        System.out.println();
        String fullPath = game.GAME_DIR + path;
        try {

            BufferedReader istream = new BufferedReader(new FileReader(new File(fullPath)));

            if (!istream.readLine().equals(validMapHeader)) {
                game.logger.err("Invalid map header! " + fullPath);
                return;
            }


            //GRID\\
            //==SIZE==\\

            String[] startPos = istream.readLine().split("startpos=");
            startPos = startPos[1].split(",");
            game.player.position.x = Integer.parseInt(startPos[0]);
            game.player.position.y = Integer.parseInt(startPos[1]);

            String[] startVel = istream.readLine().split("startvel=");
            startVel = startVel[1].split(",");
            game.player.velocity.x = Integer.parseInt(startVel[0]);
            game.player.velocity.y = Integer.parseInt(startVel[1]);

            int xSize = 0, ySize = 0;
            String[] size = istream.readLine().split("=");

            //[0] should be "size", [1] should be "<x>,<y>"
            if (size[0].equalsIgnoreCase("size")) {
                String[] nums = size[1].split(",");
                xSize = Integer.parseInt(nums[0]);
                ySize = Integer.parseInt(nums[1]);
                width = xSize;
                height = ySize;
                tiles = new byte[width * height];
            } else {
                game.logger.err("[MAP] Missing size data in map " + fullPath);
                return;
            }

            if (xSize * ySize < 4) {
                game.logger.err("[MAP] Map is too small: " + fullPath);
                return;
            }
            if (xSize * ySize > 2040) {
                game.logger.warn("[MAP] Map is possibly too big for screen: " + fullPath);
            }

            //==GRID==\\
            String[] lineElems;

            for (int y = 0; y < ySize; y++) {
                try {
                    lineElems = istream.readLine().split(",");
                } catch (NullPointerException | IOException e) {
                    game.logger.err("[MAP] Invalid map grid format: too much line in " + fullPath);
                    return;
                }
                if (lineElems.length != xSize) {
                    game.logger.err("[MAP] Invalid map grid format in " + fullPath + ", at line " + (y + 2));
                    return;
                }
                for (int x = 0; x < xSize; x++) {
                    tiles[y * xSize + x] = (byte) Integer.parseInt(lineElems[x]);
                }
            }
            game.logger.info("[MAP] Loaded grid data");

            if (!loadEntities(path, istream)) {return;}


        } catch (FileNotFoundException e) {
            game.logger.err("[MAP] Can't find: " + fullPath);
        } catch (IOException e) {
            game.logger.err("[MAP] Can't read: " + fullPath);
        } catch (NumberFormatException e) {
            game.logger.err("[MAP] Invalid number format in: " + fullPath);
        }
        catch (NullPointerException e) {
            game.logger.err("[MAP] Invalid data structure: " + fullPath);
        }
        game.logger.info("[MAP] New size: " + width + ", " + height);

        game.logger.info("[MAP] Successfully loaded: " + fullPath + "\n");

    }

    public boolean loadEntities(String path, BufferedReader istream) throws IOException
    {
        game.logger.info("[MAP] Loading entities & decoratives");
        int lines = 0;

        String fline;
        do
        {
            fline = istream.readLine();
            //System.out.println(fline);
            lines++;
            if (lines >= 128)
            {
                game.logger.err("[MAP] Can't find object list!");
                return false;
            }
        } while (!fline.contains("+ObjectList"));

        //obj loop
        lines = 0;
        while (true)
        {
            String line = istream.readLine();
            lines++;


            if (lines >= 256)
            {
                //if object list is longer than 255 then give error
                game.logger.err("[MAP] Can't find object list end!");
                return false;
            }
            if (line.toLowerCase().contains("-objectlist"))
            {
                return true;
            }

            //DECORATIVE
            if (line.toLowerCase().contains("dec "))
            {
                String[] params = line.replace("dec ", "").split(",");
                if (params.length < 5 || params.length > 5)
                {
                    game.logger.err("Invalid object notation! Object list item line: "+lines);
                    return false;
                }
                if(decCount >= decoratives.length)
                {
                    game.logger.err("Decoratives array is full!");
                    return false;
                }

                for (int i = 0; i < params.length; i++)
                    params[i] = params[i].replace(" ", "");

                decoratives[decCount] = new Decorative(
                        game,
                        Integer.parseInt(params[0]),
                        Integer.parseInt(params[1]),
                        Integer.parseInt(params[2]),
                        Integer.parseInt(params[3]),
                        params[4]);
                decCount++;
            }

            //ENTITY
            if (line.toLowerCase().contains("ent "))
            {
                String[] params = line.replace("dec ", "").split(",");
                if (params.length < 5 || params.length > 5)
                {
                    game.logger.err("Invalid object notation! Object list item line: "+lines);
                    return false;
                }

            }
        }

    }
*/

}
