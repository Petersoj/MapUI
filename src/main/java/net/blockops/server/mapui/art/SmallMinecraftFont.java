package net.blockops.server.mapui.art;

import org.bukkit.map.MapFont;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SmallMinecraftFont extends MapFont {

    private final String fontSpritesheetPath = "/Small Minecraft Font.png";
    private final int spriteHeight = 7;
    private final int spriteWidth = 7;
    private final int standardCharHeight = 5;
    private final String[] characters = new String[]{
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "abcdefghijklmnopqrstuvwxyz",
            "0123456789!?\"\'()*/+-=,.;:",
            "<>{}[]"
    };

    public SmallMinecraftFont() {
    }

    public void createFont() throws IOException {
        BufferedImage fontImage = ImageIO.read(this.getClass().getResourceAsStream(fontSpritesheetPath));

        if (fontImage.getWidth() * fontImage.getHeight() % (spriteHeight * spriteWidth) != 0) {
            throw new IllegalArgumentException("Font SpriteSheet is the wrong size!");
        }

        for (int charRow = 0; charRow < fontImage.getHeight() / spriteHeight; charRow++) {
            for (int charCol = 0; charCol < fontImage.getWidth() / spriteWidth; charCol++) {
                if (charCol > characters[charRow].length() - 1) {
                    continue;
                }

                int charWidth = 0;
                for (int row = 0; row < spriteHeight; row++) {
                    for (int col = 0; col < spriteWidth; col++) {
                        int x = charCol * spriteWidth + col;
                        int y = charRow * spriteHeight + row;

                        if (new Color(fontImage.getRGB(x, y), true).getAlpha() != 0) {
                            charWidth = Math.max(charWidth, col + 1);
                        }
                    }
                }

                boolean[] fontData = new boolean[spriteHeight * charWidth];

                for (int row = 0; row < spriteHeight; row++) {
                    for (int col = 0; col < charWidth; col++) {
                        int x = charCol * spriteWidth + col;
                        int y = charRow * spriteHeight + row;
                        fontData[row * charWidth + col] = new Color(fontImage.getRGB(x, y), true).getAlpha() != 0;
                    }
                }
                CharacterSprite characterSprite = new CharacterSprite(charWidth, spriteHeight, fontData);
                this.setChar(characters[charRow].charAt(charCol), characterSprite);
            }
        }

        CharacterSprite spaceCharacterSprite = new CharacterSprite(2, spriteHeight, new boolean[2 * spriteHeight]);
        this.setChar(' ', spaceCharacterSprite);
    }

    public int getStandardCharHeight() {
        return standardCharHeight;
    }
}
