package me.Barni.tools;

import me.Barni.*;
import me.Barni.tools.Actions.EditorAction;
import me.Barni.tools.Actions.FileAction;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class EditorGUI {
    JPanel rootPanel;

    //Panels
    JPanel PropertiesPanel;
    JPanel paintPanel;
    JPanel ChecksPanel;

    //Checkboxes
    JCheckBox grid;
    JCheckBox paint;
    JCheckBox freeCam;
    JCheckBox god;


    //Buttons
    JButton loadButton;
    JButton saveButton;

    JButton applyButton;
    JButton cancelButton;

    //Tables
    JTable propertiesTable;
    JTable mapTable;

    //Textfields
    JTextField pathField;

    //Labels
    JLabel pathResultLabel;
    JLabel objDataLabel;
    JLabel txtPreview; //Contains texture

    //Spinners
    JSpinner indexSpinner;
    JSpinner typeSpinner;

    ListSelectionModel sm;

    private LevelEditor editor;
    private EditorActor actor;
    private static Game game;
    private Map map;


    public static void init(Game g) {
        game = g;
    }

    public void setMap(Map map) {
        this.map = map;
        initUIComponents();
    }

    public void update() {
        actor.update();
    }


    public EditorGUI(LevelEditor editor) {
        this.editor = editor;
        this.actor = new EditorActor(game);
    }

    public void updateTxtPreviewImage() {
        BufferedImage img = game.getMap().atlas.getImage((int) indexSpinner.getValue(), (int) typeSpinner.getValue());
        BufferedImage resImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        resImg.getGraphics().drawImage(img, 0, 0, 64, 64, null);
        ImageIcon icon = new ImageIcon(resImg);

        txtPreview.setIcon(icon);
    }


    private void initUIComponents() {
        pathField.setText(game.MAP_DIR + game.getMap().getFileName());

        loadButton.addActionListener(e -> actor.addAction(
                new FileAction(
                        game,
                        FileAction.TYPE_LOAD,
                        pathField.getText())));
        saveButton.addActionListener(e -> actor.addAction(
                new FileAction(
                        game,
                        FileAction.TYPE_SAVE,
                        pathField.getText())));
    }

    //Called by IDEA form
    private void createUIComponents() {
        propertiesTable = new JTable(new String[][]{}, new String[]{"Key", "Value"}) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
    }
}