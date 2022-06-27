package me.Barni.tools;

import me.Barni.*;
import me.Barni.tools.actions.*;
import me.Barni.window.KeyboardHandler;
import me.Barni.window.MouseHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
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
    JLabel txtPreview; //Contains texture

    //Spinners
    JSpinner indexSpinner;
    JSpinner typeSpinner;
    private JLabel pathInfoLabel;
    private JLabel solidityInfoLabel;
    private JLabel matPathInfoLabel;
    private JButton newButton;
    private JButton addButton;
    private JTable decSelectTable;
    private JCheckBox renderCB;
    private JButton deleteButton;

    ListSelectionModel sm;

    private LevelEditor editor;
    private EditorActor actor;
    private Game game;
    private Map map;


    public void setMap(Map map) {
        this.map = map;
        actor.setMap(map);
        initUIComponents();
        if (map.getFileName().contains("blank.map")) {
            pathField.setText(game.MAP_DIR + "untitled.map");
        }
    }

    public void update() {

        //If painting & focused & LMB or RMB pressed -> add GridPaintAction to actor
        if (editor.isPainting())
            if ((MouseHandler.isPressed(MouseHandler.LMB) ||
                    MouseHandler.isPressed(MouseHandler.RMB)) &&
                    me.Barni.window.Window.isFocused()) {

                //Create action
                GridPaintAction a = new GridPaintAction(
                        game,
                        actor,
                        MouseHandler.getPosition(),
                        MouseHandler.isPressed(MouseHandler.LMB) ? (int) indexSpinner.getValue() : 0, //Erease if RMB
                        MouseHandler.isPressed(MouseHandler.LMB) ? (int) typeSpinner.getValue() : 0,
                        KeyboardHandler.getKeyState(KeyboardHandler.SHIFT) //Background tile if SHIFT is pressed
                );
                //Only add action if the tile is different
                boolean addAction = true;
                //If Last event was grid paint
                if (actor.getLastAction() != null && actor.getLastAction() instanceof GridPaintAction) {
                    //Previous tile index
                    int prevTileIndex = ((GridPaintAction) actor.getLastAction()).getTileIndex();

                    //If not different, don't add action
                    if (a.getTileIndex() == prevTileIndex)
                        addAction = false;
                }
                if (addAction) {
                    actor.addAction(a);
                }
            }

        actor.update();
    }


    public EditorGUI(LevelEditor editor, Game game) {
        this.game = game;
        this.editor = editor;
        this.actor = new EditorActor(game, this);
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
            System.out.println("<Editor> Invalid texture index: " + e.getMessage());
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

    public void updateSelectionTables() {
        //-----------------------//
        //    DECORATIVE TABLE   //
        //-----------------------//
        //Create table model
        DefaultTableModel dtm = new DefaultTableModel(map.getDecorativeCount(), 2);
        //Set header to madel
        dtm.setColumnIdentifiers(new String[]{"ID", "Material"});
        //Set model to table
        decSelectTable.setModel(dtm);
        //Set max width for "ID" row
        decSelectTable.getColumn("ID").setMaxWidth(35);
        //Set data
        for (int i = 0; i < map.getDecorativeCount(); i++) {
            dtm.setValueAt(i, i, 0);
            dtm.setValueAt(map.getDecorative(i).texture.getPath(), i, 1);
        }

    }

    public void updateDecPropertyTable() {
        boolean invalid = false;

        //Is there more selected
        invalid = actor.getSelectedDecoratives().length < 1;

        int index = -1;
        //Get index if length is not 0
        if (actor.getSelectedDecoratives().length != 0)
            index = actor.getSelectedDecoratives()[0];

        //Declare decorative
        Decorative d = null;

        //Is the index valid (throws no exception?)
        try {
            d = map.getDecorative(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            invalid = true;
        }

        //Is the decorative null
        invalid |= (d == null);

        if (invalid) {
            //Empty table
            DefaultTableModel dtm = new DefaultTableModel(0, 2);
            dtm.setColumnIdentifiers(new String[]{"Key", "Value"});
            propertiesTable.setModel(dtm);
            propertiesTable.getColumn("Key").setMaxWidth(60);
            return;
        }


        //Keys: [x, y] [w, h] layer, parallax, path (total 7 keys, 2 pairs -> 5 rows)
        //Create table model
        DefaultTableModel dtm = new DefaultTableModel(5, 2);
        //Set header to madel
        dtm.setColumnIdentifiers(new String[]{"Key", "Value"});
        //Set model to table
        propertiesTable.setModel(dtm);
        //Set max width for "ID" row
        propertiesTable.getColumn("Key").setMaxWidth(60);


        //Set data
        String[] keys = {"Position", "Size", "Layer", "Parallax", "Material"};
        //Set key texts to first column
        for (int i = 0; i < keys.length; i++){
            dtm.setValueAt(keys[i], i, 0);
        }
        //Set data
        dtm.setValueAt(d.x + ", " + d.y, 0, 1);   //Pos
        dtm.setValueAt(d.w + ", " + d.h, 1, 1);   //Size
        dtm.setValueAt(String.valueOf(d.z), 2, 1);                     //Layer
        dtm.setValueAt(String.valueOf(d.parallax), 3, 1);              //Prx
        dtm.setValueAt(d.texture.getPath(), 4, 1);     //Mat
    }

    private void initUIComponents() {
        pathField.setText(game.MAP_DIR + game.getMap().getFileName());

        loadButton.addActionListener(e -> actor.addAction(
                new FileAction(
                        game,
                        actor,
                        FileAction.TYPE_LOAD,
                        pathField.getText())));
        saveButton.addActionListener(e -> actor.addAction(
                new FileAction(
                        game,
                        actor,
                        FileAction.TYPE_SAVE,
                        pathField.getText())));
        newButton.addActionListener(e -> actor.addAction(
                new FileAction(
                        game,
                        actor,
                        FileAction.TYPE_NEW,
                        pathField.getText())));
        applyButton.addActionListener(e -> actor.addAction(
                new UpdatePropertyAction(
                        game,
                        actor,
                        propertiesTable,
                        UpdatePropertyAction.TYPE_DEC)));
        addButton.addActionListener(e -> actor.addAction(
                new AddRemoveAction(
                        game,
                        actor,
                        AddRemoveAction.TYPE_DEC,
                        AddRemoveAction.MODE_ADD)));
        deleteButton.addActionListener(e -> actor.addAction(
                new AddRemoveAction(
                        game,
                        actor,
                        AddRemoveAction.TYPE_DEC,
                        AddRemoveAction.MODE_DEL)));
        cancelButton.addActionListener(e -> updateDecPropertyTable());

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

        indexSpinner.setValue(1);
        indexSpinner.addChangeListener(e -> updateMaterialPreview());
        typeSpinner.addChangeListener(e -> updateMaterialPreview());


        //Restrict spinner values to numbers
        JFormattedTextField txt = ((JSpinner.NumberEditor) indexSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        txt = ((JSpinner.NumberEditor) typeSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        //Checkbox listeners
        paintCB.addActionListener(e -> checkBoxHandle(CB_PAINT, paintCB.isSelected()));
        freeCamCB.addActionListener(e -> checkBoxHandle(CB_FREECAM, freeCamCB.isSelected()));
        godCB.addActionListener(e -> checkBoxHandle(CB_GODMODE, godCB.isSelected()));
        renderCB.addActionListener(e -> checkBoxHandle(CB_RENDER, renderCB.isSelected()));

        ListSelectionModel selectionModel = decSelectTable.getSelectionModel();
        selectionModel.addListSelectionListener(this::handleSelection);

        //Set all UI elements' values to correct ones
        refresh();
    }

    public static final int CB_FREECAM = 0;
    public static final int CB_GODMODE = 1;
    public static final int CB_PAINT = 2;
    public static final int CB_RENDER = 3;

    private void updateCheckBoxStates() {
        freeCamCB.setSelected(editor.isFreeCam());
        godCB.setSelected(map.getPlayer().godMode);
        paintCB.setSelected(editor.isPainting());
        renderCB.setSelected(editor.isAlwaysRendering());
    }

    private void handleSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;

        actor.addAction(new TableSelectAction(
                game,
                actor,
                decSelectTable,
                TableSelectAction.TYPE_DECORATIVE
        ));
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
            case CB_RENDER:
                editor.setAlwaysRender(state);
        }
    }

    //Called by IDEA form
    private void createUIComponents() {
        //Create table forms
        //Disable row edit
        propertiesTable = new JTable(new String[][]{}, new String[]{"Key", "Value"}) {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        decSelectTable = new JTable(new String[][]{}, new String[]{"ID", "Material"}) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public void refresh() {
        updateMaterialPreview();
        validatePath();
        updateCheckBoxStates();
        updateSelectionTables();
        updateDecPropertyTable(); //Get the actor's first selected decorative
    }

    public void undo() {
        actor.undoLastAction();
    }
}