package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;

public class MapText extends MapComponent {

    private String text;
    private byte color;
    private MapFont font;

    public MapText(String text, byte color, MapFont font, int x, int y) {
        this.text = text;
        this.color = color;
        this.font = font;
        super.setLocation(x, y);
        this.updateSize();
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        mapCanvas.drawText(getX(), getY(), font, insertColorInText(text, color));
    }

    public void updateSize() {
        int width = font.getWidth(text);
        int height = font.getHeight();

        super.setSize(width, height);
    }

    public String insertColorInText(String text, byte color) {
        return "§" + color + ";" + text;
    }

    public void setText(String text, byte color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public byte getColor() {
        return color;
    }

    public MapFont getFont() {
        return font;
    }

    public void setFont(MapFont font) {
        this.font = font;
    }
}
