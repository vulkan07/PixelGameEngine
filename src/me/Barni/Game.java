package me.Barni;


import me.Barni.entity.childs.Player;
import me.Barni.hud.HUD;
import me.Barni.hud.HUDButton;
import me.Barni.hud.HUDNotification;
import me.Barni.physics.Vec2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

public class Game extends Canvas implements Runnable {
    //public String title;               //Title
    public int WIDTH, HEIGHT;           //Size
    public int PX_SIZE;                 //For pixel art: 2 or 4, for normal: 1
    private boolean running = false;    //Running

    Thread thread;                      //Game thread

    public JFrame window;               //Window
    private BufferedImage image;        //Image
    private int[] buffer;  //ImageBuffer
    public int[] clearBuffer;  //Background
    int white = new Color(137, 176, 205).getRGB();   //Color value for canvas clear
    public DecorativeEditor decorativeEditor;

    private Random r = new Random();
    private String[] titleMsgs = {
            "ZeroPointerException",
            "Ereasing C:\\",
            "Apple.",
            "Random message",
            "FLIP THE SAUSAGE!!",
            "Roses are red, violets are blue, and you're the biggest assh*le",
            "1 + 1 = 404 not found",
            "RTX is just good ray CASTING",
            "42 I guess!?",
            "WHY IS IT 3AM ALREADY???",
            "I don't need sleep. I need answers.",
            "Fucking JSON writer rearranged my files",
            "DO NOT EVEN THINK ABOUT DOING IT",
            "The cake is a lie.",
            "HAHA U DED",
            "Right behind you",
            "Yeah 20% CPU for a hello world",
            "\"Java works on every pc.\" Not even on my friends'",
            "Pointers you idiot! Pointers!",
            "C++ in a java window title will un-virgin your oil",
            "If you know what JFrame is, i'll marry you",
            "Passing by reference is good",
            "OpenGL",
            "DirectX",
            "Ceremonia Matcha",
            "Jon Hopkins - Circle",
            "Vessel",
            "Chemically unstable Fluros in the orchard",
            "When life gives you lemons...",
            "Yeah my phone is at -1%",
            "I just wrote ONE LINE, now nothing's working...",
            "500+ bytes used just for this title",
            "yeah this has 45 FPS in fullscreen lol",
            "Trigonometry",
            "\"And that initializes the vertex buffers...\"",
            "Excel is not a database!",
            "DO NOT LEARN C++",
            "#Programming memes in my titles",
            ":wqa!",
            "CTRL + ALT + SHIFT + S",
            "for (int c = 0; c < 10; c++)",
            "Hyper text transfer protocol (HTTP)",
            "I know, this is bad graphics. Still better than pacman, huh?",
            "I totally have a healthy lifestyle",
            "That sniper is a spy!",
            "Kick your bot"
    };

    public HUD getHud() {
        return hud;
    }

    HUD hud;
    public MouseHandler mouseHandler;
    public KeyboardHandler keyboardHandler;
    public Logger logger;
    public Map map;
    Player player;

    public final String GAME_DIR;

    public boolean mapEditing, screenFadingIn, screenFadingOut;
    public int blankAlpha = 255;

    JTextField textField;
    Intro intro;

    public int mapPaintID;
    Vec2D selectedTile = new Vec2D(0, 0);
    boolean selectedTileVisible = true;
    Font defaultFont = new Font("Verdana", Font.PLAIN, 20);


    public Game(String wDir) {
        GAME_DIR = wDir;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public synchronized void start(String title, int w, int h, int px_size, boolean fullscreen, boolean resizeable, byte logLevel) {

        if (running) {
            logger.err("Tried to start game, while it's running!");
            return;
        }


        this.setBackground(Color.BLACK);

        //IMAGE DATA
        this.WIDTH = w;
        this.HEIGHT = h;
        this.PX_SIZE = px_size;
        image = new BufferedImage(WIDTH / PX_SIZE, HEIGHT / PX_SIZE, BufferedImage.TYPE_INT_ARGB);
        buffer = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();

        //WINDOW\\
        window = new JFrame(title + "  -  " + titleMsgs[r.nextInt(titleMsgs.length - 1)]);
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

        //ClearBuffer (backGround)
        clearBuffer = new int[(WIDTH / PX_SIZE) * (HEIGHT / PX_SIZE)];
        for (int i = 0; i < clearBuffer.length; i++)
            clearBuffer[i] = white;


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
            map.setTileArray(defaultmap);
        }

        map.loadTextures();

        //PLAYER
        player = new Player(this, "player", new Vec2D(48, 0));
        player.loadTexture("player_1");
        map.addEntity(player);


        decorativeEditor = new DecorativeEditor(this, map);

        intro = new Intro(this, "logo", image);
        intro.start();

        textField = new JTextField();
        window.add(textField);

        hud = new HUD(this);
        hud.getRoot().add(new HUDNotification(this, "PlayerNotification", "You died.", 16, 30));

        hud.getRoot().add(new HUDButton(this, "button", 200, 200, 30, "alma"));
        ((HUDButton) hud.getRoot().getElement("button")).hoveredColor = new Color(80, 100, 120, 100);
        ((HUDButton) hud.getRoot().getElement("button")).pressedColor = new Color(0, 150, 190, 100);

        //Actual start
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void loadNewMap(String path) {

        MapLoader ml = new MapLoader(this);
        map = ml.loadMap(path);
        map.loadTextures();
        player = new Player(this, "player", new Vec2D(48, 0));
        player.loadTexture("player_1");
        map.addEntity(player);
        screenFadingIn = true;
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
        stop();
    }


    int tPos1, tPos2;

    public synchronized void stop() {
        logger.info("Game loop stopped");
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    public void tick() {
        if (intro.isPlayingIntro()) return;

        hud.update();

        if (textField.getText().contains(" ")) {
            decorativeEditor.fieldListening = false;
            setFocusable(true);
            requestFocus();
        }
        mouseHandler.update();
        map.tick();
        decorativeEditor.tick();
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
                        map.setBackTile(tPos1, mapPaintID);
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
                        map.setBackTile(tPos1, 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } else {
                    try {
                        map.setTile(tPos1, 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
        }
    }


    public void render() {
        //=CLEAR CANVAS=\\

        if (blankAlpha != 255 && !intro.isPlayingIntro())
            System.arraycopy(clearBuffer, 0, buffer, 0, buffer.length);

        Graphics g = getBufferStrategy().getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        intro.render();
        g2d.drawImage(image, 0, 0, WIDTH, HEIGHT, null);

        if (blankAlpha != 255 && !intro.isPlayingIntro()) {

            //Map
            map.renderDecoratives(image, -1); //Behind map layer
            map.renderTiles(image);

            map.renderDecoratives(image, 0); //Before map
            map.renderEntities(image);

            map.renderDecoratives(image, 1); //Before entities


            if (map.cam.zoom != 1)
                g2d.translate(-WIDTH / map.cam.zoom, -HEIGHT / map.cam.zoom);
            g2d.scale(map.cam.zoom, map.cam.zoom);

            decorativeEditor.render(image, map.cam);

            hud.render(image);
            g2d.drawImage(image, 0, 0, WIDTH, HEIGHT, null);

            /*
            //Selected tile
            if (selectedTileVisible) {
                g.setColor(new Color(150, 150, 150, mapEditing ? 180 : 50));
                g.fillRect((int) selectedTile.x * 32 - map.cam.scroll.xi(), (int) selectedTile.y * 32 - map.cam.scroll.yi(), 32, 32);
                g.drawRect((int) selectedTile.x * 32 - map.cam.scroll.xi(), (int) selectedTile.y * 32 - map.cam.scroll.yi(), 31, 31);
            }
            */


            //Selected tile type
            if (mapEditing) {

                g.setColor(Color.WHITE);
                g.setFont(defaultFont);
                g.drawString("Editing", 8, 24);
                try {
                    g.drawImage(map.atlas.getTexture(mapPaintID - 1), 8, 32, 64, 64, null);
                    if (keyboardHandler.getKeyState(KeyboardHandler.SHIFT)) {

                        g.drawString("Back", 12, 88);
                        g.setColor(new Color(0, 0, 20, 100));
                        g.fillRect(8,
                                32,
                                64,
                                64);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }

            }
        }
        //None -> back
        if (screenFadingOut) {
            blankAlpha += 5;

            if (blankAlpha > 255) {
                blankAlpha = 255;
                screenFadingOut = false;
            }
        }
        //Black -> none
        if (screenFadingIn) {
            blankAlpha -= 5;

            if (blankAlpha < 0) {
                blankAlpha = 0;
                screenFadingIn = false;
            }
        }

        if (screenFadingIn || screenFadingOut || blankAlpha == 1) {
            g2d.setColor(new Color(0, 0, 0, blankAlpha));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        g.dispose();
        getBufferStrategy().show();
    }


    //public Vec2D vecWithUniqueOrigin(Vec2D o, Vec2D v) {
    //    return new Vec2D(v.x - o.x, v.y - o.y);
    //}
}