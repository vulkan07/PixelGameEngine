package me.Barni.hud;

import me.Barni.Game;
import me.Barni.graphics.GraphicsUtils;
import me.Barni.graphics.QuadBatch;
import me.Barni.graphics.RenderableText;
import me.Barni.physics.Vec2D;
import me.Barni.window.MouseHandler;
import me.Barni.hud.events.ButtonEventListener;
import org.joml.Vector4f;

import java.awt.*;

public class HUDButton extends HUDElement {

    private boolean hovered;
    private boolean pressed;
    public boolean enabled;
    public Color hoveredColor, pressedColor, defaultColor, disabledColor;
    private Vector4f currentColor, targColor;
    private Vec2D imgSize, imgPos, textPos;
    public String text;
    private float defTextSize, targTextSize, textSize; //Text variables for text size bounce on click
    private RenderableText textRenderTarget;
    private QuadBatch image;

    private ButtonEventListener myListener;

    public void setListener(ButtonEventListener listener) {
        this.myListener = listener;
    }

    public void setImage(String name) {
        image.loadTexture(name);
        GraphicsUtils.nonaSlice(image, x,y,w,h);
    }

    public HUDButton(Game g, String name, int x, int y, int width, int height, String text) {
        super(g, name, x, y, width, height);
        hoveredColor = new Color(255, 255, 255);
        pressedColor = new Color(162, 189, 218);
        defaultColor = new Color(182, 182, 182);
        disabledColor = new Color(49, 49, 49);
        currentColor = colorToVec4(defaultColor);
        targColor = colorToVec4(defaultColor);
        imgPos = new Vec2D(x,y);
        imgSize = new Vec2D(w,h);
        textPos = new Vec2D(x,y);


        this.enabled = true;
        this.childs = null;
        this.text = text;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        imgPos.x = x;
        imgPos.y = y;
        imgSize.x = w;
        imgSize.y = h;
        textPos.x = x + w/2 - textRenderTarget.getWidth()/2;
        textPos.y = y + h/2 - textRenderTarget.getHeight()/2-3;
        textRenderTarget.setPosition(textPos.x, textPos.y);
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
        textSize = Vec2D.lerp(textSize, targTextSize, .16f);

        boolean lastPressed = pressed;
        this.hovered = false;
        this.pressed = false;
        if (enabled) { //ENABLED
            targTextSize = defTextSize;
            if (
                    !game.window.isCursorHidden() &&
                    MouseHandler.getPosition().x > x &&         //LEFT
                            MouseHandler.getPosition().y > y - 24 &&         //TOP
                            MouseHandler.getPosition().x < x + w &&     //RIGHT
                            MouseHandler.getPosition().y < y + h - 22      //BOTTOM
            ) { //HOVERED

                this.hovered = true;

                //PRESSED
                if (MouseHandler.isPressed(MouseHandler.LMB)) {
                    targTextSize = defTextSize * .95f;
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
            targTextSize = defTextSize * .9f;
            targColor = colorToVec4(disabledColor);
        }

        if (!pressed && lastPressed) {
            currentColor = colorToVec4(hoveredColor);
            targTextSize = defTextSize;
            if (myListener != null)
                myListener.onReleased();
        }
    }

    @Override
    public void render() {
        if (!visible) return;

        renderImage();

        currentColor.w = Math.min(parentOpacity, opacity);
        textRenderTarget.setColor(currentColor);
        textRenderTarget.setSize(textSize);
        float xShift = defTextSize-textSize*12+15; //approximate hack
        float yShift = defTextSize-textSize*6+8;
        textRenderTarget.setPosition(textPos.x + xShift, textPos.y + yShift);
        textRenderTarget.render();
    }

    private void renderImage() {
        if (!image.getTexture().isValid())
            return;

        image.setTint(GraphicsUtils.remapVec4f(currentColor,0,255,0,1).sub(.1f,.1f,.1f,0f));
        image.render(null);
    }

    @Override
    public void init() {
        defTextSize = 1.5f;
        targTextSize = defTextSize;
        textSize = defTextSize;

        textRenderTarget = new RenderableText(text, x, y);
        textRenderTarget.setColor(currentColor);
        textRenderTarget.setSize(defTextSize);
        textPos.x = x + w/2 - textRenderTarget.getWidth()/2;
        textPos.y = y + h/2 - textRenderTarget.getHeight()/2-3;
        textRenderTarget.setPosition(textPos.x, textPos.y);

        image = new QuadBatch(new float[]{}, new float[]{});
        GraphicsUtils.nonaSlice(image, x,y,w,h);
    }

}
