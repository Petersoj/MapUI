package net.blockops.server.mapui.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapUIRenderer extends MapRenderer {

    private boolean dirty;

    public MapUIRenderer() {
        super(true); // Contextual
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (dirty) {

        }
    }

    public void addComponent() {

    }

    public void removeComponent() {

    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
