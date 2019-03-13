package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;

public class MapBackground extends MapComponent {

    private byte backgroundColor;

    public MapBackground() {
        this(MapUIColors.TRANSPARENT);
    }

    public MapBackground(byte backgroundColor) {
        this.backgroundColor = backgroundColor;
        super.setComponentBounds(0, 0, 127, 127);
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        this.fillRectangle(mapCanvas, getX(), getY(), getWidth(), getHeight(), backgroundColor);
    }

    public byte getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(byte backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
