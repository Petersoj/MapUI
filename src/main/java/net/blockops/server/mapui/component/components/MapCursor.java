package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import org.bukkit.map.MapCanvas;

public class MapCursor extends MapComponent {

    private byte[][] cursorPixels = new byte[][]{{MapUIColors.WHITE}}; // Single white dot cursor
    private byte[][] previousPixels;
    private int previousX;
    private int previousY;

    public MapCursor() {
    }

    public MapCursor(byte[][] cursorPixels) {
        this.cursorPixels = cursorPixels;
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        return; // MapUI will handle all the Cursor updating
    }

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

    @Override
    public int getWidth() {
        return cursorPixels[0].length;
    }

    @Override
    public int getHeight() {
        return cursorPixels.length;
    }

    public byte[][] getCursorPixels() {
        return cursorPixels;
    }

    public void setCursorPixels(byte[][] cursorPixels) {
        this.cursorPixels = cursorPixels;
    }
}
