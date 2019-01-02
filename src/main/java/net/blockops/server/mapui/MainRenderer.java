package net.blockops.server.mapui;

import net.blockops.server.mapui.map.MapUI;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MainRenderer extends MapRenderer {

    private MapUIManager mapUIManager;

    public MainRenderer(MapUIManager mapUIManager) {
        super(true); // Contextual (MapCanvas per player)
        this.mapUIManager = mapUIManager;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        MapUI mapUI = mapUIManager.getPlayerMapUIs().get(player);
        if (mapUI != null && mapUI.isOpen()) {
            mapUI.update(mapCanvas);
        }
    }
}
