package net.blockops.server.mapui.map;

import net.blockops.server.mapui.art.MapUIColors;
import net.blockops.server.mapui.component.MapComponent;
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
    private MapBackground mapBackground = new MapBackground(MapUIColors.TRANSPARENT); // Default transparent background
    private boolean dirty = true;

    public ViewController(MapUI mapUI) {
        this.mapUI = mapUI;

        this.playerController = mapUI.getPlayerController();
        this.mapComponents = new ArrayList<>();
    }

    public void update() {
        if (mapCanvas == null) {
            return;
        }

        for (MapComponent mapComponent : mapComponents) {
            if (mapComponent instanceof MapButton) {
                MapButton mapButton = (MapButton) mapComponent;
                if (mapButton.isClicked()) {
                    mapButton.setClicked(false); // Clicked should only happen for one tick.
                }
            }
        }

        if (playerController.didPlayerMoveCursor()) {
            if (mapCursor != null) {

                mapCursor.setLocation(playerController.getX(), playerController.getY());

                if (!dirty) {
                    mapCursor.drawPreviousPixels(mapCanvas);
                    mapCursor.drawCurrentPixels(mapCanvas);
                }

                // Button hovering
                MapButton topMostHoveredButton = null;
                for (MapComponent mapComponent : mapComponents) {
                    if (mapComponent instanceof MapButton) {
                        MapButton mapButton = (MapButton) mapComponent;

                        mapCursor.updateCursorSensitivityLocation();

                        if (mapButton.getClickBounds().intersects(mapCursor.getCursorSensitivityBounds())) {
                            topMostHoveredButton = mapButton;
                        } else {
                            if (mapButton.isHovered()) {
                                mapButton.onHoverExit(mapCanvas, mapCursor);
                                mapButton.setHovered(false);
                            }
                        }
                    }
                }
                if (topMostHoveredButton != null) {
                    topMostHoveredButton.onHoverEnter(mapCanvas, mapCursor);
                    topMostHoveredButton.setHovered(true);
                }
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

            if (mapCursor != null) {
                mapCursor.drawCurrentPixels(mapCanvas);
            }
        }
    }

    public void onClick() {
        if (mapCursor == null) {
            return;
        }
        MapButton topMostClickedButton = null;
        for (MapComponent mapComponent : mapComponents) {
            if (mapComponent instanceof MapButton) {
                MapButton mapButton = (MapButton) mapComponent;

                mapCursor.updateCursorSensitivityLocation();

                if (mapButton.getClickBounds().intersects(mapCursor.getCursorSensitivityBounds())) {
                    topMostClickedButton = mapButton;
                }
            }
        }
        if (topMostClickedButton != null) {
            topMostClickedButton.onClick(mapCanvas, mapCursor);
            topMostClickedButton.setClicked(true);
        }
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
