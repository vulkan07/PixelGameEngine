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
    int white = (0xFF << 24 | 181 << 16 | 218 << 8 | 232 << 0); //new Color(181, 218, 232).getRGB();   //Color value for canvas clear

    MouseHandler mouseHandler;          //Mouse
    KeyboardHandler keyboardHandler;    //Keyboard
    Logger logger;                      //Logger
    Map map;                            //Map

    public final String GAME_DIR;

    Player player;                      //FOR TEST
    ParticleEmitter pem;                //PEM ONLY FOR TEST purposes

    public Game(String wDir) {
        GAME_DIR = wDir;
    }

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
        //Entity e = new Entity(this, "test", new Vec2D(712, 600));
        //e.resistance = .1f;

        map.addEntity(player);
        //map.addEntity(e);
        map.loadMap("py.map");
        pem = new ParticleEmitter(new Vec2D(200, 200), new Vec2D(0, 1), true, 60, 3, 60);

        map.physics.init();

        //ClearBuffer
        clearBuffer = new int[(WIDTH / PX_SIZE) * (HEIGHT / PX_SIZE)];
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
        //map.getEntity("test").velocity.x -= 0.11f;

        //TEST\\
        pem.initialPos = mouseHandler.getPosition().div(PX_SIZE);
        pem.emitting = mouseHandler.isPressed(mouseHandler.RMB);
        pem.update();
    }


    public void render() {
        //=CLEAR CANVAS=\\
        System.arraycopy(clearBuffer, 0, buffer, 0, buffer.length);

        //Map
        map.renderTiles(image);
        map.renderEntities(image);
        map.renderDecoratives(image);


        //TEST\\
        pem.render(image);

        //= Draw image to buffer -> show =\\
        Graphics g = getBufferStrategy().getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);

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
        *////


        //int alpha = Math.max(50,colorRangeLimit(255-(int)new Vec2D(kx,ky).dist(mouseHandler.getPosition())));
        int xmap = (int)remap(kx, -16, 1920, 0, 255);
        g.setColor(new Color(xmap, xmap, xmap, 255));
        g.fillOval((int) kx, (int) ky, 32, 32);
        if (mouseHandler.isPressed(mouseHandler.LMB | mouseHandler.RMB)) {
            tx = mouseHandler.getPosition().x - 16;
            ty = mouseHandler.getPosition().y - 16;
        }
        kx = lerp(kx, tx, 0.1f);
        ky = lerp(ky, ty, 0.1f);

        g.dispose();
        getBufferStrategy().show();
    }

    float kx, ky, tx, ty;

    float lerp(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }

    float remap(float value, float low1, float high1, float low2, float high2) {

        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);

    }

    public int colorRangeLimit(int value) {
        return value > 255 ? 255 : (value < 0 ? 0 : value);
    }

    public Vec2D vecWithUniqueOrigin(Vec2D o, Vec2D v) {
        return new Vec2D(v.x - o.x, v.y - o.y);
    }
}