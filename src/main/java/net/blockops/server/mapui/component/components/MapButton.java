package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.component.MapComponent;
import net.blockops.server.mapui.map.MapUI;

import java.awt.Rectangle;

public abstract class MapButton extends MapComponent {

    private Rectangle clickBounds;
    protected boolean hovered;
    protected boolean clicked;

    public MapButton(Rectangle bounds) {
        this(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public MapButton(int x, int y, int width, int height) {
        super.setComponentBounds(x, y, width, height);
        this.clickBounds = new Rectangle(getComponentBounds());
    }

    // draw() should be implemented by a child class (and it will draw the unaltered MapButton)

    @Override
    public void update() {
        if (clicked) {
            clicked = false; // Clicked should only be true for one tick
        }
    }

    public void onClick(MapUI mapUI, MapCursor mapCursor) {

    }

    public void onHoverEnter(MapUI mapUI, MapCursor mapCursor) {

    }

    public void onHoverExit(MapUI mapUI, MapCursor mapCursor) {

    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public Rectangle getClickBounds() {
        return clickBounds;
    }

    public void setClickBounds(Rectangle clickBounds) {
        this.clickBounds = clickBounds;
    }
}
