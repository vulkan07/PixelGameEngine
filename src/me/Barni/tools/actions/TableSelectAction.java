package me.Barni.tools.actions;

import me.Barni.Game;
import me.Barni.tools.EditorActor;

import javax.swing.*;

public class TableSelectAction extends EditorAction {

    public static final int TYPE_DECORATIVE = 0;
    public static final int TYPE_ENTITY = 1;

    JTable table;
    private int type;
    private int[] indices, prevIndices;

    public TableSelectAction(Game g, EditorActor actor, JTable table, int selectType) {
        super(g, actor);
        this.table = table;
        this.type = selectType;
    }

    @Override
    public void execute() {
        super.execute();

        //Get selected id's from table
        indices = table.getSelectedRows();

        //Save previous selected
        prevIndices = actor.getSelectedDecoratives();

        //Set actor's array
        actor.setSelectedDecs(indices);
        actor.getGUI().updateDecPropertyTable();
    }

    @Override
    public void undo() {
        super.undo();
        System.err.println("NO UNDO ON SELECT ACTION DEFINED (yet)");
    }
}
