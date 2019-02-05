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

    // This renderer is only meant to give MapUI a per-player MapCanvas reference.
    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        // TODO create a way that only gets a MapUI if there was a new one registered (inefficient to always call .get)
        MapUI mapUI = mapUIManager.getPlayerMapUIs().get(player);
        if (mapUI != null && mapUI.getViewController().getMapCanvas() == null) {
            mapUI.getViewController().setMapCanvas(mapCanvas);
        }
    }
}
