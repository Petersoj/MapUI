package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.component.MapComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;

public class MapText extends MapComponent {

    private String text;
    private MapFont font;

    public MapText(String text, MapFont font, int x, int y) {
        this.text = text;
        this.font = font;
        super.setLocation(x, y);
        this.updateSize();
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        mapCanvas.drawText(getX(), getY(), font, text);
    }

    public void centerHorizontallyTo(int leftX, int width) {
        Validate.isTrue(width >= getWidth(), "Width cannot be less than the text width!");

        int offset = Math.round(((float) width - getWidth()) / 2f);
        setLocation(leftX + offset, getY());
    }

    public void centerVerticallyTo(int topY, int height) {
        Validate.isTrue(height >= getHeight(), "Height cannot be less than the text height!");

        int offset = Math.round(((float) height - getHeight()) / 2f);
        setLocation(topY + offset, getY());
    }

    public void updateSize() {
        int width = font.getWidth(text);
        int height = font.getHeight();

        super.setSize(width, height);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MapFont getFont() {
        return font;
    }

    public void setFont(MapFont font) {
        this.font = font;
    }
}
