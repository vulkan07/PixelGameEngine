package me.Barni;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Map {
    Game game;
    public Physics physics;
    public TextureAtlas atlas;

    public int width, height, tileSize;
    public byte[] tiles;
    public Entity[] entities = new Entity[16];
    public Decorative[] decoratives = new Decorative[32];

    BufferedImage txt;

    public Map(Game g, int w, int h, int tSize) {
        width = h;
        height = w;
        tileSize = tSize;
        tiles = new byte[width * height];
        game = g;
        atlas = new TextureAtlas(game, Material.materialPath.length, tileSize);

        game.logger.info("[MAP] Initialized new map, size: " + w + ", " + h);

        physics = new Physics(game, this);

        //TEST
        for (int i = 0; i < tiles.length; i++)
            tiles[i] = 0;
    }

    public void loadMap(String path) {
        String fullPath = game.GAME_DIR + path;
        try {

            BufferedReader istream = new BufferedReader(new FileReader(new File(fullPath)));

            //==SIZE==\\
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


        } catch (FileNotFoundException e) {
            game.logger.err("[MAP] Can't find: " + fullPath);
        } catch (IOException e) {
            game.logger.err("[MAP] Can't read: " + fullPath);
        } catch (NumberFormatException e) {
            game.logger.err("[MAP] Invalid number format in: " + fullPath);
        }
        game.logger.info("[MAP] Successfully loaded: " + fullPath);
        game.logger.info("[MAP] New size: " + width + "," + height);
    }

    public void loadTextures() {
        BufferedImage buffer = null;
        //atlas.addTexture(null); //void

        for (int i = 1; i < Material.materialPath.length; i++) {

            try {
                buffer = ImageIO.read(new File(game.GAME_DIR + Material.materialPath[i]));
            } catch (IOException e) {
                game.logger.err("[MAP] Cannot load texture: " + game.GAME_DIR + Material.materialPath[i]);
            }
            atlas.addTexture(buffer);
        }
    }

    public void renderTiles(BufferedImage img) {

        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == 0) continue;
            txt = atlas.getTexture(tiles[i] - 1);
            int y = i / width; //Y
            int x = i % width; //x
            img.getGraphics().drawImage(txt, x * tileSize, y * tileSize, null);
            //img.getGraphics().drawRect(x*tileSize,y*tileSize,tileSize,tileSize);

        }

    }

    public void renderEntities(BufferedImage img) {
        for (Entity e : entities) {
            if (e != null)
                e.render(img);
        }

    }

    public void renderDecoratives(BufferedImage img) {
        for (Decorative d : decoratives) {
            if (d != null)
                d.render(img);
        }
    }

    public void tick() {
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

    public void addEntity(Entity e) {
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == null) {
                entities[i] = e;
                return;
            }
        }
        game.logger.err("[MAP] Entity array is full!!");
    }

    public Entity getEntity(String name) {
        for (Entity e : entities)
            if (e == null) continue;
            else if (e.name == name) return e;
        return null;
    }

    public void removeEntity(String name) {
        for (int i = 0; i < entities.length; i++)
            if (entities[i].name == name) entities[i] = null;
    }
}
