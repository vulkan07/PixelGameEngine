package me.Barni.tools.actions;

import me.Barni.Game;

import javax.swing.*;

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
            game.loadNewMap(path);
        if (type == TYPE_SAVE) {
            //Comfirm dialog
            int n = JOptionPane.showConfirmDialog(null, "Do you want to overwrite file?", "Save", JOptionPane.OK_CANCEL_OPTION);
            if (n == 0)
                game.getMap().dumpCurrentMapIntoFile(path);
        }
        game.getLevelEditor().refresh();
    }
}
