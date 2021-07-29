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
    private int decCount = 0;


    Camera cam;
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

        cam = new Camera(game, this);

        //TEST
        for (int i = 0; i < tiles.length; i++)
            tiles[i] = 0;
    }

    public void dumpCurrentMapIntoFile(String path) {
        game.logger.info("[MAP] Writing out current map");

        String data = "";
        File file = new File(game.GAME_DIR + path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < tiles.length; i++) {
            if (i % width == 0 && i != 0) {
                data += '\n';
            }
            data += tiles[i] + ",";
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
            game.logger.info("[MAP] Dumped map file into " + game.GAME_DIR + path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addDecorative(Decorative dec) {
        if (decCount >= decoratives.length) {
            game.logger.err("Decoratives array is full!");
            return;
        }
        decoratives[decCount] = dec;
        decCount++;
    }

    public void loadTextures() {
        BufferedImage buffer = null;
        //atlas.addTexture(null); //void

        for (int i = 1; i < Material.materialPath.length; i++) {

            try {
                buffer = ImageIO.read(new File(game.GAME_DIR + "textures\\" + Material.materialPath[i]));
            } catch (IOException e) {
                game.logger.err("[MAP] Cannot load texture: " + game.GAME_DIR + "textures\\" + Material.materialPath[i]);
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
            img.getGraphics().drawImage(txt,
                    x * tileSize - cam.scroll.xi(),
                    y * tileSize - cam.scroll.yi(),
                    null);
            //img.getGraphics().drawRect(x*tileSize,y*tileSize,tileSize,tileSize);

        }

        if (game.mapEditing)
            img.getGraphics().drawRect(
                    -cam.scroll.xi(),
                    -cam.scroll.yi(),
                    width * tileSize,
                    height * tileSize
            );
    }

    public void renderEntities(BufferedImage img) {
        for (Entity e : entities) {
            if (e != null)
                e.render(img, cam);
        }

    }

    public void renderDecoratives(BufferedImage img) {
        for (Decorative d : decoratives) {
            if (d != null)
                d.render(img, cam);
        }
    }

    public void tick() {

        if (game.player.position.dist(cam.view) > 50)
            cam.lookAt(game.player.position);
        cam.update();

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
