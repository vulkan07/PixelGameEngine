package me.Barni.tools;

import me.Barni.Decorative;
import me.Barni.Material;
import me.Barni.physics.Vec2D;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class EditorGUI {
    JPanel rootPanel;

    JPanel PropertiesPanel;
    JPanel paintPanel;
    JPanel ChecksPanel;

    JCheckBox grid;
    JCheckBox paint;
    JCheckBox outlEnt;
    JCheckBox outlDec;
    JCheckBox freeCam;
    JCheckBox god;
    JCheckBox appleCheckBox;

    JComboBox textureSelectBox;
    JComboBox selectionBox;

    JButton reloadButton;
    JButton overwriteButton;
    JButton applyButton;
    JButton cancelButton;

    JTable propertiesTable;
    JTable selectTable;

    JTextField pathField;

    JLabel pathResultLabel;
    JLabel txtPreview;
    JLabel objDataLabel;
    JButton addButton;
    JButton deleteButton;
    private JButton duplicateButton;
    private JTextField addTypeField;

    ListSelectionModel sm;

    LevelEditor editor;

    public EditorGUI(LevelEditor editor) {

        this.editor = editor;

        initUIComponents();

        //
        // ADD LISTENERS
        //
        {
            addButton.addActionListener(e -> {
                trySelectAddButtonAction(0);
            });
            deleteButton.addActionListener(e -> {
                trySelectDeleteButtonAction(0);
            });

            grid.addActionListener(e -> {
                editor.showGrid = !editor.showGrid;
            });
            paint.addActionListener(e -> {
                if (paint.isSelected()) {
                    editor.setMouseGizmo(editor.MOUSE_GIZMO_PAINT);
                    editor.paintingGrid = true;
                } else {
                    editor.setMouseGizmo(editor.MOUSE_GIZMO_SELECT);
                    editor.paintingGrid = false;
                }
            });
            textureSelectBox.addActionListener(e -> {
                editor.paintTileIndex = textureSelectBox.getSelectedIndex() + 1;
                updateTxtPreviewImage();
            });
            outlEnt.addActionListener(e -> {
                editor.outlineEnts = outlEnt.isSelected();
            });
            outlDec.addActionListener(e -> {
                editor.outlineDecs = outlDec.isSelected();
            });
            god.addActionListener(e -> {
                editor.game.getPlayer().godMode = god.isSelected();
            });
            freeCam.addActionListener(e -> {
                if (freeCam.isSelected()) {
                    editor.freeCam = true;
                    editor.pos = editor.game.getPlayer().position.copy();
                    editor.cam.lerp = .07f;
                    editor.game.getPlayer().locked = true;
                } else {
                    editor.freeCam = false;
                    editor.cam.followEntity = editor.game.getPlayer();
                    editor.cam.lerp = editor.cam.DEFAULT_LERP;
                    editor.game.getPlayer().locked = false;
                }
            });
            reloadButton.addActionListener(e -> {
                tryLoadMap();
            });
            overwriteButton.addActionListener(e -> {
                saveMap();
            });
            selectionBox.addActionListener(e -> {
                if (selectionBox.getSelectedIndex() == 0) { //Decoratives
                    reloadTablesOnFilterChange(0);
                    outlDec.setSelected(true);
                    outlEnt.setSelected(false);
                    editor.outlineEnts = false;
                    editor.outlineDecs = true;
                    editor.setSelectionType(0);
                } else {                        //Entities
                    reloadTablesOnFilterChange(1);
                    outlDec.setSelected(false);
                    outlEnt.setSelected(true);
                    editor.outlineEnts = true;
                    editor.outlineDecs = false;
                    editor.setSelectionType(1);
                }
            });
            sm = selectTable.getSelectionModel();
            sm.addListSelectionListener(e -> {
                //sm.addSelectionInterval(0,4);
                //sm.setLeadSelectionIndex(8);

                String[][] decTableData = gatherDecorativeProperties();
                propertiesTable.setModel(new DefaultTableModel(decTableData, new String[]{"Key", "Value"}));

                if (selectionBox.getSelectedIndex() == 0) {
                    //Decoratives
                    editor.selectedDecorativesID = selectTable.getSelectedRows();
                    if (selectTable.getSelectedRows().length < 1)
                        deleteButton.setForeground(new Color(202, 202, 200));
                    else
                        deleteButton.setForeground(new Color(65, 62, 60));
                }
            });
            cancelButton.addActionListener(e -> {
                String[][] decTableData = gatherDecorativeProperties();
                propertiesTable.setModel(new DefaultTableModel(decTableData, new String[]{"Key", "Value"}));
            });
            applyButton.addActionListener(e -> setDecorativeProperties());
        }
    }

    private boolean waitingForMousePress;

    public void trySelectDeleteButtonAction(int fType) {
        switch (fType) {
            case 0: //Decorative
                for (int i : editor.selectedDecorativesID)
                    editor.map.removeDecorative(i);
                reloadTablesOnFilterChange(0);
                break;
            case 1:
                System.out.println("not programmed yet");
                break;
        }
    }

    public void trySelectAddButtonAction(int fType) {
        switch (fType) {
            case 0: //Decorative
                if (!waitingForMousePress) {
                    editor.obtainNewMousePress();
                    waitingForMousePress = true;
                }
                if (editor.getMouseClickLocation() != null) {
                    waitingForMousePress = false;
                    Vec2D pos = editor.getMouseClickLocation().copy();
                    Decorative d = new Decorative(editor.game,
                            pos.xi() - 16,
                            pos.yi() - 16,
                            1, 1, 32, 32,
                            addTypeField.getText());
                    editor.map.addDecorative(d);
                    reloadTablesOnFilterChange(0);
                }
                break;
            case 1:
                System.out.println("not programmed yet");
                break;

        }
    }

    public void setDecorativeProperties() {

        if (editor.selectedDecorativesID.length == 0)
            return;

        Decorative[] decs = new Decorative[editor.selectedDecorativesID.length];
        int i = 0;
        for (int index : editor.selectedDecorativesID) {
            decs[i] = editor.map.decoratives[index];
            i++;
        }

        //Data variables
        String path;
        int x, y, w, h, z;
        float p;

        TableModel t = propertiesTable.getModel();
        path = t.getValueAt(0, 1).toString();
        try {
            x = Integer.parseInt(t.getValueAt(1, 1).toString());
            y = Integer.parseInt(t.getValueAt(2, 1).toString());
            w = Integer.parseInt(t.getValueAt(3, 1).toString());
            h = Integer.parseInt(t.getValueAt(4, 1).toString());
            z = Integer.parseInt(t.getValueAt(5, 1).toString());
            p = Float.parseFloat(t.getValueAt(6, 1).toString());
        } catch (NumberFormatException e) {
            editor.game.getLogger().err("[EDITOR] Invalid values in property table! Can't parse!");
            applyButton.setForeground(Color.RED);
            return;
        }
        applyButton.setForeground(Color.BLACK);
        for (Decorative d : decs) {
            d.texture.loadTexture(editor.game, path, w, h, true);
            d.x = x;
            d.y = y;
            d.w = w;
            d.h = h;
            d.z = z;
            d.parallax = p;
        }
    }

    public String[][] gatherDecorativeProperties() {
        // Material, x, y, w, h, layer, parallax
        String[][] data = new String[7][2];

        //Only collect data if one decorative is selected at the time
        if (editor.selectedDecorativesID.length != 1)
            return data;

        //Get decorative from map array by first select id
        Decorative d = editor.map.decoratives[editor.selectedDecorativesID[0]];
        if (d == null)
            return data;

        data[0][0] = "Material";
        data[1][0] = "X";
        data[2][0] = "Y";
        data[3][0] = "W";
        data[4][0] = "H";
        data[5][0] = "Z-layer";
        data[6][0] = "Parallax";


        data[0][1] = d.texture.getPath();
        data[1][1] = String.valueOf(d.x);
        data[2][1] = String.valueOf(d.y);
        data[3][1] = String.valueOf(d.w);
        data[4][1] = String.valueOf(d.h);
        data[5][1] = String.valueOf(d.z);
        data[6][1] = String.valueOf(d.parallax);

        return data;
    }

    public void tryLoadMap() {
        File f = new File(pathField.getText());
        if (!f.exists()) {
            pathResultLabel.setText("Invalid path!");
            return;
        }
        pathResultLabel.setText("");
        editor.game.screenFadeIn(180);
        editor.game.loadNewMap(pathField.getText());
    }

    public void saveMap() {
        File f = new File(pathField.getText());
        if (f.exists()) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite?", "Save map", JOptionPane.YES_NO_OPTION);
            if (confirm != 0)
                return;
        }
        String[] fPaths = pathField.getText().split("\\\\");
        editor.game.getMap().dumpCurrentMapIntoFile(fPaths[fPaths.length - 1].split("\\.")[0]);
        editor.game.getLogger().info("[EDITOR] Saving map to: " + pathField.getText());
    }

    public void updateTxtPreviewImage() {
        BufferedImage img = editor.game.getMap().atlas.getTexture(textureSelectBox.getSelectedIndex());
        BufferedImage resImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        resImg.getGraphics().drawImage(img, 0, 0, 64, 64, null);
        ImageIcon icon = new ImageIcon(resImg);

        txtPreview.setIcon(icon);
    }

    private void reloadTablesOnFilterChange(int fType) {
        // 0 = decoratives
        // 1 = entities
        switch (fType) {
            case 1:
                String[][] entData = new String[editor.game.getMap().entities.length][3];
                for (int i = 0; i < entData.length; i++) {
                    if (editor.game.getMap().entities[i] == null) {
                        entData[i][1] = "";
                        entData[i][2] = "";
                    } else {
                        String[] className = editor.game.getMap().entities[i].getClass().toString().split("\\.");
                        entData[i][2] = editor.game.getMap().entities[i].name;
                        entData[i][1] = className[className.length - 1];
                    }
                    entData[i][0] = String.valueOf(i);
                }
                selectTable.setModel(new DefaultTableModel(entData, new String[]{"ID", "Class", "Name"}));
                selectTable.getColumn("ID").setMaxWidth(24);
                break;

            case 0:
                String[][] decData = new String[editor.game.getMap().decoratives.length][2];
                for (int i = 0; i < decData.length; i++) {
                    if (editor.game.getMap().decoratives[i] == null)
                        decData[i][1] = "";
                    else
                        decData[i][1] = editor.game.getMap().decoratives[i].texture.getPath();
                    decData[i][0] = String.valueOf(i);
                }
                selectTable.setModel(new DefaultTableModel(decData, new String[]{"ID", "Material"}));
                selectTable.getColumn("ID").setMaxWidth(24);
                break;

            default:
                selectTable = new JTable();
        }
    }

    // [Manual]
    // Prepare GUI Components
    //
    private void initUIComponents() {
        pathField.setText("C:\\dev\\01.map");

        //Add txt names to texture select combo box
        for (String s : Material.materialPath) {
            if (s != null)
                textureSelectBox.addItem(s);
        }
    }

    // [UI editor dispatch]
    // Prepare GUI Components
    //
    private void createUIComponents() {
        selectTable = new JTable(new String[][]{}, new String[]{"Select above to refresh"}) {
            public boolean isCellEditable(int row, int column) {
                return false; //column == 2;
            }
        };
        propertiesTable = new JTable(new String[][]{}, new String[]{"Key", "Value"}) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
    }
}