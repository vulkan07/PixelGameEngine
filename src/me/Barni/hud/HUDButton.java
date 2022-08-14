package me.Barni.hud;

import me.Barni.Game;
import me.Barni.graphics.RenderableText;
import me.Barni.physics.Vec2D;
import me.Barni.window.MouseHandler;
import me.Barni.hud.events.ButtonEventListener;
import org.joml.Vector4f;

import java.awt.*;

public class HUDButton extends HUDElement {

    private boolean hovered;
    private boolean pressed, pressing;
    public boolean enabled;
    public Color hoveredColor, pressedColor, defaultColor, disabledColor;
    private Vector4f currentColor, targColor;
    public String text;
    private RenderableText textRenderTarget;


    private ButtonEventListener myListener;

    public void setListener(ButtonEventListener listener) {
        this.myListener = listener;
    }

    public HUDButton(Game g, String name, int x, int y, int width, int height, String text) {
        super(g, name, x, y, width, height);
        hoveredColor = new Color(255, 255, 255);
        pressedColor = new Color(162, 189, 218);
        defaultColor = new Color(115, 115, 115);
        disabledColor = new Color(49, 49, 49);
        currentColor = colorToVec4(defaultColor);
        targColor = colorToVec4(defaultColor);

        this.enabled = true;
        this.childs = null;
        this.text = text;
    }

    private Vector4f colorToVec4(Color c) {
        return new Vector4f(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }


    @Override
    public void add(HUDElement elem) {
        game.getLogger().err("Can't add child to a HUDButton!");
    }

    @Override
    public void update() {

        currentColor.lerp(targColor, .135f);

        boolean lastPressed = pressed;
        this.hovered = false;
        this.pressed = false;
        if (enabled) { //ENABLED
            if (
                    !game.window.isCursorHidden() &&
                    MouseHandler.getPosition().x > x &&         //LEFT
                            MouseHandler.getPosition().y > y - 24 &&         //TOP
                            MouseHandler.getPosition().x < x + w &&     //RIGHT
                            MouseHandler.getPosition().y < y + h -8       //BOTTOM
            ) { //HOVERED

                this.hovered = true;

                //PRESSED
                if (MouseHandler.isPressed(MouseHandler.LMB)) {
                    pressed = true;
                    if (!lastPressed) {
                        currentColor = colorToVec4(pressedColor);
                        targColor = colorToVec4(pressedColor);

                        if (myListener != null) {
                            myListener.onPressed();
                        }
                    }
                } else {
                    targColor = colorToVec4(hoveredColor);
                }
            } else { //NOT HOVERED
                targColor = colorToVec4(defaultColor);
            }
        } else { //DISABLED
            targColor = colorToVec4(disabledColor);
        }

        if (!pressed && lastPressed) {
            currentColor = colorToVec4(hoveredColor);
            if (myListener != null)
                myListener.onReleased();
        }
    }

    @Override
    public void render() {
        if (!visible) return;
        textRenderTarget.setColor(currentColor);
        textRenderTarget.render();
    }

    @Override
    public void init() {
        textRenderTarget = new RenderableText(text, x, y);
        textRenderTarget.setSize(1.5f);
    }
}
