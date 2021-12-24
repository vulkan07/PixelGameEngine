package me.Barni;

import me.Barni.physics.Vec2D;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {

    public final byte LMB = 1;//(byte) 1;
    public final byte RMB = 4;//(byte) 3;
    public final byte WHEEL = 2;//(byte)  2;
    public final byte MB4 = 8;//(byte) 5;
    public final byte MB5 = 16;//(byte) 4;

    private byte pressed = 0;
    private Vec2D pos, lastPos, delta;
    JFrame window;
    Game game;

    public MouseHandler(JFrame window, Game game) {
        this.game = game;
        this.window = window;
        pos = new Vec2D();
        delta = new Vec2D();
        lastPos = new Vec2D();
    }

    public void update() {
        try {
            pos.x = window.getMousePosition().x;
            pos.y = window.getMousePosition().y;
            delta = lastPos.copy().sub(pos);
            lastPos = pos.copy();
        } catch (NullPointerException nex) {
        }
    }

    public boolean isPressed(int button) {
        return (pressed & button) != 0;
        //return (pressed & 1 << button - 1) != 0;
    }

    public Vec2D getPosition() {
        return pos;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (game.intro.isPlayingIntro())
            game.intro.skip();
        pressed = (byte) (pressed | 1 << e.getButton() - 1);
        //System.out.println(isPressed(RMB));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = (byte) (pressed & 0xFFFFF0 << e.getButton() - 1);
        //System.out.println(isPressed(RMB));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
