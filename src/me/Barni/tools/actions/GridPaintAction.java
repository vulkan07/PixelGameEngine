package me.Barni.tools.actions;

import me.Barni.Game;
import me.Barni.Tile;
import me.Barni.physics.Vec2D;

public class GridPaintAction extends EditorAction {

    private final Vec2D mousePos;
    private final int tID;
    private final int tType;
    private final boolean backTile;
    private int index = -1;
    private Tile prevTile;

    public GridPaintAction(Game g, Vec2D mousePos, int tileID, int tileType, boolean backTile) {
        super(g);
        this.mousePos = mousePos;
        this.tID = tileID;
        this.tType = tileType;
        this.backTile = backTile;
    }

    @Override
    public void execute() {
        executed = true;
        success = true;

        index = getTileFromMousePos(game, mousePos);
        if (index < 0 || index > map.getTilesLength())
            return;

        //TODO validate if index is out of bounds
        if (backTile)
            prevTile = new Tile(map.getBackTile(index).id, map.getBackTile(index).type);
        else
            prevTile = new Tile(map.getTile(index).id, map.getTile(index).type);

        if (backTile)
            map.setBackTileData(index, tID, tType);
        else
            map.setTileData(index, tID, tType);
    }

    @Override
    public void undo() {
        super.undo();
        if (backTile)
            map.setBackTile(index, prevTile);
        else
            map.setTile(index, prevTile);
    }

    public static int getTileFromMousePos(Game game, Vec2D pos) {
        //TODO validate if index is out of bounds (in coordinates)
        int w = game.getMap().getWidth();
        float zoom = game.getMap().getCamera().getZoom();
        float scrollX = game.getMap().getCamera().getScrollX();
        float scrollY = game.getMap().getCamera().getScrollY();

        //shift y value if window frame is visible
        int yShift = game.window.isFullScreen() ? 0 : 64;

        int x = (int) (((pos.x + scrollX) * zoom - game.getWIDTH() / 2) / 32);
        int y = (int) (((pos.y + scrollY) * zoom - game.getHEIGHT() / 2 + yShift) / 32);

        return y * w + x;
    }

    public int getTileIndex() {
        if (index == -1)
            index = getTileFromMousePos(game, mousePos);
        return index;
    }
}
