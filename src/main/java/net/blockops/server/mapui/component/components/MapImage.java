package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;

import java.awt.image.BufferedImage;

public class MapImage extends MapComponent {

    private BufferedImage image;
    private byte[][] buffer;

    public MapImage(BufferedImage image, int x, int y) {
        this.image = image;
        super.setComponentBounds(x, y, image.getWidth(), image.getHeight());
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        if (buffer == null) {
            this.convertImageToMapColors();
        }
        super.drawPixels(mapCanvas, getX(), getY(), buffer, false);
    }

    private void convertImageToMapColors() {
        this.buffer = MapUIColors.imageToMapColors(image);
    }
}
