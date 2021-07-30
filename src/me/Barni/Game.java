package me.Barni;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

public class Game extends Canvas implements Runnable {
    //public String title;                //Title
    public int WIDTH, HEIGHT;           //Size
    public int PX_SIZE;                 //For pixel art: 2 or 4, for normal: 1
    private boolean running = false;    //Running

    Thread thread;                      //Game thread

    public JFrame window;               //Window
    private BufferedImage image;        //Image
    private int[] buffer, clearBuffer;  //ImageBuffer
    int white = new Color(137, 176, 205).getRGB();   //Color value for canvas clear

    MouseHandler mouseHandler;          //Mouse
    KeyboardHandler keyboardHandler;    //Keyboard
    Logger logger;                      //Logger
    Map map;                            //Map

    public final String GAME_DIR;

    public boolean mapEditing, solidityEditing;
    public int mapPaintID;
    Vec2D selectedTile = new Vec2D(0, 0);
    boolean selectedTileVisible = true;
    Font defaultFont = new Font("Verdana", Font.PLAIN, 24);


    Player player;                      //FOR TEST
    ParticleEmitter pem;                //PEM ONLY FOR TEST purposes

    public Game(String wDir) {
        GAME_DIR = wDir;
    }

    public synchronized void start(String title, int w, int h, int px_size, boolean fullscreen, boolean resizeable, byte logLevel) {

        if (running) {
            logger.err("Tried to start game, while it's running!");
            return;
        }

        //IMAGE DATA
        this.WIDTH = w;
        this.HEIGHT = h;
        this.PX_SIZE = px_size;
        image = new BufferedImage(WIDTH / PX_SIZE, HEIGHT / PX_SIZE, BufferedImage.TYPE_INT_ARGB);
        buffer = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();

        //WINDOW\\
        window = new JFrame(title);
        //Fullscreen
        if (fullscreen) {
            window.setUndecorated(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(WIDTH, HEIGHT);
        window.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        window.setLocationRelativeTo(null);
        window.add(this);
        window.setResizable(resizeable);
        window.setVisible(true);
        this.requestFocus();
        //window.setIconImage(); do this


        //BufferStrategy
        createBufferStrategy(2);


        //Utility
        this.logger = new Logger(logLevel);

        mouseHandler = new MouseHandler(window, this);
        this.addMouseListener(mouseHandler);

        keyboardHandler = new KeyboardHandler(this, false);
        this.addKeyListener(keyboardHandler);


        //Map
        MapLoader ml = new MapLoader(this);
        map = ml.loadMap(GAME_DIR + "01.map");

        //If map doesnt load, a hardcoded map loads
        if (map == null) {
            map = new Map(this, 3, 5, 32);
            byte[] defaultmap = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2,};
            player.position = new Vec2D(48, 0);
            map.setTileArray(defaultmap);
        }

        map.loadTextures();
        map.physics.init();

        player = new Player(this, "player", new Vec2D(512, 500));
        player.loadTexture("player.png", "player.anim");
        map.addEntity(player);

        //ClearBuffer
        clearBuffer = new int[(WIDTH / PX_SIZE) * (HEIGHT / PX_SIZE)];
        for (int i = 0; i < clearBuffer.length; i++)
            clearBuffer[i] = white;


        //Actual start
        running = true;
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {

        //IMPORTANT! Frames and ticks are bounded together
        int fps = 60;
        double framePerTick = 1000000000 / fps;
        double delta = 0;
        long now, last, timer = 0;
        int frames = 0;

        last = System.nanoTime();
        logger.info("[GAME] Preferred FPS: " + fps);
        logger.info("[GAME] Game loop ready to start\n"); // \n to separate loop logs

        while (running) {

            now = System.nanoTime();
            delta += (now - last) / framePerTick;
            timer += (now - last);
            last = now;


            if (delta >= 1) {
                //=TICK=\\
                tick();
                //=RENDER=\\
                render();
                frames++;


                delta--;
            }
            /*try {
                Thread.sleep((long)(delta));
            } catch (InterruptedException e) {
            }*/
            if (timer >= 1000000000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer = 0;
            }
        }
    }


    int tPos1, tPos2;

    public void tick() {
        mouseHandler.update();
        map.tick();
        //map.getEntity("test").velocity.x -= 0.11f;

        //Tile editor
        selectedTile = mouseHandler.getPosition().copy();
        selectedTile.add(map.cam.scroll);
        selectedTile.div(32);


        mapPaintID = mapPaintID < 1 ? 1 : (mapPaintID > Material.MAT_COUNT - 1 ? Material.MAT_COUNT - 1 : mapPaintID);

        if (mapEditing) {
            tPos1 = ((int) selectedTile.x + (int) selectedTile.y * map.width);
            if (mouseHandler.isPressed(mouseHandler.LMB)) {
                if (keyboardHandler.getKeyState(KeyboardHandler.SHIFT)) {
                    try {
                        map.solidTiles[tPos1] = true;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } else {

                    if (tPos1 != tPos2) {
                        tPos2 = tPos1;
                        try {
                            map.setTile(tPos1, mapPaintID);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                }
            } else if (mouseHandler.isPressed(mouseHandler.RMB))
                if (keyboardHandler.getKeyState(KeyboardHandler.SHIFT)) {
                    try {
                        map.solidTiles[tPos1] = false;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } else {
                    try {
                        map.setTile(tPos1, 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
        }

        if (keyboardHandler.getKeyState(KeyboardHandler.R))
            map.dumpCurrentMapIntoFile("currentMap.txt");

        //TEST\\
        //pem.position = mouseHandler.getPosition().div(PX_SIZE);
        //pem.position = player.position;
        //pem.active = mouseHandler.isPressed(mouseHandler.RMB);
    }


    public void render() {
        //=CLEAR CANVAS=\\
        System.arraycopy(clearBuffer, 0, buffer, 0, buffer.length);

        //Map
        map.renderDecoratives(image, -1); //Behind map layer
        map.renderTiles(image);

        map.renderDecoratives(image, 0); //Before map
        map.renderEntities(image);

        map.renderDecoratives(image, 1); //Before entities


        //= Draw image to buffer -> show =\\
        Graphics g = getBufferStrategy().getDrawGraphics();


        Graphics2D g2d = (Graphics2D) g;
        if (map.cam.zoom != 1)
            g2d.translate(-WIDTH / map.cam.zoom, -HEIGHT / map.cam.zoom);
        g2d.scale(map.cam.zoom, map.cam.zoom);
        g2d.drawImage(image, 0, 0, WIDTH, HEIGHT, null);

        //Selected tile
        if (selectedTileVisible) {
            g.setColor(new Color(150, 150, 150, mapEditing ? 180 : 50));
            g.fillRect((int) selectedTile.x * 32 - map.cam.scroll.xi(), (int) selectedTile.y * 32 - map.cam.scroll.yi(), 32, 32);
            g.drawRect((int) selectedTile.x * 32 - map.cam.scroll.xi(), (int) selectedTile.y * 32 - map.cam.scroll.yi(), 31, 31);
        }

        //Selected tile type
        if (mapEditing) {
            g.setColor(Color.WHITE);
            g.setFont(defaultFont);
            g.drawString("Editing", 8, 24);
            if (keyboardHandler.getKeyState(KeyboardHandler.SHIFT)) {

                g.drawString("Solidity", 8, 64);
            } else {
                try {
                    g.drawImage(map.atlas.getTexture(mapPaintID - 1), 8, 32, 64,64, null);
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }
        /*///
        b1.x = mouseHandler.getPosition().x;
        b1.y = mouseHandler.getPosition().y;

        float a = (b1.y - a1.y);
        float b = (a1.x - b1.x);
        float c = (a * a1.x + b * a1.y);

        float aa = (b2.y - a2.y);
        float bb = (a2.x - b2.x);
        float cc = (aa * a2.x + bb * a2.y);

        float det = a * bb - aa * b;
        if (det == 0)
            g.setColor(Color.BLUE);

        float x = (bb * c - b * cc) / det;
        float y = (a * cc - aa * c) / det;

        Vec2D cPoint = new Vec2D(x, y);

        Vec2D AB = vecWithUniqueOrigin(a2, b2);
        Vec2D AC = vecWithUniqueOrigin(a2, cPoint);
        float kAC = AC.dot(AB);
        float kAB = AB.dot(AB);

        if (!(0 < kAC && kAC < kAB) || a1.dist(cPoint) > a1.dist(b1)) {
                x = b1.x;
                y = b1.y;
        }

        g.drawLine((int) a2.x, (int) a2.y, (int) b2.x, (int) b2.y);
        g.drawLine((int) a1.x, (int) a1.y, (int) x, (int) y);
        */

        /*
        int xmap = (int) remap(kx, -16, 1920, 0, 255);
        g.setColor(new Color(xmap, xmap, xmap, 255));
        g.fillOval((int) kx, (int) ky, 32, 32);
        if (mouseHandler.isPressed(mouseHandler.LMB | mouseHandler.RMB)) {
            tx = mouseHandler.getPosition().x - 16;
            ty = mouseHandler.getPosition().y - 16;
        }
        kx = lerp(kx, tx, 0.1f);
        ky = lerp(ky, ty, 0.1f);
        */

        g.dispose();
        getBufferStrategy().show();
    }


    public int colorRangeLimit(int value) {
        return value > 255 ? 255 : (value < 0 ? 0 : value);
    }

    public Vec2D vecWithUniqueOrigin(Vec2D o, Vec2D v) {
        return new Vec2D(v.x - o.x, v.y - o.y);
    }
}