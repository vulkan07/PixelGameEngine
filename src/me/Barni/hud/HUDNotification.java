package me.Barni.hud;


import me.Barni.Game;
import me.Barni.graphics.RenderableText;
import me.Barni.physics.Vec2D;

import java.awt.*;

public class HUDNotification extends HUDElement {

    public String message;
    public Color msgColor;
    private float tx, indent;
    private RenderableText velText;

    public HUDNotification(Game g, String name, String text, int indent, int y) {
        super(g, name, 0, 0, 1, 32);
        this.x = -1000;
        this.y = y;
        this.indent = indent;

        if (indent < 0) {
            game.getLogger().err("[HUDNotification] - \"" + name + "\" - negative indent value: " + indent + "! Defaulting to 16.");
            this.indent = 16;
        }

        setMessage(text);

        this.msgColor = Color.WHITE;
    }

    public void setMessage(String text) {
        if (text == null)
            this.message = "<null>";
        this.message = text;
    }

    @Override
    public void render() {
        velText.setSize(1.5f);
        velText.setColor(Color.BLACK);
        velText.setText(message);
        velText.setPosition(x+10, y+10);
        velText.render();
        w = velText.getWidth();
        h = velText.getHeight();
    }

    @Override
    public void update() {
        if (x == -w - 1 && hidden)
            visible = false;

        if (showTimer > 0)
            showTimer--;

        x = Vec2D.lerp(x, tx, 0.08f);

        if (showTimer <= 0 && timed) {
            hide();
        }
    }

    private int showTimer;
    private boolean timed, hidden;

    public void show(int time) {
        hidden = false;
        timed = false;
        showTimer = 0;
        if (time > 0) {
            showTimer = time;
            timed = true;
        }
        //x = -w - 1;
        tx = indent;
        visible = true;
    }

    public void hide() {
        hidden = true;
        tx = -w - 30;
    }

    @Override
    public void init() {
        velText = new RenderableText("",0,0);
        show(1);
    }
}
