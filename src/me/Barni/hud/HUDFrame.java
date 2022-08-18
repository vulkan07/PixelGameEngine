package me.Barni.hud;

import me.Barni.Game;
import me.Barni.graphics.GraphicsUtils;
import me.Barni.graphics.Quad;
import me.Barni.graphics.QuadBatch;
import me.Barni.hud.events.ButtonEventListener;
import me.Barni.physics.Vec2D;

public class HUDFrame extends HUDElement {

    private QuadBatch frame;
    private Quad closeIcon;
    private float currentHeight;
    private float targHeight;
    private HUDButton closeButton;

    public HUDFrame(Game g, String name, int x, int y, int w, int h) {
        super(g, name, x, y, w, h);
        currentHeight = h;
        targHeight = h;
    }

    public void setFrame(String name) {
        frame.loadTexture(name);
    }

    @Override
    public void setPosition(float x, float y) {
        if (parent != null){
            x += parent.x;
            y += parent.y;
        }

        this.x = x;
        this.y = y;

        closeButton.setPosition(-5,-5);
        closeIcon.setPosition(new Vec2D(x + 10, y + 30));
    }

    @Override
    public void update() {
        super.update();
        currentHeight = GraphicsUtils.lerp(currentHeight, targHeight, 0.4f);
    }

    @Override
    public void render() {
        if (!visible)
            return;

        super.render();

        GraphicsUtils.nonaSlice(frame, x, y, w, currentHeight);
        frame.render(null);
        closeIcon.render(null);
    }

    @Override
    public void init() {
        frame = new QuadBatch();
        closeIcon = new Quad(x + 10, y + 30, 16, 16);
        closeIcon.loadTexture("gui/collapse");
        setFrame("gui/frameDef");
        closeButton = new HUDButton(game, "_", 0,0, 30, 24, "");
        closeButton.setVisible(false);
        closeButton.init();
        //closeButton.setImage("gui/buttonDef"); //REMOVE
        this.add(closeButton);

        HUDFrame frame = this;
        closeButton.setListener(new ButtonEventListener() {
            @Override
            public void onPressed() {
                if (frame.isOpen())
                    frame.close();
                else
                    frame.open();
            }

            @Override
            public void onReleased() {

            }
        });
        super.init();
    }

    public boolean isOpen() {
        return open;
    }

    private boolean open = true, closeable = true;

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    public void open() {
        for (HUDElement element : childs) {
            if (element.getName().equals("_"))
                continue;
            element.setEnabled(true);
            element.show();
        }
        open = true;
        targHeight = h;
    }

    public void close() {
        for (HUDElement element : childs) {
            if (element.getName().equals("_"))
                continue;
            element.setEnabled(false);
            element.hide();
        }
        open = false;
        targHeight = 25f;
    }
}
