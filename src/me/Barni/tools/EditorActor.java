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
        cam = map.cam;
        reload();
    }

    public void reload() {

    }

    public void executeLastAction(boolean force) {
        if (actions.size() == 0)
            return;

        EditorAction a = actions.get(actions.size()-1);
        if (a == null)
            return;

        if (!force && a.isExecuted())
            return;

        a.execute();
    }
    public void addAction(EditorAction a) {
        actions.add(a);
    }

    public void update() {
        executeLastAction(false);
    }

}
