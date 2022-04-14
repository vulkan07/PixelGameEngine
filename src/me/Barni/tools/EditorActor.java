package me.Barni.tools;

import me.Barni.Decorative;
import me.Barni.Game;
import me.Barni.Map;
import me.Barni.tools.actions.EditorAction;

import java.util.ArrayList;

public class EditorActor {
    private Game g;
    private Map map;
    private ArrayList<EditorAction> actions = new ArrayList<>();
    private int[] selectedDecIndices = null;

    public EditorActor(Game g) {
        this.g = g;
    }

    public void setMap(Map m) {
        map = m;
        reload();
    }

    public void reload() {

    }
    public void undoLastAction() {
        if (actions.size() < 2)
            return;

        int index = actions.size();
        //Find the latest not undone element
        do {
            index--;
        } while (index > 0 && actions.get(index).isUndone());
        EditorAction a = actions.get(index);

        if (a == null)
            return;

        a.undo();
    }

    public EditorAction getLastAction() {
        if (actions.size() <= 1)
            return null;
        return actions.get(actions.size()-1);
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


    public int[] getSelectedDecoratives() {
        return selectedDecIndices;
    }

    public void setSelectedDecs(int[] selectedDecIndices) {
        this.selectedDecIndices = selectedDecIndices;

        //Reset
        for (int i = 0; i < map.getDecorativeCount(); i++) {
            map.getDecorative(i).selected = false;
        }
        //Set selected
        for (int i : selectedDecIndices) {
            map.getDecorative(i).selected = true;
        }
    }
}
