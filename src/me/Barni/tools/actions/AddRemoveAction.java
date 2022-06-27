package me.Barni.tools.actions;

import me.Barni.Decorative;
import me.Barni.Game;
import me.Barni.tools.EditorActor;

public class AddRemoveAction extends EditorAction {

    public static final int TYPE_ENT = 0;
    public static final int TYPE_DEC = 1;
    public static final int MODE_ADD = 2;
    public static final int MODE_DEL = 3;
    private int type, mode, id = -1;
    private Decorative[] removed;

    public AddRemoveAction(Game g, EditorActor actor, int type, int mode) {
        super(g, actor);
        this.type = type;
        this.mode = mode;
    }

    @Override
    public void execute() {
        super.execute();

        if (type == TYPE_DEC) {
            if (mode == MODE_ADD) {
                Decorative d = new Decorative(game, 0, 0, 8, 1, 32, 32, "dec");
                id = map.addDecorative(d);
            } else if (mode == MODE_DEL) {
                int[] ids = actor.getSelectedDecoratives();

                if (ids[0] == -1) //no selection
                    return;

                removed = new Decorative[ids.length];
                int idShiftBack = 0, prevID = ids[0];
                int removeIndex = 0;
                for (int i : ids) {
                    if (prevID < i) {
                        idShiftBack++;
                    }
                    removed[removeIndex] = map.getDecorative(i - idShiftBack);
                    map.removeDecorative(i - idShiftBack);
                    prevID = i;
                    removeIndex++;
                }
            }
        }
        actor.getGUI().refresh();
    }

    @Override
    public void undo() {
        super.undo();

        if (type == TYPE_DEC) {
            if (mode == MODE_ADD) {
                map.removeDecorative(id);
            } else if (mode == MODE_DEL) {
                for (Decorative d : removed) {
                    map.addDecorative(d);
                }
            }
        }
        actor.getGUI().refresh();
    }
}
