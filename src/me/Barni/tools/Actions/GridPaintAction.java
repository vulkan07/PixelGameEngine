package me.Barni.tools.Actions;

import me.Barni.Game;
import me.Barni.Tile;

public class GridPaintAction extends EditorAction {

    int tileIndex, tileID, prevTileID;

    public GridPaintAction(Game g, int tileIndex, int tileID) {
        super(g);
        this.tileIndex = tileIndex;
        this.tileID = tileID;
    }

    @Override
    public void execute() {
        executed = true;
        success = true;
        prevTileID = map.getTile(tileIndex);
        map.setTile(tileIndex, new Tile(tileID,0));
    }

    @Override
    public void undo() {
        executed = false;
        tileID = map.getTile(tileIndex);
        map.setTile(tileIndex, new Tile(prevTileID,0));
    }
}
