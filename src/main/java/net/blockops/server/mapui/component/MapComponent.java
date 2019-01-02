package net.blockops.server.mapui.component;

import com.sun.javafx.geom.Vec2f;
import org.bukkit.map.MapCanvas;

public abstract class MapComponent {

    private int x;
    private int y;
    private int width;
    private int height;

    public abstract void draw(MapCanvas mapCanvas);

    public abstract int getWidth();

    public abstract int getHeight();

    protected byte[][] getPixels(MapCanvas mapCanvas, int x, int y, int width, int height, byte[][] pixels) {
        if (pixels == null) {
            pixels = new byte[height][width];
        }
        for (int row = y; row < height; row++) {
            for (int col = x; col < width; col++) {
                pixels[row][col] = mapCanvas.getPixel(col, row);
            }
        }
        return pixels;
    }

    protected void drawPixels(MapCanvas mapCanvas, int x, int y, byte[][] pixels, boolean drawTransparency) {
        for (int row = y; row < pixels.length; row++) {
            for (int col = x; col < pixels[0].length; col++) {
                byte pixel = pixels[row][col];
                if (drawTransparency) {
                    mapCanvas.setPixel(col, row, pixel);
                } else if (pixel != 0) {
                    mapCanvas.setPixel(col, row, pixel);
                }
            }
        }
    }

    protected void drawRectangle(MapCanvas mapCanvas, int x, int y, int width, int height, boolean fill, byte color) {
        for (int xx = x; xx < width; xx++) {
            for (int yy = y; yy < height; yy++) {
                mapCanvas.setPixel(xx, yy, color);
            }
        }
    }

    protected void drawCircle(MapCanvas mapCanvas, int x, int y, int radius, boolean fill, byte color) {
        this.drawElipse(mapCanvas, x, y, radius, radius, fill, color);
    }

    protected void drawElipse(MapCanvas mapCanvas, int x, int y, int width, int height, boolean fill, byte color) {
        // TODO drawElipse
    }

    protected void drawLine(MapCanvas mapCanvas, int x1, int y1, int x2, int y2, int thickness, byte color) {
        // TODO drawLine
        Vec2f start = new Vec2f(x1, y1);
        Vec2f end = new Vec2f(x2, y2);

    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
