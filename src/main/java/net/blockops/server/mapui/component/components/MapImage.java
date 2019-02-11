package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;

import java.awt.image.BufferedImage;

public class MapImage extends MapComponent {

    private byte[][] buffer;

    public MapImage(byte[][] imageMapColors, int x, int y) {
        this.buffer = imageMapColors;
        super.setComponentBounds(x, y, imageMapColors[0].length, imageMapColors.length);
    }

    public MapImage(BufferedImage image, int x, int y) {
        this.buffer = MapUIColors.imageToMapColors(image);
        super.setComponentBounds(x, y, image.getWidth(), image.getHeight());
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        if (buffer != null) {
            super.drawPixels(mapCanvas, getX(), getY(), buffer, false);
        }
    }
}
