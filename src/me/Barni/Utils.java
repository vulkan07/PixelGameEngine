package me.Barni;

import com.sun.javafx.binding.StringFormatter;
import org.lwjgl.opengl.*;

public abstract class Utils {

    public static Game game;

    public static void init(Game g) {
        game = g;
    }

    //--------- MATH ----------\\
    //-------------------------\\
    public static float lerp(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }

    public static float remap(float value, float low1, float high1, float low2, float high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }


    //--------- OpenGL ----------\\
    //---------------------------\\

    public static final int glErrorCodes[] = {
            GL30.GL_NO_ERROR,

            GL30.GL_INVALID_ENUM,
            GL30.GL_INVALID_VALUE,
            GL30.GL_INVALID_OPERATION,
            GL30.GL_STACK_OVERFLOW,
            GL30.GL_STACK_UNDERFLOW,
            GL30.GL_OUT_OF_MEMORY,
            GL30.GL_INVALID_FRAMEBUFFER_OPERATION,
    };

    public static final String glErrorMessages[] = {
            "No Error",
            "Invalid Enum",
            "Invalid Value",
            "Invalid Operation",
            "Stack Overflow",
            "Stack Underflow",
            "Out of memory",
            "Invalid Framebuffer Operation",
    };

    public static void GLClearError() {
        while (GL30.glGetError() != GL30.GL_NO_ERROR) ;
    }

    public static String GLCheckError() {
        int code;
        StringBuilder msg = new StringBuilder();
        while ((code = GL30.glGetError()) != GL30.GL_NO_ERROR) {
            for (int i = 0; i < glErrorCodes.length; i++) {
                if (code == glErrorCodes[i]) {
                    msg.append("[GLError] [")
                            //append("0x").
                            //append(Integer.toHexString(code)).
                            //append(" - ").
                            .append(glErrorMessages[i])
                            .append("] at ")
                            .append(getStackCaller().replace("me.Barni.", ""));
                }
            }
        }
        String s = msg.toString();

        if (s.equals("")) {
            return "";
        } else {
            //System.out.println(s);
            game.getLogger().err(s);
            return s + "\n";
        }
    }

    public static String getStackCaller() {
        java.util.Map<Thread, StackTraceElement[]> m = Thread.getAllStackTraces();
        StackTraceElement[] ste = m.get(Thread.currentThread());
        return ste[4].toString();
    }
}
