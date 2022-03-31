package me.Barni;

public class Main {
    public static void main(String[] args) {

        //System.setProperty("sun.java2d.opengl", "True"); //Enable openGL -> Generates error on close
        //System.out.println("\\u001B[31m"+"test"); //NOT WORKING for IDEA try to use cmd
        //System.getProperty("user.home");          //Good to get some paths

        Game game;
        game = new Game("C:\\Dev\\");

        int w = 1920, h = 1080;
        boolean fc = false;
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-w"))
                    w = Integer.parseInt(args[i + 1]);

                if (args[i].equals("-h"))
                    h = Integer.parseInt(args[i + 1]);
                if (args[i].equals("-fullscreen"))
                    fc = true;
            }
        } catch (Exception e) {
            System.err.println("Invalid arguments!");
            e.printStackTrace();
        }
        game.start("Über Brutal Platformer 2077", w, h, fc, false, Logger.LOG_INFO);
    }
}

//====================================================================================\\
//============================= T O D O     L I S T ==================================\\
//====================================================================================\\

//TODO [X] Fix camera shift on zoom
//TODO [ ] Fix native resolution set

//TODO [ ] Fix GPU Usage

//TODO [ ] Add gizmos to edit levels
//TODO [ ] Add menus to edit levels

//TODO [ ] Text rendering

//TODO [ ] PHYSICS optimize ent to ent collision
//TODO [ ] PHYSICS fix side stuck collision

