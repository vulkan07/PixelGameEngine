package me.Barni.graphics;

import me.Barni.Game;
import org.joml.Vector4f;

import java.awt.*;
import java.nio.charset.StandardCharsets;


public class RenderableText {
    private String text;
    private float xPos;
    private float yPos;



    private float width;
    private float height;
    private float size;
    private Color color;
    private QuadBatch batch;
    private boolean  inMap;
    private ShaderProgram textShader;

    private static Game game;

    private int numCols = 512 / 32, numRows = 512 / 32, cellWidth = 32, cellHeight = 32, startCharID = 32; //TODO


    public RenderableText(String text, int x, int y) {
        this.text = text;
        this.xPos = x;
        this.yPos = y;
        this.size = 1.3f;
        this.color = Color.WHITE;
        initGraphics();
    }

    public RenderableText(String text, int x, int y, int size, Color color) {
        this.text = text;
        this.xPos = x;
        this.yPos = y;
        this.size = size;
        this.color = color;
        initGraphics();
    }

    public static void init(Game g) {
        game = g;
    }

    //Constructors call this
    private void initGraphics() {

        /*
        vao = new VertexArrayObject();
        float[] vArray = new float[8];
        vao.setVertexData(vArray);
        vao.setElementData(QUAD_ELEMENT_ARRAY);
        vao.addAttributePointer(2, "pos"); //Position (x,y)
        vao.addAttributePointer(2, "tex"); //TX coords (u,v)
        t = new Texture();
        outdatedTexture = true;*/
        batch = new QuadBatch(new float[]{}, new float[]{});
        batch.loadTexture("fonts/consolas");
        textShader = new ShaderProgram(game);
        textShader.create("gui_text");
        textShader.link();
        updateText();
    }

    public void updateText() {
        byte[] chars = text.getBytes(StandardCharsets.UTF_8);
        int numChars = chars.length;
        float[] positions = new float[numChars * 4];
        float[] texCoords = new float[numChars * 8];

        width = 9 * size * chars.length;
        height = cellHeight * size;

        for (int i = 0; i < numChars; i++) {
            int offset = i * 4;
            byte currChar = (byte) (chars[i] - startCharID);

            float col = currChar % numCols;
            float row = currChar / numCols;

            //X
            positions[offset] = xPos + i * 9*size;   //x
            texCoords[offset * 2] = col / numCols;  //u
            texCoords[offset * 2 + 1] = row / numRows;  //v

            //Y
            positions[offset + 1] = yPos;             //y
            texCoords[offset * 2 + 2] = (col + 1) / numCols;  //u
            texCoords[offset * 2 + 3] = (row + 1) / numRows;  //v

            //Width
            positions[offset + 2] = cellWidth*size;     //w
            texCoords[offset * 2 + 4] = (col + 1) / numCols;  //u
            texCoords[offset * 2 + 5] = row / numRows;  //v

            //Height
            positions[offset + 3] = cellHeight*size;    //h
            texCoords[offset * 2 + 6] = col / numCols;  //u
            texCoords[offset * 2 + 7] = (row + 1) / numRows;  //v

        }
        batch.updateData(positions, texCoords);

    }

    public void render() {
        textShader.uploadVec4(
                "tint",
                new Vector4f(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        color.getAlpha()));
        batch.render(textShader);
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        updateText();
    }

    public float getxPos() {
        return xPos;
    }

    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
        updateText();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        updateText();
    }

    public boolean isInMap() {
        return inMap;
    }

    public void setInMap(boolean inMap) {
        this.inMap = inMap;
    }
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
