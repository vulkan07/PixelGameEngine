package me.Barni.graphics;

import me.Barni.Game;
import org.joml.Vector4f;

import java.awt.*;
import java.nio.charset.StandardCharsets;


public class RenderableText {
    public static final String DEFAULT_FONT = "fonts/consolas";
    private String text;
    private float xPos;
    private float yPos;


    private float widthFactor, lineBreakHeight; //TODO parameter getters/setters
    private float width;
    private float height;
    private float size;
    private Vector4f color;
    private QuadBatch batch;
    private boolean inMap;
    private ShaderProgram textShader;

    private static Game game;

    private int numCols = 1024 / 64, numRows = 1024 / 64, cellWidth = 64, cellHeight = 64, startCharID = 32; //TODO read meta from file


    public RenderableText(String text, float x, float y) {
        this.text = text;
        this.xPos = x;
        this.yPos = y;
        this.size = 1.3f/3f;
        this.color = new Vector4f(255,255,255,255); //WHITE
        initGraphics();
    }

    public RenderableText(String text, float x, float y, float size, Color color) {
        this.text = text;
        this.xPos = x;
        this.yPos = y;
        this.size = size;
        this.color = new Vector4f(255,255,255,255); //WHITE
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
        batch.loadTexture(DEFAULT_FONT);
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

        width = 30 * size * chars.length;
        height = cellHeight * size;

        int line = 1;
        float lineXPos = 0;

        for (int i = 0; i < numChars; i++) {
            int offset = i * 4;
            byte currChar = (byte) (chars[i] - startCharID);

            float col = currChar % numCols;
            float row = currChar / numCols;

            lineXPos++;

            if (chars[i] == '\n') {
                line++;
                lineXPos = 0;
            }

            //X
            positions[offset] = xPos + lineXPos * 30 * size;   //x
            texCoords[offset * 2] = col / numCols;  //u
            texCoords[offset * 2 + 1] = row / numRows;  //v

            //Y
            positions[offset + 1] = yPos + line * cellHeight/1.8f;             //y
            texCoords[offset * 2 + 2] = (col + 1) / numCols;  //u
            texCoords[offset * 2 + 3] = (row + 1) / numRows;  //v

            //Width
            positions[offset + 2] = cellWidth * size;     //w
            texCoords[offset * 2 + 4] = (col + 1) / numCols;  //u
            texCoords[offset * 2 + 5] = row / numRows;  //v

            //Height
            positions[offset + 3] = cellHeight * size;    //h
            texCoords[offset * 2 + 6] = col / numCols;  //u
            texCoords[offset * 2 + 7] = (row + 1) / numRows;  //v

        }
        batch.updateData(positions, texCoords);

    }

    public void render() {
        if (text.isEmpty())
            return;
        textShader.uploadVec4("tint", color);
        textShader.uploadBool("uInMap", isInMap());
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
        this.size = size/3f;
        updateText();
    }

    public Color getColor() {
        return new Color(color.x, color.y, color.z, color.w);
    }

    public void setColor(Color color) {
        this.color.x = remap(color.getRed(), 0, 255, 0, 1);
        this.color.y = remap(color.getGreen(), 0, 255, 0, 1);
        this.color.z = remap(color.getBlue(), 0, 255, 0, 1);
        this.color.w = remap(color.getAlpha(), 0, 255, 0, 1);
        updateText();
    }    public void setColor(Vector4f color) {
        this.color.x = remap(color.x(), 0, 255, 0, 1);
        this.color.y = remap(color.y(), 0, 255, 0, 1);
        this.color.z = remap(color.z(), 0, 255, 0, 1);
        this.color.w = remap(color.w(), 0, 255, 0, 1);
        updateText();
    }
    private float remap(float value, float low1, float high1, float low2, float high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
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
