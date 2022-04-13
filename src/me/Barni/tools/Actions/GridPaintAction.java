package me.Barni.tools.Actions;

import me.Barni.Game;
import me.Barni.Tile;
import me.Barni.physics.Vec2D;

public class GridPaintAction extends EditorAction {

    private Vec2D mousePos;
    private int tID, tType, index;
    private Tile prevTile;

    public GridPaintAction(Game g, Vec2D mousePos, int tileID, int tileType) {
        super(g);
        this.mousePos = mousePos;
        this.tID = tileID;
        this.tType = tileType;
    }

    @Override
    public void execute() {
        executed = true;
        success = true;

        int w = game.getMap().getWidth();
        float zoom = game.getMap().getCamera().getZoom();
        float scrollX = game.getMap().getCamera().getScrollX();
        float scrollY = game.getMap().getCamera().getScrollY();

        //shift y value if window frame is visible
        int yShift = game.window.isFullScreen() ? 0 : 64;

        int x = (int)(((mousePos.x + scrollX) * zoom - game.getWIDTH() /2         ) / 32);
        int y = (int)(((mousePos.y + scrollY) * zoom - game.getHEIGHT()/2 + yShift) / 32);

        index = y * w + x;
        if (index < 0 || index > map.getTilesLength())
            return;
        prevTile = new Tile(map.getTile(index).id, map.getTile(index).type);
        map.setTileData(index, tID, tType);
    }

    @Override
    public void undo() {
        super.undo();
        map.setTile(index, prevTile);
    }
}
