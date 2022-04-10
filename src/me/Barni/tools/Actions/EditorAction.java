package me.Barni.tools.Actions;

import me.Barni.Game;
import me.Barni.Map;

public abstract class EditorAction {
    Game g;
    Map map;


    boolean success, executed;

    public boolean isSuccess() {
        return success;
    }

    public boolean isExecuted() {
        return executed;
    }

    public EditorAction(Game g) {
        this.g = g;
        this.map = g.getMap();
    }

    public void execute() {
        executed = true;
        success = true;
    }
    public void undo() {
        executed = false;
    }
}
