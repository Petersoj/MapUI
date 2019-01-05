package net.blockops.server.mapui;

import net.blockops.server.mapui.art.SmallMinecraftFont;
import net.blockops.server.mapui.map.MapUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class MapUIManager {

    private Plugin plugin;
    private short emptyMapID;
    private MapView mapView;

    private static SmallMinecraftFont smallMinecraftFont;
    private MapUIEventListener mapUIEventListener;
    private MapUIEventHandlers mapUIEventHandlers;
    private MainRenderer mainRenderer;
    private HashMap<Player, MapUI> playerMapUIs;

    public MapUIManager(Plugin plugin, short emptyMapID) {
        this.plugin = plugin;
        this.emptyMapID = emptyMapID;

        this.mapUIEventHandlers = new MapUIEventHandlers(this);
        this.mapUIEventListener = new MapUIEventListener(this);
        this.mainRenderer = new MainRenderer(this);
        this.playerMapUIs = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
    public void init() {
        mapUIEventListener.registerEvents();

        mapView = Bukkit.getMap(emptyMapID); // Deprecated, but it shouldn't be...
        for (MapRenderer mapRenderer : mapView.getRenderers()) {
            mapView.removeRenderer(mapRenderer);
        }
        mapView.addRenderer(mainRenderer);
    }

    public void deinit() {
        for (MapUI mapUI : playerMapUIs.values()) {
            if (mapUI.isOpen()) {
                mapUI.close();
                mapUI.deinit();
            }
        }
        playerMapUIs.clear();
        mapView.removeRenderer(mainRenderer);
    }

    public SmallMinecraftFont getSmallMinecraftFont() {
        if (smallMinecraftFont == null) {
            try {
                smallMinecraftFont = new SmallMinecraftFont();
                smallMinecraftFont.createFont();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return smallMinecraftFont;
    }

    public void registerPlayerMapUI(Player key, MapUI value) {
        if (playerMapUIs.containsKey(key)) {
            throw new IllegalStateException("Cannot register an already initialized MapUI!");
        }
        this.playerMapUIs.put(key, value);
    }

    public void deregisterPlayerMapUI(Player key) {
        this.playerMapUIs.remove(key);
    }

    public MapUIEventHandlers getMapUIEventHandlers() {
        return mapUIEventHandlers;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public MapView getMapView() {
        return mapView;
    }

    protected HashMap<Player, MapUI> getPlayerMapUIs() {
        return playerMapUIs;
    }
}
