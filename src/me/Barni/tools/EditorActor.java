package me.Barni.tools;

import me.Barni.Camera;
import me.Barni.Game;
import me.Barni.Map;
import me.Barni.tools.Actions.EditorAction;

import java.util.ArrayList;

public class EditorActor {
    private Game g;
    private Map map;
    private Camera cam;
    private ArrayList<EditorAction> actions = new ArrayList<>();

    public EditorActor(Game g) {
        this.g = g;
    }

    public void setMap(Map m) {
        map = m;
        cam = map.getCamera();
        reload();
    }

    public void reload() {

    }
    public void undoLastAction() {
        if (actions.size() < 2)
            return;

        int index = actions.size()-1;
        int actual;

        //Find the latest not undone element
        do {
            actual = index;
            index--;
        } while (index >= 0 && actions.get(index).isUndone());

        EditorAction a = actions.get(index);

        if (a == null)
            return;

        a.undo();
    }

    public void executeLastAction(boolean force) {
        if (actions.size() == 0)
            return;

        EditorAction a = actions.get(actions.size()-1);
        if (a == null)
            return;

        if (!force && (a.isExecuted() || a.isUndone()))
            return;

        a.execute();
    }
    public void addAction(EditorAction a) {

        //Remove all undone actions
        int index = actions.size()-1;
        while (index >= 0 && actions.get(index).isUndone()) {
            actions.remove(index);
            index--;
        }

        actions.add(a);
    }

    public void update() {
        executeLastAction(false);
    }

}
