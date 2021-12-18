package me.Barni.tools;

import me.Barni.Decorative;
import me.Barni.Material;
import me.Barni.physics.Vec2D;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
                editor.game.player.godMode = god.isSelected();
            });
            freeCam.addActionListener(e -> {
                if (freeCam.isSelected()) {
                    editor.freeCam = true;
                    editor.pos = editor.game.player.position.copy();
                    editor.cam.lerp = .07f;
                    editor.game.player.locked = true;
                } else {
                    editor.freeCam = false;
                    editor.cam.followEntity = editor.game.player;
                    editor.cam.lerp = editor.cam.DEFAULT_LERP;
                    editor.game.player.locked = false;
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
                    loadSelectTableOnFilterChange(0);
                    outlDec.setSelected(true);
                    outlEnt.setSelected(false);
                    editor.outlineEnts = false;
                    editor.outlineDecs = true;
                    editor.setSelectionType(0);
                } else {                        //Entities
                    loadSelectTableOnFilterChange(1);
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
                if (selectionBox.getSelectedIndex() == 0) {
                    //Decoratives
                    editor.selectedDecorativesID = selectTable.getSelectedRows();
                    if (selectTable.getSelectedRows().length < 1)
                        deleteButton.setForeground(new Color(202,202,200));
                    else
                        deleteButton.setForeground(new Color(65,62,60));
                }
            });
        }
    }

    private boolean waitingForMousePress;

    public void trySelectDeleteButtonAction(int fType) {
        switch (fType) {
            case 0: //Decorative
                for (int i : editor.selectedDecorativesID)
                    editor.map.removeDecorative(i);
                loadSelectTableOnFilterChange(0);
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
                    loadSelectTableOnFilterChange(0);
                }
                break;
            case 1:
                System.out.println("not programmed yet");
                break;

        }
    }

    public void tryLoadMap() {
        File f = new File(pathField.getText());
        if (!f.exists()) {
            pathResultLabel.setText("Invalid path!");
            return;
        }
        pathResultLabel.setText("");
        editor.game.blankAlpha = 180;
        editor.game.screenFadingIn = true;
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
        editor.game.map.dumpCurrentMapIntoFile(fPaths[fPaths.length - 1].split("\\.")[0]);
        editor.game.logger.info("[EDITOR] Saving map to: " + pathField.getText());
    }

    public void updateTxtPreviewImage() {
        BufferedImage img = editor.game.map.atlas.getTexture(textureSelectBox.getSelectedIndex());
        BufferedImage resImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        resImg.getGraphics().drawImage(img, 0, 0, 64, 64, null);
        ImageIcon icon = new ImageIcon(resImg);

        txtPreview.setIcon(icon);
    }

    private void loadSelectTableOnFilterChange(int fType) {
        // 0 = decoratives
        // 1 = entities
        switch (fType) {
            case 1:
                String[][] entData = new String[editor.game.map.entities.length][3];
                for (int i = 0; i < entData.length; i++) {
                    if (editor.game.map.entities[i] == null) {
                        entData[i][1] = "";
                        entData[i][2] = "";
                    } else {
                        String[] className = editor.game.map.entities[i].getClass().toString().split("\\.");
                        entData[i][2] = editor.game.map.entities[i].name;
                        entData[i][1] = className[className.length - 1];
                    }
                    entData[i][0] = String.valueOf(i);
                }
                selectTable.setModel(new DefaultTableModel(entData, new String[]{"ID", "Class", "Name"}));
                selectTable.getColumn("ID").setMaxWidth(24);
                break;

            case 0:
                String[][] decData = new String[editor.game.map.decoratives.length][2];
                for (int i = 0; i < decData.length; i++) {
                    if (editor.game.map.decoratives[i] == null)
                        decData[i][1] = "";
                    else
                        decData[i][1] = editor.game.map.decoratives[i].texture.getPath();
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