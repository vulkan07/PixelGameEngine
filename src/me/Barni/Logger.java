package me.Barni;

public class Logger {
    private byte logLevel;
    private int indentLevel = 0;
    public static final String INDENT = "    ";
    private static String currentIndent = "";
    public static final boolean LOG_ERRORS_TO_SYS_OUT = true;
    public static final byte LOG_SUPER = 4;
    public static final byte LOG_INFO = 3;
    public static final byte LOG_WARN_ERR = 2;
    public static final byte LOG_ERR = 1;
    public static final byte LOG_NONE = 0;


    public Logger(byte level) {
        logLevel = level;


        if (level < 0 || level > 4) {
            logLevel = 4;
            err("[LOGGER] Invalid level: " + level + ", defaulting to ALL");
        }

        String typeStr = null;
        switch (level) {
            case 0:
                typeStr = "NONE";
                break;
            case 1:
                typeStr = "ONLY ERRORS";
                break;
            case 2:
                typeStr = "ERROR-WARNING";
                break;
            case 3:
                typeStr = "ALL";
                break;
            case 4:
                typeStr = "ALL + SUBINFO";
                break;
        }
        info("[LOGGER] mode set to " + typeStr);
    }

    public String getIndentStr() {
        return currentIndent;
    }

    private void updateIndentionString() {
        currentIndent = "";
        for (int i = 0; i < indentLevel; i++)
            currentIndent += INDENT;
    }

    public void increaseIndention(String indentionName) {
        indentLevel++;
        if (indentionName != null)
            System.out.println(currentIndent + "    << " + indentionName + " >>");
        updateIndentionString();
    }

    public void decreaseIndention(String indentionName) {
        indentLevel--;
        if (indentLevel < 0)
            indentLevel = 0;
        updateIndentionString();
        if (indentionName != null)
            System.out.println(currentIndent + "    << " + indentionName + " >>\n");
    }

    public void warn(String msg) {
        if (logLevel >= 2)
            System.out.println(currentIndent + "[!] " + msg);
    }

    public void raw(String msg) {
        System.out.println(currentIndent + msg);
    }

    public void err(String msg) {
        if (logLevel >= 1)
            if (LOG_ERRORS_TO_SYS_OUT)
                System.out.println(currentIndent + "[X] " + msg);
            else
                System.err.println(currentIndent + "[X] " + msg);
    }

    public void info(String msg) {
        if (logLevel >= 3)
            System.out.println(currentIndent + "[.] " + msg);
    }

    public void subInfo(String msg) {
        if (logLevel == 4)
            System.out.println(currentIndent + " .  " + msg);
    }
}
