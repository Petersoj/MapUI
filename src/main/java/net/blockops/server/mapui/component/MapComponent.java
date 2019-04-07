package net.blockops.server.mapui.component;

import org.bukkit.map.MapCanvas;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

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

    protected void drawRectangle(MapCanvas mapCanvas, Rectangle rectangle, byte color) {
        this.drawRectangle(mapCanvas, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color);
    }

    protected void drawRectangle(MapCanvas mapCanvas, int x, int y, int width, int height, byte color) {
        for (int xi = x; xi < x + width; xi++) {
            mapCanvas.setPixel(xi, y, color);
            mapCanvas.setPixel(xi, y + height - 1, color);
        }
        for (int yi = y; yi < y + height; yi++) {
            mapCanvas.setPixel(x, yi, color);
            mapCanvas.setPixel(x + width - 1, yi, color);
        }
    }

    protected void drawThickRectangle(MapCanvas mapCanvas, int x, int y, int width, int height, int thickness, byte color) {
        // TODO drawThickRectangle
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    protected void fillRectangle(MapCanvas mapCanvas, Rectangle rectangle, byte color) {
        this.fillRectangle(mapCanvas, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color);
    }

    protected void fillRectangle(MapCanvas mapCanvas, int x, int y, int width, int height, byte color) {
        for (int xi = x; xi < x + width; xi++) {
            for (int yi = y; yi < y + height; yi++) {
                mapCanvas.setPixel(xi, yi, color);
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

    protected void drawLine(MapCanvas mapCanvas, int x1, int y1, int x2, int y2, byte color) {
        this.drawLine(mapCanvas, x1, y1, x2, y2, 1, color);
    }

    protected void drawLine(MapCanvas mapCanvas, int x1, int y1, int x2, int y2, int thickness, byte color) {
        if (thickness < 1) {
            return;
        }
        Point2D start = new Point2D.Float(x1, y1);
        Point2D end = new Point2D.Float(x2, y2);

        double halfThickness = (double) thickness / 2;
        double distance = start.distance(end);
        if (distance == 0) {
            return;
        }
        double dx = (double) (x2 - x1) / distance;
        double dy = (double) (y2 - y1) / distance;

        double currentX = start.getX();
        double currentY = start.getY();

        for (double i = 0; i < distance; i++) {
            if (thickness > 1) {
                double accuracy = 2.5d;
                double preciseDX = dx / accuracy;
                double preciseDY = dy / accuracy;

                double perpX1 = currentX;
                double perpY1 = currentY;
                for (int perpIndex = 0; perpIndex < halfThickness * accuracy; perpIndex++) {
                    perpX1 += preciseDY;
                    perpY1 -= preciseDX;
                    mapCanvas.setPixel((int) Math.round(perpX1), (int) Math.round(perpY1), color);
                }

                double perpX2 = currentX;
                double perpY2 = currentY;
                for (int perpIndex = 0; perpIndex < halfThickness * accuracy; perpIndex++) {
                    perpX2 -= preciseDY;
                    perpY2 += preciseDX;
                    mapCanvas.setPixel((int) Math.round(perpX2), (int) Math.round(perpY2), color);
                }
            }
            mapCanvas.setPixel((int) Math.round(currentX), (int) Math.round(currentY), color);
            currentX += dx;
            currentY += dy;
        }
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

    public void setComponentBounds(Rectangle componentBounds) {
        this.componentBounds = componentBounds;
    }

    public Rectangle getComponentBounds() {
        return componentBounds;
    }
}
