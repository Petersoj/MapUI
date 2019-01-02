package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;

import java.awt.Rectangle;

public class MapCursor extends MapComponent {

    private byte[][] cursorPixels = new byte[][]{{MapUIColors.WHITE}}; // Default Single white dot cursor
    private Rectangle cursorSensitivityBounds = new Rectangle();
    private byte[][] previousPixels;
    private int previousX;
    private int previousY;

    @Override
    public void draw(MapCanvas mapCanvas) {
        return; // MapUI will handle all the Cursor updating
    }

    // Because MapCursor will likely be drawn so often and redrawing all mapComponents is expensive, MapCursor's
    // update will only draw on pixels it needs to.
    public void drawPreviousPixels(MapCanvas mapCanvas) {
        if (previousPixels == null) { // First time running
            previousPixels = super.getPixels(mapCanvas, getX(), getY(), cursorPixels.length,
                    cursorPixels[0].length, previousPixels); // Samples and creates a new previousPixels array
            previousX = getX();
            previousY = getY();
        }
        super.drawPixels(mapCanvas, previousX, previousY, previousPixels, true);
    }

    public void drawCurrentPixels(MapCanvas mapCanvas) {
        if (cursorPixels == null) {
            return;
        }
        previousX = getX();
        previousY = getY();
        previousPixels = super.getPixels(mapCanvas, previousX, previousY, cursorPixels.length,
                cursorPixels[0].length, previousPixels); // Samples pixels and creates array if necessary
        super.drawPixels(mapCanvas, getX(), getY(), cursorPixels, false);
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
        cursorSensitivityBounds.x = getX();
        cursorSensitivityBounds.y = getY();
    }

    public void setCursorSensitivityBounds(int width, int height) {
        this.cursorSensitivityBounds.width = width;
        this.cursorSensitivityBounds.height = height;
    }

    public Rectangle getCursorSensitivityBounds() {
        return cursorSensitivityBounds;
    }

    public byte[][] getCursorPixels() {
        return cursorPixels;
    }
}
