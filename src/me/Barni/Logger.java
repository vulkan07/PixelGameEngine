package me.Barni;

public class Logger {
    private byte logLevel;
    public static final byte LOG_ALL = 3;
    public static final byte LOG_WARN_ERR = 2;
    public static final byte LOG_ERR = 1;
    public static final byte LOG_NONE = 0;


    public Logger(byte level) {
        logLevel = level;


        if (level<0 || level>3)
        {
            logLevel = 4;
            err("Invalid log level: " + level + ", defaulting to ALL");
        }

        String typeStr = null;
        switch (level)
        {
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
        }
        info("Log mode set to " + typeStr);
    }

    public void warn(String msg) {
        if (logLevel >= 2)
            System.out.println("[!] " + msg);
    }

    public void err(String msg) {
        if (logLevel >= 1)
            System.out.println("[X] " + msg);
    }

    public void info(String msg) {
        if (logLevel == 3)
            System.out.println("[.] " + msg);
    }

}
