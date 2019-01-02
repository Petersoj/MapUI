package net.blockops.server.mapui.component.components;

import net.blockops.server.mapui.component.MapComponent;

import java.awt.Rectangle;

public abstract class MapButton extends MapComponent {

    private MapText mapText;
    private Rectangle clickBounds;
    private boolean hovered;
    private boolean clicked;

    public MapButton(Rectangle bounds) {
        this(null, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public MapButton(MapText mapText, Rectangle bounds) {
        this(mapText, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public MapButton(MapText mapText, int x, int y, int width, int height) {
        this.mapText = mapText;
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

    public void onClick(MapCursor mapCursor) {

    }

    public void onHoverEnter(MapCursor mapCursor) {

    }

    public void onHoverExit(MapCursor mapCursor) {

    }

    public MapText getMapText() {
        return mapText;
    }

    public void setMapText(MapText mapText) {
        this.mapText = mapText;
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
