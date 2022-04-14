package me.Barni.tools.actions;

import me.Barni.Game;
import me.Barni.tools.EditorActor;

import javax.swing.*;
import java.io.File;

public class FileAction extends EditorAction {
    public static final int TYPE_LOAD = 1;
    public static final int TYPE_SAVE = 2;
    public static final int TYPE_NEW = 3;

    private int type;
    private String path;

    public FileAction(Game g, EditorActor actor, int type, String path) {
        super(g, actor);
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

            if (path.contains("blank.map")){
                JOptionPane.showMessageDialog(null, "You cannot overwrite sample map \"blank.map\"", "Save", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Comfirm dialog
            boolean fileExists = new File(path).exists();
            int n = 0;
            if (fileExists)
                n = JOptionPane.showConfirmDialog(null, "This file already exists. Overwrite?", "Save", JOptionPane.OK_CANCEL_OPTION);

            if (n == 0)
                game.getMap().dumpCurrentMapIntoFile(path);
        }

        if (type == TYPE_NEW) {
            game.loadNewMap(game.MAP_DIR + "blank.map");
        }
        game.getLevelEditor().refresh();
    }
}
