package me.Barni;

public class Main {
    public static void main(String[] args)
    {

        //System.setProperty("sun.java2d.opengl", "true"); //Enable openGL -> Generates error on close
        //System.out.println("\\u001B[31m"+"test"); //NOT WORKING for IDEA try to use cmd
        //System.getProperty("user.home");          //Good to get some paths
        Game game = new Game();
        //game.init();
        game.start("Platformer Über Brutal RageGame hogy kilépsz", 1920, 1080, 1, true, false, Logger.LOG_ALL);
    }
}

//====================================================================================\\
//============================= T O D O     L I S T ==================================\\
//====================================================================================\\
        //TODO [ ] PLAYER: Two hitboxes: touchHitbox, collideHitbox

        //TODO [ ] *Texture: If animated, texture array+indexer, that's changing by ticks, controlled by tick scheduler array; Read all from files
        //                   If single texture: texture array[0], others are bypassed/null

        //TODO [ ] *Physics: Make physics work independent from entities, maybe have a map to connect ent groups

        //TODO [ ] *Camera: Abstract render from Game
        //                  Do scrolling in directions, define algorithm


        //TODO [ ] GAME: reduce processor usage
