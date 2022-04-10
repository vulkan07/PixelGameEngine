package me.Barni;


import me.Barni.entity.childs.Player;
import me.Barni.graphics.RenderableText;
import me.Barni.graphics.TextRenderer;
import me.Barni.texture.AnimSequenceLoader;
import me.Barni.window.KeyboardHandler;
import me.Barni.window.MouseHandler;
import me.Barni.window.Window;
import me.Barni.hud.HUD;
import me.Barni.hud.HUDButton;
import me.Barni.hud.HUDNotification;
import me.Barni.physics.Vec2D;
import me.Barni.tools.LevelEditor;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public final class Game implements Runnable {

    private boolean running = false;    //Running
    Thread thread;                      //Game thread

    public Window window;               //Window


    private final Random r = new Random(); // Main random

    private int WIDTH, HEIGHT;

    private HUD hud;
    private Logger logger;
    private Map map;
    private Player player;

    public String GAME_DIR; //Root game directory, which all file readers will use
    public String SHADER_DIR;
    public String BG_DIR;
    public String TEXTURE_DIR;
    public String MAP_DIR;
    public double GAME_VERSION;

    public String nextLevel; //if not empty, game will change to this map

    //Screen fading variables
    private boolean isScreenFadingIn, isScreenFadingOut;
    private float blankAlpha = 255;

    private Intro intro;
    private LevelEditor levelEditor;

    public String defaultWindowTitle;

    Font defaultFont = new Font("Verdana", Font.PLAIN, 20); // Default font


    //Constructor
    public Game(String wDir) {
        GAME_DIR = wDir;
    }

    //---------------------------------\\
    //--->  Load Title Random Msg  <---\\
    private String loadRandomTitleMsg() {
        String msg = "";
        String lines = "";

        //Try read titles.txt
        try {
            File file = new File(GAME_DIR + "titles.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            lines = new String(data, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            logger.warn("Can't find titles.txt to get random title");
        }

        //If read successfully choose random line as title
        if (!lines.equals("")) {
            String[] msgs = lines.split("\n");
            return msgs[r.nextInt(msgs.length - 1)];
        }

        return msg;
    }

    private boolean loadSearchPaths() {
        String rawStr;
        //Try read titles.txt
        try {
            File file = new File(System.getProperty("user.dir") + "/game.json");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            rawStr = new String(data, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            logger.err("Can't read game.json to load root search paths!");
            return false;
        }

        JSONObject jsonObject;
        //Try to convert the raw string to JSONObject
        try {
            jsonObject = new JSONObject(rawStr);
        } catch (JSONException e) {
            logger.err("Can't read game.json to load root search paths!");
            return false;
        }

        try {
            if (jsonObject.getString("base").equals("$"))
                GAME_DIR = System.getProperty("user.dir");
            else
                GAME_DIR = jsonObject.getString("base") + "/";
            TEXTURE_DIR = jsonObject.getString("textures").replace("$", GAME_DIR) + "/";
            BG_DIR = jsonObject.getString("background").replace("$", GAME_DIR) + "/";
            SHADER_DIR = jsonObject.getString("shaders").replace("$", GAME_DIR) + "/";
            MAP_DIR = jsonObject.getString("maps").replace("$", GAME_DIR) + "/";
            GAME_VERSION = jsonObject.getDouble("version");
        } catch (JSONException e) {

            logger.err("Invalid game.json file! " + e.getMessage());
            return false;
        }

        return true;
    }

    //--------------------------------\\
    //----------->  Start  <----------\\
    public synchronized void start(String title, int w, int h, boolean fullscreen, boolean resizeable, byte logLevel) {

        //this function only can be called once
        if (running) {
            getLogger().err("Tried to start game, while it's running!");
            return;
        }


        //Logger
        this.logger = new Logger(logLevel);

        if (!loadSearchPaths())
            throw new IllegalStateException("Can't load search paths!");

        System.out.println(">---------------------<");
        System.out.println("    PixelGameEngine");
        System.out.println("         v" + GAME_VERSION);
        System.out.println(">---------------------<");
        //Set Logger for static classes
        AnimSequenceLoader.logger = logger;
        Material.logger = logger;

        WIDTH = w;
        HEIGHT = h;


        window = new Window(this, title + "  -  " + loadRandomTitleMsg(), w, h, fullscreen);

        if (!Material.loadMaterials(GAME_DIR+"materials.json")) return;

        //Level editor
        levelEditor = new LevelEditor(this);

        //Intro
        intro = new Intro(this);

        //Hud
        hud = new HUD(this);


        //Add PlayerNotification
        getHud().getRoot().add(new HUDNotification(this, "PlayerNotification", "<initial>", 16, 30));

        //Add button & set colors
        getHud().getRoot().add(new HUDButton(this, "button", 200, 200, 30, "alma"));
        ((HUDButton) getHud().getRoot().getElement("button")).hoveredColor = new Color(80, 100, 120, 100);
        ((HUDButton) getHud().getRoot().getElement("button")).pressedColor = new Color(0, 150, 190, 100);

        GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);

        //Start unique game thread
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void loadNewMap(String path) {

        TextRenderer.init(this);
        RenderableText.init(this);

        nextLevel = "";

        if (map != null)
            map.destroy(); //Old map

        if (!Material.loadMaterials(GAME_DIR+"materials.json")) stop();

        MapLoader ml = new MapLoader(this);


        map = ml.loadMap(path); //Load map via ml

        //If map doesn't load, a hardcoded map loads
        if (map == null) {
            map = new Map(this, 3, 5, 32, "<default>");
            Tile[] defaultmap = new Tile[15];
            map.setTileArray(defaultmap);
        }

        map.createShaderPrograms();
        map.loadTextures(); //Load map textures

        player = new Player(this, "player", new Vec2D(48, 0)); //Remake player
        player.loadTexture("player_1"); //Load player texture

        map.addEntity(player);          //Add player
        map.cam.followEntity = player;  //Make camera follow player

        if (levelEditor != null)
            levelEditor.reloadMap(map);

        resetScreenFade(true);
        fadeInScreen(255);//Add screen fading effect
    }

    //--------------------------------\\
    //----------->   Run   <----------\\
    @Override
    public void run() {

        window.init();

        loadNewMap("01.map");
        map.createShaderPrograms();
        intro.start();


        final int desiredUPS = 60;
        final int desiredFPS = 60;

        final long updateThreshold = 1000000000 / desiredUPS;
        final long drawThreshold = 1000000000 / desiredFPS;

        long lastFPS = 0, lastUPS = 0, lastFPSUPSOutput = 0;

        int fps = 0, ups = 0;

        while (!GLFW.glfwWindowShouldClose(window.getWindow())) {
            if ((System.nanoTime() - lastFPSUPSOutput) > 1000000000) {

                //
                //System.out.println((double) fps + "/" + (double) ups + " | FPS/UPS");
                //
                fps = 0;
                ups = 0;

                lastFPSUPSOutput = System.nanoTime();
            }

            if ((System.nanoTime() - lastUPS) > updateThreshold) {
                float delta = (System.nanoTime() - lastUPS) * 0.0000001f;
                lastUPS = System.nanoTime();

                tick(1.1f);

                window.update();
                ups++;
            }

            if ((System.nanoTime() - lastFPS) > drawThreshold) {
                lastFPS = System.nanoTime();
                if (Window.isFocused()) {
                    render();
                }
                fps++;
            }

            // Calculate next frame, or skip if we are running behind
            if (!((System.nanoTime() - lastUPS) > updateThreshold || (System.nanoTime() - lastFPS) > drawThreshold)) {
                long nextScheduledUP = lastUPS + updateThreshold;
                long nextScheduledDraw = lastFPS + drawThreshold;

                long minScheduled = Math.min(nextScheduledUP, nextScheduledDraw);

                long nanosToWait = minScheduled - System.nanoTime();
                int finalWait = (int) (nanosToWait / 1000000 / 1.7f);

                if (finalWait > 0) //Safety check
                    try {
                        Thread.sleep(finalWait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }
        stop();
    }

    //---------------------------------\\
    //----------->   Stop   <----------\\
    public synchronized void stop() {
        getLogger().info("Game loop stopped");

        map.destroy();

        window.destroy();
        GLFW.glfwSetErrorCallback(null);
        GLFW.glfwTerminate();

        System.exit(0);
    }

    //---------------------------------\\
    //----------->   Tick   <----------\\
    private void tick(float delta) {
        if (!nextLevel.equals(""))
            loadNewMap(nextLevel);

        hud.update();

        MouseHandler.update(this);
        KeyboardHandler.update(this);

        map.tick(delta);

        levelEditor.update();
    }

    //---------------------------------\\
    //----------->  Render  <----------\\
    private void render() {

        //-----------------\\
        if (intro.isPlayingIntro() || getScreenFadeAlpha() != 0)
            window.clear(); //Only clear when playing intro, game overdraws every surface anyways

        if (intro.isPlayingIntro())
            intro.render();

        else
            map.render(map.cam);
        //-----------------\\

        updateScreenFade();


        GLFW.glfwSwapBuffers(window.getWindow());
    }

    private void updateScreenFade() {
        if (!intro.isPlayingIntro() && (isScreenFadingIn || isScreenFadingOut))
            intro.renderWheel(1 - getScreenFadeAlphaNormalized());
        //None -> back
        if (isScreenFadingOut) {
            blankAlpha += .1f;
            blankAlpha *= 1.05f;

            if (blankAlpha > 255) {
                blankAlpha = 255;
                isScreenFadingOut = false;
            }
        }
        //Black -> none
        if (isScreenFadingIn) {
            blankAlpha -= .1f;
            blankAlpha /= 1.05f;

            if (blankAlpha < 0) {
                blankAlpha = 0;
                isScreenFadingIn = false;
            }
        }
    }

    //Call to fade the screen FROM black
    public void fadeInScreen(int alphaStart) {
        if (alphaStart < 0 || alphaStart > 255 || isScreenFadingIn)
            return;
        blankAlpha = alphaStart;
        isScreenFadingOut = false;
        isScreenFadingIn = true;
    }

    //Call to fade the screen TO black
    public void fadeOutScreen(int alphaStart) {
        if (alphaStart < 0 || alphaStart > 255 || isScreenFadingOut)
            return;
        blankAlpha = alphaStart;
        isScreenFadingOut = true;
        isScreenFadingIn = false;
    }

    public void resetScreenFade(boolean isFadedOut) {
        isScreenFadingOut = false;
        isScreenFadingIn = false;
        blankAlpha = isFadedOut ? 255 : 0;
    }


    //----------------------------------\\
    //------------ GETTERS -------------\\
    //----------------------------------\\

    public Logger getLogger() {
        return logger;
    }

    public Map getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public LevelEditor getLevelEditor() {
        return levelEditor;
    }

    public HUD getHud() {
        return hud;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public float getScreenFadeAlpha() {
        return blankAlpha;
    }

    public float getScreenFadeAlphaNormalized() {
        return Vec2D.remap(blankAlpha, 0, 255, 0, 1);
    }

    public Intro getIntro() {
        return intro;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }
}