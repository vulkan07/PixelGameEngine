package me.Barni;

import me.Barni.exceptions.EngineException;

public class Main {
    public static void main(String[] args) {

        //System.setProperty("sun.java2d.opengl", "True"); //Enable openGL -> Generates error on close
        //System.out.println("\\u001B[31m"+"test"); //NOT WORKING for IDEA try to use cmd
        //System.getProperty("user.home");          //Good to get some paths

        Game game;
        game = new Game("C:\\Dev\\");
        System.out.println("\n");

        String anime =
                "                         ##&@&&/,,,*,,,@@/,,,,,%@&(,,.,,,,,*%&***(@@@@@@@@@@@@@@@@@@@@@@@@@%@@@@@@@@@@@@@@@          \n" +
                "                      *(%&@@@/,,,,,,,&&,,#*,**&%*,,.,,,,,*(&****&@@@@@@@@@@&@@@@%@@@@@@&@@@@@@@@@@@@@@@@@@@@         \n" +
                "                     /#@&@&#,,,,,,,&&,.,,,,,(#*,*.,*,,,*/&/**,*@@@@@@@@@@@@&@@@@@@@@@@@@@@@@@@%@@@@@@@@@@@@@@        \n" +
                "                   @#@#&&&*,,,,,.%&/.,,,..,(,,,.***,,###%**,*/@@@@@@@@@@@@@%@@@@@@@@@@@@@@@@@@&%&@@@@@@@@@@@@@       \n" +
                "                 @(&/&&@%*,,,,,.&(,.,,, ..,,,..,*,@%#(&(**.**@@@@@@@@@@@@@@&@@@@%@@&@@@@@@@@@@@%%&@@@@@@@@@@@@@      \n" +
                "                 %(/&&@(,,(,,..&*,.... ,,,,,.,,,(###(%(**,*(&@@@@@@@@@@@@@%@@@@@(@@&@@@@@&@@@@@@%%%@@@@@@@@@@@@@(    \n" +
                "               #%,#&@&*,,,,.,,&/.......,,..... ./,../*..,*//@@@@@@@@@@@@@%#@@@@((&@@@@@@@#@@@@@@&#%@@@@@@@@@@@@@@    \n" +
                "              /(,&&&&*,,,....#(......  ....,.(@@@##/%,, ,**#&@@@@@@@@@@@&(@@@@#((&@@@@@@@#@@@@@@&#%%@@@@@@@@@@@@@    \n" +
                "             (/,#&#&*,.......%, .,....,.,.,*&@@@@#(#*,.,,*/&@@@@@@@@@@@#/&@@@#%/(@#@@@@@##@@@@@@@#%#@@@@@@@@@@@@@    \n" +
                "            (*.(&/&#,,......((,.,.....,,, (@@@@@&#((,,//***@@@@@@%@@@@//#@@&%&%/(#&@%%%&(%#&@@@@@###&@@@@@&@@@@@@    \n" +
                "            *,###&&,,,,..,,.%*.,,.,*.,,..#@@@@@@@#/*,.(,*#/@@%@@&&@@@(%#@%#@@%*/%/&@@@@((%*%@@@@@&%#%@@@@@@@@@@@@    \n" +
                "            ,/(&*&(,,,,,*,,.%/..*/,*(,,,,@@@@@@@@#/,,,(,,,*@&@@@%@@&/*#/&@@@@(/**/%@@@&/(//#@@&@@(###@@@@@@@@@@@@    \n" +
                "           ,,  #/&*.....,/&%#*..,,..(*.,%@@@@@@@@#(,,#(.,,*&(&@#@@#**%@@@@@@&**#*/%@@@*/*/*(@@@@&(#%#@@@@@@@@@@@(    \n" +
                "           ,(  %(&,..,.,/,.*/%,....*((..%@@@#@@@@@(.,@#*,,,/*%@*@(/@@@@@@@@@**&%(/(@@%////*/@@@@@(%@%@@@@@@@@@@@(    \n" +
                "          ,. .# ,&,..,,,,..,,,.,,..,.        *#@(,(* @&(.,,,,/&*#&@@@@@@@@@*%@@%#*/@@*#%*//*(@@@@%@@&@@@@@@@&@@@(    \n" +
                "          .,  * /#*,,...*,*.... ..    *#           # */&(.,,,*#,@@@@@@@@@@/@@@@@%#*(@&@%#*/*/&@@@%@@@@@@@@@&@@@@(    \n" +
                "          .,*,  (/(....., ,,,./...., &@@@@@          *@@&#.,,,*/@@@@@@@@%@@@@@@@@%#*%@@&%(***/@@@@@@@@@@@@&@@@@@     \n" +
                "          .* &    *,,/,.....#&&&..,/%(@@            %&@@@@#/,**#@@@@@@@@.(***///(###,%@@@%#***(@@%@@@@@@@&@@@@@      \n" +
                "                  &*,.... .&%&@@&,..@@@@.**        %&@@@@@@&#,**@@@@@@@@*.          /,@@@@%#***(@@&@&@@@%@@@@@@      \n" +
                "               /*,,..... .%@&&@@@/(,.@@#&.,@&%@@, @@@@@@@@@@@@#**#@@@@&(%.              (%@@***,/@@&@@@%@@@@@@#      \n" +
                "            (/,,,.,&(.....(&&%@@&*#%(*&&&#*(  *@@@@@@@@@@@@@@@@@@#/%@@@@%,/          %&. #@/*,%&//(@%@(@@@@@@@     @ \n" +
                "               &#@@@.,... (&&(@@@,/(&@%&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@/ *         &@&.   */@@&&@(*(@(@@@@@@.    @@(\n" +
                "                  ,/,,,..,%&@%&@&(.(%&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ %&,   *. @@%,*&@*/@%@@@@@&&,*%@&&@@@%      \n" +
                "                 @(,,...*/#&&&(&&%,*#@@@&@&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%  .  (@@@@@@@%/&(@@@@@@%@&*#@@@@&&&(###   \n" +
                "                 #...,((..,@&@&&&&,.*(&@@@@@@@@@@@@@@@@@@@@%@@@@@@@@@@@@@@@@@@@@&@@@@@/*#,/@@@@@@%@@%/%@@@@@@@       \n" +
                "                /...,,..,,,/&@@/&&#.,./#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&@@&&&&&&@#(@&/*/@@@@@@(@@@/*&@@@@@@@%      \n" +
                "               %..,...... ,/&@&%,@%(,,,.,(@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&&&%&&&&%(#,(@@@@@#/*@@@*/@@@@@@@.       \n" +
                "              #.,,....... ..*&@@,.@(/,.,,. *&@@@@@@@@@@@(&@&%%@@@@@@@@@@@@@@@@@@@@(...,#@%@@@/***@@%*(@@@@@@@        \n" +
                "            %(.......... . ..(&&%.,%*..,...,, (&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@,,**,*@@#@@@,****@@(**@@@@@@@        \n" +
                "           @/.......... ... .,%&&,..(/.  ,,...*( (@@@@@@@@@@@@@@@@@@@@@@@@@@* ,*,,,,%@#@@@%,,/*,*&&***&@@@@@@        \n" +
                "          %..........  ..... ..%&% ,..(  ......((((,*&@@@@@@@@@@@@%(. ***,.,,,,,,(&&#&@@@/,.,,,,,&&*,*&@@@@@@.       \n" +
                "         # .  .   .   ..,./%/ .,%&(... . . .. /((((((((/**/(((((((*,,*,,#*.,,,,%/&**@@@@*.(.,,,,,#&*.*&@@@@@@.       \n" +
                "        (          */(%@@@@&#/ .*&%.      ,//(((((((((((((((((((((* ,**,,,.,*(,#,,*@@@@*,.,,,,,**/&*.,#&&@&@@.       \n" +
                "     &*(..      //(@@@@@@@@@@&/ .(%%///////./(((((((((((((((((((((((((((&#(,,/(///@@@&%@@@@&.*,,,,@,.,/&&&@@@&       \n" +
                "    %%.       *//&@@@@@@@@@@@@&/ .(%,./////////////(((((((((((((((((((@@@#(&@@@#(@&@&&@@@@@@@@@#,,(*..,&&&&&&&&      \n" +
                "   &#       ,**/&@@@@@@@@@@@@@@%#..(%*@@%.*///////((////(((((((((((%@@&*&@@@@@@/&&&%&@@@@@@@@@@@@(.*..,%&&&&&&&      \n" +
                "   (       ,***&@@@@@@@@@@@@@@@@(@..###@@@@@@#.*//////////((((((%@@@@@@&/#@@@@#&&&%&@@@@@@@@@@@@@@@ . .*@@&&&&&&     \n" +
                "           ***(@@@@@@@@@@@@@@@@@%%@..(*&@@@@@@@@@@@##@&((((/((,,,#&@@@@@@@@@@&&&&%@@@@@@@@@@@@@@@@@@ ..,%&@&&&&&/    \n" +
                "          .***(&@@@@@@@@@@@@@@@@@(@( .(,@@@@@@@@@@@@@@@@@(/(@@@@@@@@@@@@@@@@@#&&&%@@@@@@@@@@@@@@@@@@..../&&&&&%%%#   \n" +
                "          .***(&&&@@@@@@@@@@@@@@@%@&, ./%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@/&&&(@@@@@@@@@@@@@@@@@@@,....%%%%%%%%%/  \n" +
                "       *  .,,*(&&&@&@@@@@@@@@@@@@&#@/  . &@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&.%&%&@@%@@@@@@@@@@@@@@@@,.  .(%%%%%&%%%( \n" +
                "          .,,,/&&&&&&&@@@@@@#&&@@&(@/   .%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@/,%%/@@,@@@@@@@@@@@@@@@@@ .   .%%%&&&&%%* \n" +
                "          .,,,*&&&&&&&&@&&@&&,%@@&#@#.   .&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@.*%%%@.@@@@@@@@@@@@@@@@@@ .    /%@&@&&%%#.\n" +
                "           ,,,,#&&&&&&&&&&&&&# (&&#&@(    &&@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%./%(&*#@@@@@&&%&&@@@@@@@@&(,   .#%&%%%%%#*\n" +
                "           ,,,,(&&&&&&&&&&&&&&.*%&@@@&    (%&@&@@@@@@@@@@@@@@@@@@@@@@@@@@@* /%*&,%@@%((%&@@@@@@@@@@%. .(%  /%########\n";
        int w = 1920, h = 1080;
        boolean fc = false;
        boolean lookingForNext = false;
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-w")) {
                    w = Integer.parseInt(args[i + 1]);
                    lookingForNext = true;
                    continue;
                }
                if (args[i].equalsIgnoreCase("-h")) {
                    h = Integer.parseInt(args[i + 1]);
                    lookingForNext = true;
                    continue;
                }
                if (args[i].equalsIgnoreCase("-fullscreen")) {
                    fc = true;
                    continue;
                }
                //Fun easter egg -- psst! Don't tell anyone!
                if (args[i].equalsIgnoreCase("-uwu")) {
                    System.out.println("* >----------------------------------------------------------------------------------------------------------------------< *");
                    for (int j = 0; j < anime.length(); j++) {
                        System.out.print(anime.charAt(j));
                        if (j%10==0)
                            Thread.sleep(1);
                    }
                    System.out.println("* >----------------------------------------------------------------------------------------------------------------------< *");
                    continue;
                }
                if (!lookingForNext)
                    System.out.println("Unknown launch argument: " + args[i]);
                lookingForNext = false;
            }
        } catch (Exception e) {
            System.err.println("Invalid arguments!");
            e.printStackTrace();
        }
        try {
            game.start("PixelGameEngine", w, h, fc, false, Logger.LOG_INFO);
        } catch (EngineException e) {
            e.printStackTrace();
        }
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

//TODO [X] Text rendering

//TODO [ ] PHYSICS optimize ent to ent collision
//TODO [ ] PHYSICS fix side stuck collision

