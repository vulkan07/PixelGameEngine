package me.Barni;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Map {
    Game game;
    public Physics physics;
    public TextureAtlas atlas;

    public int width, height, tileSize;
    private byte[] tiles;
    private byte[] backTiles;
    public Entity[] entities = new Entity[16];
    public Decorative[] decoratives = new Decorative[16];
    private int decCount = 0;
    public Vec2D playerStartPos, playerStartVel;


    Camera cam;
    BufferedImage txt;

    public byte getBackTile(int i) {
        return backTiles[i];
    }

    public void setBackTile(int i, int id) {
        backTiles[i] = (byte) id;
    }

    public byte getTile(int i) {
        return tiles[i];
    }

    public void setTile(int i, int id) {
        tiles[i] = (byte) id;
    }


    public void setTile(int x, int y, int id) {
        tiles[y * width + x] = (byte) id;
    }

    public byte getTile(int x, int y) {
        return tiles[y * width + x];
    }

    public int getTilesLength() {
        return tiles.length;
    }

    public void setTileArray(byte[] newTiles) {
        tiles = newTiles;
    }

    public Map(Game g, int w, int h, int tSize) {
        width = h;
        height = w;
        tileSize = tSize;
        tiles = new byte[width * height];
        backTiles = new byte[width * height];
        game = g;
        atlas = new TextureAtlas(game, Material.materialPath.length, tileSize);

        game.logger.info("[MAP] Initialized new map, size: " + w + ", " + h);

        physics = new Physics(game, this);

        cam = new Camera(game, this);

        //TEST
        for (int i = 0; i < backTiles.length; i++)
            backTiles[i] = 0;
    }


    public void dumpCurrentMapIntoFile(String path) {
        game.logger.info("[MAP] Writing out current map");

        String data = ".map=\n";
        File file = new File(game.GAME_DIR + path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //FG
        for (int i = 0; i < tiles.length; i++) {
            if (i % width == 0 && i != 0) {
                data += '\n';
            }
            data += tiles[i] + ",";
        }
        data += "\n\n.backMap=\n";
        //BG
        for (int i = 0; i < backTiles.length; i++) {
            if (i % width == 0 && i != 0) {
                data += '\n';
            }
            data += backTiles[i] + ",";
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

        for (int i = 1; i < Material.materialPath.length; i++) {
            Texture t = new Texture();
            t.loadTexture(game, Material.materialPath[i] + ".png", 32, 32, Material.materialPath[i] + ".anim");
            atlas.addTexture(t);
        }
    }

    public void renderTiles(BufferedImage img) {

        Graphics g = img.getGraphics();
        g.setColor(new Color(0, 0, 20, 100));


        for (int i = 0; i < tiles.length; i++) {


            int x = i % width; //x
            int y = i / width; //Y
            if (
                    x * tileSize + tileSize < cam.scroll.x ||
                            x * tileSize > cam.scroll.x + game.WIDTH ||
                            y * tileSize + tileSize < cam.scroll.y ||
                            y * tileSize > cam.scroll.y + game.WIDTH
            ) continue;

            //BG
            if (backTiles[i] != 0) {
                txt = atlas.getTexture(backTiles[i] - 1);
                if (backTiles[i] != 0) {
                    g.drawImage(txt,
                            x * tileSize - cam.scroll.xi(),
                            y * tileSize - cam.scroll.yi(),
                            null);
                    if (!Material.translucent[backTiles[i]])
                        g.fillRect(x * tileSize - cam.scroll.xi(),
                                y * tileSize - cam.scroll.yi(),
                                tileSize,
                                tileSize);
                }
            }

            //FG
            if (tiles[i] == 0) continue;

            txt = atlas.getTexture(tiles[i] - 1);
            if (tiles[i] == Material.WATER) {
                g.drawImage(txt,
                        x * tileSize - cam.scroll.xi(),
                        y * tileSize - cam.scroll.yi() + 6,
                        null);
            } else
                g.drawImage(txt,
                        x * tileSize - cam.scroll.xi(),
                        y * tileSize - cam.scroll.yi(),
                        null);
            //img.getGraphics().drawRect(x*tileSize,y*tileSize,tileSize,tileSize);
        }

        if (game.mapEditing)
            g.drawRect(
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

    public void renderDecoratives(BufferedImage img, int zPlane) {
        for (Decorative d : decoratives) {
            if (d != null)
                if (d.z == zPlane)
                    d.render(img, cam);
        }
    }

    public void tick() {

        atlas.update();

        if (game.player.position.dist(cam.view) > 50 && game.player.alive)
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
        if (e instanceof Player)
            initPlayer((Player) e);
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] == null) {
                entities[i] = e;
                physics.init();
                game.logger.subInfo("[MAP] Added entity: " + e.getClass());
                return;
            }
        }
        game.logger.err("[MAP] Entity array is full!!");
    }

    public void initPlayer(Player p) {
        p.spawnLocation = playerStartPos.copy();
        p.position = playerStartPos.copy();
        p.velocity = playerStartVel.copy();
        playerStartPos = null;

        playerStartVel = null;
    }

    public Entity getEntity(String name) {
        for (Entity e : entities)
            if (e == null) continue;
            else if (e.name == name) return e;
        return null;
    }

    public void removeEntity(String name) {
        physics.init();
        for (int i = 0; i < entities.length; i++)
            if (entities[i] != null)
                if (entities[i].name == name) entities[i] = null;
    }


}
