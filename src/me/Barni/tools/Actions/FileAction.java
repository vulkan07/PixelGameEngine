package me.Barni.tools.Actions;

import me.Barni.Game;

public class FileAction extends EditorAction {
    public static final int TYPE_LOAD = 1;
    public static final int TYPE_SAVE = 2;

    private int type;
    private String path;

    public FileAction(Game g, int type, String path) {
        super(g);
        this.type = type;
        this.path = path;
    }

    @Override
    public void execute() {
        executed = true;
        success = true;
        if (type == TYPE_LOAD)
            g.loadNewMap(path);
        if (type == TYPE_SAVE)
            g.getMap().dumpCurrentMapIntoFile(path);
        g.getLevelEditor().refresh();
    }
}
