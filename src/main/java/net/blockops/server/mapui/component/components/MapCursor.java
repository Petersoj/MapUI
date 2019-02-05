package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;

import java.awt.Rectangle;

public class MapCursor extends MapComponent {

    private byte[][] cursorPixels = new byte[][]{{MapUIColors.WHITE}}; // Default Single white dot cursor
    private Rectangle cursorSensitivityBounds = new Rectangle();
    private Rectangle relativeCursorSensitivityBounds = new Rectangle();
    private boolean hidden = false;
    private byte[][] previousPixels;
    private int previousX;
    private int previousY;

    @Override
    public void draw(MapCanvas mapCanvas) {
        return; // ViewController will handle all the Cursor updating
    }

    // Because MapCursor will likely be drawn so often and redrawing all mapComponents is expensive, MapCursor's
    // update will only draw on pixels it needs to.
    public void drawPreviousPixels(MapCanvas mapCanvas) {
        if (previousPixels == null) { // First time running
            this.savePreviousPixels(mapCanvas);
        }
        super.drawPixels(mapCanvas, previousX, previousY, previousPixels, true);
    }

    public void drawCurrentPixels(MapCanvas mapCanvas) {
        if (cursorPixels == null) {
            return;
        }
        this.savePreviousPixels(mapCanvas);

        super.drawPixels(mapCanvas, getX(), getY(), cursorPixels, false);
    }

    private void savePreviousPixels(MapCanvas mapCanvas) {
        int clampedX = getX() > 127 ? 127 : (getX() < 0 ? 0 : getX());
        int clampedY = getY() > 127 ? 127 : (getY() < 0 ? 0 : getY());
        setLocation(clampedX, clampedY);

        previousX = getX();
        previousY = getY();

        previousPixels = super.getPixels(mapCanvas, getX(), getY(), cursorPixels.length,
                cursorPixels[0].length, previousPixels); // Samples pixels and creates array if necessary
    }

    public void setColor(byte color) {
        for (int row = 0; row < cursorPixels.length; row++) {
            for (int col = 0; col < cursorPixels[0].length; col++) {
                if (cursorPixels[row][col] != 0) {
                    cursorPixels[row][col] = color;
                }
            }
        }
    }

    public void setCursorPixels(byte[][] cursorPixels, boolean sensitivityBoundsAreSame) {
        this.cursorPixels = cursorPixels;
        this.setSize(cursorPixels[0].length, cursorPixels.length);

        if (sensitivityBoundsAreSame) {
            this.cursorSensitivityBounds.x = getWidth();
            this.cursorSensitivityBounds.y = getHeight();
        }
    }

    public void updateCursorSensitivityLocation() {
        cursorSensitivityBounds.x = relativeCursorSensitivityBounds.x + getX();
        cursorSensitivityBounds.y = relativeCursorSensitivityBounds.y + getY();
    }

    public void setCursorSensitivityBounds(int relativeX, int relativeY, int width, int height) {
        this.relativeCursorSensitivityBounds.x = relativeX;
        this.relativeCursorSensitivityBounds.y = relativeY;
        this.relativeCursorSensitivityBounds.width = width;
        this.relativeCursorSensitivityBounds.height = height;

        this.cursorSensitivityBounds.width = width;
        this.cursorSensitivityBounds.height = height;
    }

    public Rectangle getCursorSensitivityBounds() {
        return cursorSensitivityBounds;
    }

    public byte[][] getCursorPixels() {
        return cursorPixels;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
