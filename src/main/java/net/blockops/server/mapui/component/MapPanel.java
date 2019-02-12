package net.blockops.server.mapui.component;

import java.util.ArrayList;

/**
 * Convenience class for creating "Panels" for the ViewController
 * Something like "MenuPanel" might be a subclass of this class.
 * This makes it easier to transition from one view to another.
 */
public abstract class MapPanel {

    private ArrayList<MapComponent> mapComponents;

    public void MapPanel() {
        this.mapComponents = new ArrayList<>();
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
}
