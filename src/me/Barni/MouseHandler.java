package me.Barni;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {

    public final int LMB = 1;
    public final int RMB = 3;
    public final int WHEEL = 2;
    public final int MB4 = 5;
    public final int MB5 = 4;

    private byte pressed = 0;
    private Vec2D pos;
    JFrame window;
    Game game;

    public MouseHandler(JFrame window, Game game)
    {
        this.game = game;
        this.window = window;
        pos = new Vec2D(0,0);
    }

    public void update()
    {
        try {
            pos.x = window.getMousePosition().x;
            pos.y = window.getMousePosition().y;
        } catch (NullPointerException nex) {}
    }

    public boolean isPressed(int button)
    {
        return (pressed & 1 << button - 1) != 0;
    }

    public Vec2D getPosition() {return pos;}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = (byte)(pressed | 1 << e.getButton()-1);
        //System.out.println(isPressed(RMB));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = (byte)(pressed & 0x11111110 << e.getButton()-1);
        //System.out.println(isPressed(RMB));
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
