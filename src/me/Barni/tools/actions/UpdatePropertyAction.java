package me.Barni.tools.actions;

import me.Barni.Decorative;
import me.Barni.Game;
import me.Barni.tools.EditorActor;

import javax.swing.*;

public class UpdatePropertyAction extends EditorAction {

    public static final int TYPE_DEC = 0;
    public static final int TYPE_ENT = 1;
    private int objID;
    private int type;
    private JTable table;

    //Previous values
    private float px, py, pp;
    private int pw, ph, pz;
    private String ppath;

    public UpdatePropertyAction(Game g, EditorActor actor, JTable propTable, int type) {
        super(g, actor);
        this.type = type;
        this.table = propTable;
    }

    @Override
    public void execute() {
        super.execute();
        if (type == TYPE_DEC) {
            if (actor.getSelectedDecoratives().length < 1)
                return;
            objID = actor.getSelectedDecoratives()[0];
            Decorative d = map.getDecorative(objID);

            //Layout:
            //[x,y] 0
            //[w,h] 1
            //[z]   2
            //[p]   3
            //[mat] 4

            int row = 0;
            try {
                //Parse data
                String pos = (String) table.getValueAt(0, 1);
                row++;
                String size = (String) table.getValueAt(1, 1);
                row++;
                int z = Integer.parseInt((String) table.getValueAt(2, 1));
                row++;
                float p = Float.parseFloat((String) table.getValueAt(3, 1));
                row++;
                String mat = (String) table.getValueAt(4, 1);

                //Save previous data
                px = d.x;
                py = d.y;
                pw = d.w;
                ph = d.h;
                pz = d.z;
                pp = d.parallax;
                ppath = d.texture.getPath();

                //Set data
                d.x = Float.parseFloat(pos.split(",")[0].replace(" ", ""));
                d.y = Float.parseFloat(pos.split(",")[1].replace(" ", ""));
                d.w = Integer.parseInt(size.split(",")[0].replace(" ", ""));
                d.h = Integer.parseInt(size.split(",")[1].replace(" ", ""));
                d.z = z;
                d.parallax = p;
                d.texture.loadTexture(mat, d.w, d.h);
                d.texture.uploadImageToGPU(0);
                actor.getGUI().refresh();
            } catch (Exception e) {
                System.out.printf("Invalid table value at row %d! %s%n", row, e.getMessage());
                success = false;
            }
        }
    }

    @Override
    public void undo() {
        super.undo();
        if (type == TYPE_DEC) {
            Decorative d = map.getDecorative(objID);

            //Buffer values
            float bx, by, bp;
            int bw, bh, bz;
            String bpath;


            bx = d.x;
            by = d.y;
            bw = d.w;
            bh = d.h;
            bz = d.z;
            bp = d.parallax;
            bpath = d.texture.getPath();

            d.x = px;
            d.y = py;
            d.w = pw;
            d.h = ph;
            d.z = pz;
            d.parallax = pp;
            d.texture.loadTexture(ppath, d.w, d.h);
            d.texture.uploadImageToGPU(0);
            actor.getGUI().refresh();

            px = bx;
            py = by;
            pw = bw;
            ph = bh;
            pz = bz;
            pp = bp;
            ppath = bpath;
        }
    }
}
