package net.blockops.server.mapui.component;

import com.sun.javafx.geom.Vec2f;
import org.bukkit.map.MapCanvas;

import java.awt.Rectangle;

public abstract class MapComponent {

    private Rectangle componentBounds = new Rectangle(0, 0, 0, 0);

    public abstract void draw(MapCanvas mapCanvas);

    // Convenient method if a MapComponent needs to update every tick
    // This method is called before draw()
    public void update() {
    }

    protected byte[][] getPixels(MapCanvas mapCanvas, int x, int y, int width, int height, byte[][] pixels) {
        if (pixels == null) {
            pixels = new byte[height][width];
        }
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pixels[row][col] = mapCanvas.getPixel(col + x, row + y);
            }
        }
        return pixels;
    }

    protected void drawPixels(MapCanvas mapCanvas, int x, int y, byte[][] pixels, boolean drawTransparency) {
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                byte pixel = pixels[row][col];
                if (drawTransparency) {
                    mapCanvas.setPixel(col + x, row + y, pixel);
                } else if (pixel != 0) {
                    mapCanvas.setPixel(col + x, row + y, pixel);
                }
            }
        }
    }

    protected void drawRectangle(MapCanvas mapCanvas, Rectangle rectangle, boolean fill, byte color) {
        this.drawRectangle(mapCanvas, rectangle.x, rectangle.y, rectangle.width, rectangle.height, fill, color);
    }

    protected void drawRectangle(MapCanvas mapCanvas, int x, int y, int width, int height, boolean fill, byte color) {
        for (int xx = x; xx < width + x; xx++) {
            for (int yy = y; yy < height + y; yy++) {
                if (fill) {
                    mapCanvas.setPixel(xx, yy, color);
                } else {
                    if (yy == y || yy == y + height - 1) {
                        mapCanvas.setPixel(xx, yy, color);
                    } else if (xx == x || xx == x + width - 1) {
                        mapCanvas.setPixel(xx, yy, color);
                    }
                }
            }
        }
    }

    protected void drawCircle(MapCanvas mapCanvas, int x, int y, int radius, boolean fill, byte color) {
        this.drawElipse(mapCanvas, x, y, radius, radius, fill, color);
    }

    protected void drawElipse(MapCanvas mapCanvas, int x, int y, int width, int height, boolean fill, byte color) {
        // TODO drawElipse
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    protected void drawLine(MapCanvas mapCanvas, int x1, int y1, int x2, int y2, int thickness, byte color) {
        // TODO drawLine
        Vec2f start = new Vec2f(x1, y1);
        Vec2f end = new Vec2f(x2, y2);

        throw new UnsupportedOperationException("Not implemented yet!");
    }

    public void centerHorizontallyTo(int leftX, int width) {
        int halfWidth = Math.round(((float) width) / 2f);
        int halfComponentWidth = Math.round(((float) getWidth() / 2f));

        int offset = halfWidth - halfComponentWidth;
        setLocation(leftX + offset, getY());
    }

    public void centerVerticallyTo(int topY, int height) {
        int halfHeight = Math.round(((float) height) / 2f);
        int halfComponentHeight = Math.round(((float) getHeight() / 2f));

        int offset = halfHeight - halfComponentHeight;
        setLocation(getX(), topY + offset);
    }

    public void setLocation(int x, int y) {
        componentBounds.x = x;
        componentBounds.y = y;
    }

    public int getX() {
        return componentBounds.x;
    }

    public int getY() {
        return componentBounds.y;
    }

    public void setSize(int width, int height) {
        componentBounds.width = width;
        componentBounds.height = height;
    }

    public int getWidth() {
        return componentBounds.width;
    }

    public int getHeight() {
        return componentBounds.height;
    }

    public void setComponentBounds(int x, int y, int width, int height) {
        this.componentBounds.setBounds(x, y, width, height);
    }

    public Rectangle getComponentBounds() {
        return componentBounds;
    }
}
