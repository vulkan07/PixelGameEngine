package me.Barni;

public class Main {
    public static void main(String[] args) {

        //System.setProperty("sun.java2d.opengl", "True"); //Enable openGL -> Generates error on close
        //System.out.println("\\u001B[31m"+"test"); //NOT WORKING for IDEA try to use cmd
        //System.getProperty("user.home");          //Good to get some paths


        Game game;


        boolean devMode = false;
        if (args.length > 0)
            devMode = args[0].equalsIgnoreCase("true");

        if (devMode)
            game = new Game(System.getProperty("user.dir") + "\\assets\\");
        else
            game = new Game("C:\\Dev\\");

        game.start("Platformer Über Brutal RageGame hogy kilépsz", 1920, 1080, 1, true, false, Logger.LOG_ALL);
    }
}

//====================================================================================\\
//============================= T O D O     L I S T ==================================\\
//====================================================================================\\
//DONE [X] PLAYER: Two hitboxes: touchHitbox, collideHitbox

//DONE [X] *Texture: If animated, texture array+indexer, that's changing by ticks, controlled by tick-data scheduler array; All read from files
//                   If single texture: texture array[0], others are bypassed/null

//DONE [X] *Physics: Make physics work independent from entities, maybe have a map to connect ent groups

//TODO [ ] PHYSICS optimize ent to ent collision
//TODO [ ] PHYSICS fix side stuck collision

//TODO [ ] *Camera: Abstract render from Game
//                  Do scrolling in directions


//TODO [ ] GAME: reduce processor usage
