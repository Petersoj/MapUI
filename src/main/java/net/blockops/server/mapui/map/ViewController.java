package net.blockops.server.mapui.map;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
import net.blockops.server.mapui.component.MapPanel;
import net.blockops.server.mapui.component.components.MapBackground;
import net.blockops.server.mapui.component.components.MapButton;
import net.blockops.server.mapui.component.components.MapCursor;
import org.bukkit.map.MapCanvas;

import java.util.ArrayList;

public class ViewController {

    private MapUI mapUI;

    private PlayerController playerController;
    private ArrayList<MapComponent> mapComponents;
    private MapCanvas mapCanvas;
    private MapCursor mapCursor;
    private MapBackground mapBackground;
    private boolean dirty;

    public ViewController(MapUI mapUI) {
        this.mapUI = mapUI;

        this.playerController = mapUI.getPlayerController();
        this.mapComponents = new ArrayList<>();
        this.mapBackground = new MapBackground(MapUIColors.TRANSPARENT); // Default transparent background
        this.dirty = true;
    }

    protected void update() {
        if (mapCanvas == null) { // MapCanvas will be set by the MainRenderer class
            return;
        }

        // Update all components first
        for (MapComponent mapComponent : mapComponents) {
            mapComponent.update(); // Convenience method for some MapComponents that need to update every tick
        }

        // Handle MapCursor
        if (mapCursor != null && playerController.didPlayerDirectionChange()) {
            mapCursor.setLocation(playerController.getCursorX(), playerController.getCursorY());
            mapCursor.updateCursorSensitivityLocation();

            // Update raster to display cursor if raster is not dirty
            if (!dirty && !mapCursor.isHidden()) {
                mapCursor.drawPreviousPixels(mapCanvas);
                mapCursor.drawCurrentPixels(mapCanvas);
            }

            // Handle Button hovering
            MapButton topMostHoveredButton = null;
            for (MapComponent mapComponent : mapComponents) {
                if (mapComponent instanceof MapButton) {
                    MapButton mapButton = (MapButton) mapComponent;

                    if (mapButton.getClickBounds().intersects(mapCursor.getCursorSensitivityBounds())) {
                        topMostHoveredButton = mapButton;
                    } else if (mapButton.isHovered()) {
                        mapButton.setHovered(false);
                        mapButton.onHoverExit(mapUI, mapCursor);
                    }
                }
            }
            if (topMostHoveredButton != null && !topMostHoveredButton.isHovered()) {
                topMostHoveredButton.setHovered(true);
                topMostHoveredButton.onHoverEnter(mapUI, mapCursor);
            }
        }

        if (dirty) {
            dirty = false;

            // - Clear Canvas and redraw everything (Background -> components -> cursor) -

            mapBackground.draw(mapCanvas);

            for (MapComponent mapComponent : mapComponents) {
                if (mapComponent instanceof MapCursor) {
                    throw new IllegalStateException("MapCursor should not be in MapUI components! Use setCursor()");
                } else if (mapComponent instanceof MapBackground) {
                    throw new IllegalStateException(
                            "MapBackground should not be in MapUI components! Use setBackground()");
                }
                mapComponent.draw(mapCanvas);
            }

            if (mapCursor != null && !mapCursor.isHidden()) {
                mapCursor.drawCurrentPixels(mapCanvas);
            }
        }
    }

    public void onClick() {
        if (mapCursor == null) {
            return;
        }
        for (MapComponent mapComponent : mapComponents) {
            if (mapComponent instanceof MapButton) {
                MapButton mapButton = (MapButton) mapComponent;

                if (mapButton.isHovered()) { // Only one button is allowed to be hovered over at a time
                    mapButton.setClicked(true);
                    mapButton.onClick(mapUI, mapCursor);
                    break;
                }
            }
        }
    }

    public void setMapPanel(MapPanel mapPanel) {
        mapComponents.clear();
        mapComponents.addAll(mapPanel.getMapComponents());
        dirty = true;
    }

    public void addComponent(MapComponent mapComponent) {
        mapComponents.add(mapComponent);
    }

    public void removeComponent(MapComponent mapComponent) {
        mapComponents.remove(mapComponent);
    }

    public void clearComponents() {
        mapComponents.clear();
    }

    public ArrayList<MapComponent> getMapComponents() {
        return mapComponents;
    }

    public MapCanvas getMapCanvas() {
        return mapCanvas;
    }

    public void setMapCanvas(MapCanvas mapCanvas) {
        this.mapCanvas = mapCanvas;
    }

    public MapCursor getMapCursor() {
        return mapCursor;
    }

    public void setMapCursor(MapCursor mapCursor) {
        this.mapCursor = mapCursor;
    }

    public MapBackground getMapBackground() {
        return mapBackground;
    }

    public void setMapBackground(MapBackground mapBackground) {
        this.mapBackground = mapBackground;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
