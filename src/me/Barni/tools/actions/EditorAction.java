package me.Barni.tools.actions;

import me.Barni.Game;
import me.Barni.Map;
import me.Barni.tools.EditorActor;

public abstract class EditorAction {
    protected Game game;
    protected Map map;
    protected EditorActor actor;

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

    public EditorAction(Game g, EditorActor actor) {
        this.game = g;
        this.map = g.getMap();
        this.actor = actor;
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
