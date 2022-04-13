package me.Barni.tools;

import me.Barni.*;
import me.Barni.tools.Actions.FileAction;
import me.Barni.tools.Actions.GridPaintAction;
import me.Barni.window.MouseHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class EditorGUI {
    JPanel rootPanel;

    //Panels
    JPanel PropertiesPanel;
    JPanel paintPanel;
    JPanel ChecksPanel;

    JCheckBox paintCB;
    JCheckBox freeCamCB;
    JCheckBox godCB;


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
    private JLabel pathInfoLabel;
    private JLabel solidityInfoLabel;
    private JLabel matPathInfoLabel;

    ListSelectionModel sm;

    private LevelEditor editor;
    private EditorActor actor;
    private Game game;
    private Map map;


    public void setMap(Map map) {
        this.map = map;
        initUIComponents();
    }

    public void update() {

        //If painting & focused & LMB or RMB pressed -> add GridPaintAction to actor
        if (editor.isPainting())
            if ((MouseHandler.isPressed(MouseHandler.LMB) ||
                    MouseHandler.isPressed(MouseHandler.RMB)) &&
                    me.Barni.window.Window.isFocused()) {

                actor.addAction(
                        new GridPaintAction(
                                game,
                                MouseHandler.getPosition(),
                                MouseHandler.isPressed(MouseHandler.LMB) ? (int) indexSpinner.getValue() : 0, //Erease if RMB
                                MouseHandler.isPressed(MouseHandler.LMB) ? (int) typeSpinner.getValue() : 0
                        )
                );
            }

        actor.update();
    }


    public EditorGUI(LevelEditor editor, Game game) {
        this.game = game;
        this.editor = editor;
        this.actor = new EditorActor(game);
    }

    public void updateMaterialPreview() {

        int id = (int) indexSpinner.getValue();
        int type = (int) typeSpinner.getValue();

        //Correct values
        if (id < 0)
            id = 0;
        if (id > Material.getMatCount() - 1)
            id = Material.getMatCount() - 1;
        if (type < 0)
            type = 0;
        if (type > Material.getMaxTypes())
            type = Material.getMaxTypes();
        indexSpinner.setValue(id);
        typeSpinner.setValue(type);

        //id == 0 means 'void'
        if (id == 0) {
            txtPreview.setIcon(null);
            matPathInfoLabel.setText(" ");
            solidityInfoLabel.setText(" ");
            return;
        }


        BufferedImage img;
        //Try to change image
        try {
            img = game.getMap().atlas.getImage(id - 1, type);
        } catch (Exception e) {
            System.out.println("invalid index" + e.getMessage());
            txtPreview.setIcon(null);
            return;
        }

        BufferedImage resImg = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        resImg.getGraphics().drawImage(img, 0, 0, 64, 64, null);
        ImageIcon icon = new ImageIcon(resImg);

        txtPreview.setIcon(icon);


        matPathInfoLabel.setText("Path: " + Material.getPath(id, type));
        solidityInfoLabel.setText("Solidity: " + Material.isSolid(id, type));
    }

    public void validatePath() {
        File f = new File(pathField.getText());
        if (f.exists()) {

            String result = MapLoader.isValidMapFile(pathField.getText(), game);
            if (result.equals("")) {
                pathField.setForeground(Color.BLACK);
                pathInfoLabel.setText("Valid file");
            } else {
                pathField.setForeground(Color.RED);
                pathInfoLabel.setText("Error:" + result);
            }
        } else {
            pathField.setForeground(Color.ORANGE);
            pathInfoLabel.setText("Not existing file");
        }

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

        // Listen for changes in the pathField text
        pathField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validatePath();
            }

            public void removeUpdate(DocumentEvent e) {
                validatePath();
            }

            public void insertUpdate(DocumentEvent e) {
                validatePath();
            }
        });

        indexSpinner.addChangeListener(e -> updateMaterialPreview());
        typeSpinner.addChangeListener(e -> updateMaterialPreview());

        //Initial texture update
        updateMaterialPreview();


        //Restrict spinner values to numbers
        JFormattedTextField txt = ((JSpinner.NumberEditor) indexSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        txt = ((JSpinner.NumberEditor) typeSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);


        updateCheckBoxStates();
        //Checkbox listeners
        paintCB.addActionListener(e -> checkBoxHandle(CB_PAINT, paintCB.isSelected()));
        freeCamCB.addActionListener(e -> checkBoxHandle(CB_FREECAM, freeCamCB.isSelected()));
        godCB.addActionListener(e -> checkBoxHandle(CB_GODMODE, godCB.isSelected()));
    }

    public static final int CB_FREECAM = 0;
    public static final int CB_GODMODE = 1;
    public static final int CB_PAINT = 2;

    private void updateCheckBoxStates() {
        freeCamCB.setSelected(editor.isFreeCam());
        godCB.setSelected(map.getPlayer().godMode);
        paintCB.setSelected(editor.isPainting());
    }

    //Handle checkbox updates
    private void checkBoxHandle(int id, boolean state) {
        switch (id) {
            case CB_FREECAM:
                editor.setCamPos(map.getPlayer().position);
                editor.setFreeCam(state);
                break;
            case CB_GODMODE:
                map.getPlayer().godMode = state;
                break;
            case CB_PAINT:
                editor.setPainting(state);
        }
    }

    //Called by IDEA form
    private void createUIComponents() {
        propertiesTable = new JTable(new String[][]{}, new String[]{"Key", "Value"}) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
    }

    public void refresh() {
        updateMaterialPreview();
        validatePath();
        updateCheckBoxStates();
    }

    public void undo() {
        actor.undoLastAction();
    }
}