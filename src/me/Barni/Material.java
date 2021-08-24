package me.Barni;

public class Material {


    public static final int MAT_COUNT  = 12;
    public static final int VOID  = 0;
    public static final int DIRT = 1;
    public static final int GRASS_TILE = 2;
    public static final int GRASS = 3;
    public static final int GLASS = 4;
    public static final int PLANKS = 5;
    public static final int BRICK = 6;
    public static final int WATER = 7;
    public static final int SPIKE = 8;
    public static final int COBBLE_STONE = 9;
    public static final int LAVA = 10;
    public static final int METAL = 11;

    //0 = not solid
    //1 = solid
    //2 = slows down
    //3 = kills
    public static final int[] solid =
        {
                0,
                1,
                1,
                0,
                1,
                1,
                1,
                2,
                3,
                1,
                3,
                1,
        };

    public static boolean isTypeSolid(int solidity)
    {
        return solidity == 1;
    }

    public static final boolean[] translucent =
            {
                    false,
                    false,
                    false,
                    true,
                    true, //glass
                    false,
                    false,
                    true,
                    true,
                    false,
                    false,
                    false
            };

    public static final String[] materialPath =
            {null,
            "dirt",
            "grass_tile",
            "grass",
            "glass",
            "planks",
            "brick",
            "water",
            "spike",
            "cobble",
            "lava",
            "iron",
            };
}
