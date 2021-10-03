package me.Barni.superhexagon;

public class PolygonLine {
    float y;
    int deadLine;
    boolean disabled;

    public PolygonLine(float y, int deadLine)
    {
        this.y = y;
        this.deadLine = deadLine;
    }

    public void tick(float step)
    {
        this.y += step;
        if (y > deadLine)
            y = deadLine;
    }

    public void disable()
    {
        disabled = true;
    }

    public void enable(int y)
    {
        this.y = y;
        disabled = false;
    }
}
