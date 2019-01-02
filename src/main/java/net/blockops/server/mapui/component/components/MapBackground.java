package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;

public class MapBackground extends MapComponent {

    private byte backgroundColor = MapUIColors.TRANSPARENT;

    public MapBackground() {
    }

    public MapBackground(byte backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        this.drawRectangle(mapCanvas, 0, 0, 128, 128, true, backgroundColor);
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 128;
    }

    public byte getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(byte backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
