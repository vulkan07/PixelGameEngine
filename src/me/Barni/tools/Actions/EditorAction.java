package me.Barni.tools.Actions;

import me.Barni.Game;
import me.Barni.Map;

public abstract class EditorAction {
    Game game;
    Map map;


    protected boolean success, executed, undone;


    public boolean isUndone() {
        return undone;
    }
    public boolean isSuccess() {
        return success;
    }

    public boolean isExecuted() {
        return executed;
    }

    public EditorAction(Game g) {
        this.game = g;
        this.map = g.getMap();
    }

    public void execute() {
        executed = true;
        undone = false;
        success = true;
    }
    public void undo() {
        executed = false;
        undone = true;
    }
}
