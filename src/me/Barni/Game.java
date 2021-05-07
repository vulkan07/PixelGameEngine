package me.Barni;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {
    //public String title;                //Title
    public int WIDTH, HEIGHT;           //Size
    public int PX_SIZE;                 //For pixel art: 2 or 4, for normal: 1
    private boolean running = false;    //Running

    public JFrame window;               //Window
    private BufferedImage image;        //Image
    private int[] buffer, clearBuffer;  //ImageBuffer
    int white = new Color(199, 236, 255).getRGB();   //Color value for canvas clear

    MouseHandler mouseHandler;          //Mouse
    KeyboardHandler keyboardHandler;    //Keyboard
    Logger logger;                      //Logger
    Map map;                            //Map

    public static final String GAME_DIR = "C:\\Dev\\";

    Player player;                      //FOR TEST
    ParticleEmitter pem;                //PEM ONLY FOR TEST purposes

    public void start(String title, int w, int h, int px_size, boolean fullscreen, boolean resizeable, byte logLevel) {

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
        window.requestFocus();

        //BufferStrategy
        createBufferStrategy(2);


        //Utility
        this.logger = new Logger(logLevel);

        mouseHandler = new MouseHandler(window, this);
        this.addMouseListener(mouseHandler);

        keyboardHandler = new KeyboardHandler(this, false);
        this.addKeyListener(keyboardHandler);

        //Map
        map = new Map(this, 60, 34, 32);
        map.loadTextures();

        //=TEST=\\
        player = new Player(this, "player", new Vec2D(512, 500));
        player.loadTexture("test.png", "test.anim");
        Entity e = new Entity(this, "test", new Vec2D(712,600));
        e.resistance = .1f;

        map.addEntity(player);
        map.addEntity(e);
        map.loadMap("py.map");
        pem = new ParticleEmitter(new Vec2D(200, 200), new Vec2D(0, -2), true, 60, 3, 60);

        map.physics.init();

        //ClearBuffer
        clearBuffer = new int[WIDTH / PX_SIZE * HEIGHT / PX_SIZE];
        for (int i = 0; i < clearBuffer.length; i++)
            clearBuffer[i] = white;


        //Actual start
        running = true;
        new Thread(this).start();
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
        logger.info("Preferred FPS: " + fps);
        logger.info("Game loop ready to start\n"); // \n to separate loop logs

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

            if (timer >= 1000000000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer = 0;
            }
        }
    }

    public void tick() {
        mouseHandler.update();
        map.tick();
        map.getEntity("test").velocity.x -= 0.11f;

        //TEST\\
        pem.initialPos = mouseHandler.getPosition().div(PX_SIZE);
        pem.emitting = mouseHandler.isPressed(mouseHandler.LMB);
        pem.update();
    }

    public void render() {
        //=CLEAR CANVAS=\\
        System.arraycopy(clearBuffer, 0, buffer, 0, buffer.length);

        //Map
        map.renderTiles(image);
        map.renderEntities(image);


        //TEST\\
        pem.render(image);

        //= Draw image to buffer -> show =\\
        Graphics g = getBufferStrategy().getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
        g.dispose();
        getBufferStrategy().show();
    }


    // public int colorRangeLimit(int num) {
    //  return num > 255 ? 255 : (num < 0 ? 0 : num);
    // }

}