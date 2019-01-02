package net.blockops.server.mapui.art;

import java.awt.Color;

public class MapUIColors {

    private MapUIColors() {
    }

    // Colors found on https://minecraft.gamepedia.com/Map_item_format
    private static final Color[] basicMapColors = new Color[]{
            c(0, 0, 0), // Transparent Color
            c(127, 178, 56),
            c(247, 233, 163),
            c(199, 199, 199),
            c(255, 0, 0),
            c(160, 160, 255),
            c(167, 167, 167),
            c(0, 124, 0),
            c(255, 255, 255),
            c(164, 168, 184),
            c(151, 109, 77),
            c(112, 112, 112),
            c(64, 64, 255),
            c(143, 119, 72),
            c(255, 252, 245),
            c(216, 127, 51),
            c(178, 76, 216),
            c(102, 153, 216),
            c(229, 229, 51),
            c(127, 204, 25),
            c(242, 127, 165),
            c(76, 76, 76),
            c(153, 153, 153),
            c(76, 127, 153),
            c(127, 63, 178),
            c(51, 76, 178),
            c(102, 76, 51),
            c(102, 127, 51),
            c(153, 51, 51),
            c(25, 25, 25),
            c(250, 238, 77),
            c(92, 219, 213),
            c(74, 128, 255),
            c(0, 217, 58),
            c(129, 86, 49),
            c(112, 2, 0),
            c(209, 177, 161),
            c(159, 82, 36),
            c(149, 87, 108),
            c(112, 108, 138),
            c(186, 133, 36),
            c(103, 117, 53),
            c(160, 77, 78),
            c(57, 41, 35),
            c(135, 107, 98),
            c(87, 92, 92),
            c(122, 73, 88),
            c(76, 62, 92),
            c(76, 50, 35),
            c(76, 82, 42),
            c(142, 60, 46),
            c(37, 22, 16)
    };

    private static Color c(int r, int g, int b) {
        return new Color(r, g, b);
    }

    private static class MapUIColor {

        private final byte mapID;
        private final Color color;

        MapUIColor(byte mapID, Color color) {
            this.mapID = mapID;
            this.color = color;
        }

        public byte getMapID() {
            return mapID;
        }

        public Color getColor() {
            return color;
        }

        @Override
        public String toString() {
            return "MapID: " + mapID + " " + color.toString();
        }
    }

    // 4 variations of 51 colors. TODO remove public here
    public static MapUIColor[] allMapColors = new MapUIColor[basicMapColors.length * 4];

    static {
        for (int index = 0; index < allMapColors.length; index += 4) {

            byte baseMapIDIndex = (byte) Math.floor(index / 4f);

            for (byte variant = 0; variant < 4; variant++) {
                byte newMapID = (byte) (baseMapIDIndex * (byte) 4 + variant & 0xFF);

                int rgbMultiplier;
                if (variant == 0) {
                    rgbMultiplier = 180;
                } else if (variant == 1) {
                    rgbMultiplier = 220;
                } else if (variant == 2) {
                    rgbMultiplier = 255;
                } else { // 3
                    rgbMultiplier = 135;
                }

                Color basicMapColor = basicMapColors[baseMapIDIndex];
                int r = basicMapColor.getRed() * rgbMultiplier / 255;
                int g = basicMapColor.getGreen() * rgbMultiplier / 255;
                int b = basicMapColor.getBlue() * rgbMultiplier / 255;

                allMapColors[index + variant] = new MapUIColor(newMapID, new Color(r, g, b));
            }
        }
    }

    public static byte matchColor(int r, int g, int b) {
        return matchColor(new Color(r, g, b));
    }

    public static byte matchColor(Color color) {
        int smallestDistance = Byte.MAX_VALUE;
        MapUIColor bestMatchColor = null;

        for (int i = 4; i < allMapColors.length; i++) {
            MapUIColor otherMapUIColor = allMapColors[i];

            Color otherMapColor = otherMapUIColor.getColor();

            int distance = (int) Math.sqrt(
                    Math.pow(otherMapColor.getRed() - color.getRed(), 2) +
                            Math.pow(otherMapColor.getGreen() - color.getGreen(), 2) +
                            Math.pow(otherMapColor.getBlue() - color.getBlue(), 2));
            if (distance < smallestDistance) {
                if (color.equals(new Color(10, 10, 10))) {
                    System.out.println(otherMapUIColor);
                }
                smallestDistance = distance;
                bestMatchColor = otherMapUIColor;
            }
        }
        return bestMatchColor == null ? 0 : bestMatchColor.getMapID();
    }

    // Declaring these constants at the bottom is necessary for matchColor method to work properly.

    public static byte TRANSPARENT = 0;

    public static byte BLACK = matchColor(new Color(10, 10, 10));

    public static byte WHITE = matchColor(new Color(255, 255, 255));

    public static byte GRAY = matchColor(new Color(145, 145, 145));

    public static byte LIGHT_GRAY = matchColor(new Color(190, 190, 190));

    public static byte DARK_GRAY = matchColor(new Color(70, 70, 70));

    public static byte RED = matchColor(new Color(255, 0, 0));

    public static byte DARK_RED = matchColor(new Color(150, 0, 0));

    public static byte ORANGE = matchColor(new Color(216, 127, 51));

    public static byte YELLOW = matchColor(new Color(255, 255, 85));

    public static byte GREEN = matchColor(new Color(85, 230, 85));

    public static byte AQUA = matchColor(new Color(85, 255, 255));

    public static byte BLUE = matchColor(new Color(85, 85, 255));

    public static byte PURPLE = matchColor(new Color(115, 12, 115));
}
