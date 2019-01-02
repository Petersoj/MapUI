package net.blockops.server.mapui;

import net.blockops.server.mapui.art.SmallMinecraftFont;
import net.blockops.server.mapui.map.MapUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class MapUIManager {

    private Plugin plugin;
    private short emptyMapID;
    private MapView mapView;

    private SmallMinecraftFont smallMinecraftFont;
    private MapUIListeners mapUIListeners;
    private MainRenderer mainRenderer;
    private HashMap<Player, MapUI> playerMapUIs;

    public MapUIManager(Plugin plugin, short emptyMapID) {
        this.plugin = plugin;
        this.emptyMapID = emptyMapID;

        this.mapUIListeners = new MapUIListeners(this);
        this.mainRenderer = new MainRenderer(this);
        this.playerMapUIs = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
    public void init() {
        mapUIListeners.registerEvents();

        mapView = Bukkit.getMap(emptyMapID);
        for (MapRenderer mapRenderer : mapView.getRenderers()) {
            mapView.removeRenderer(mapRenderer);
        }
        mapView.addRenderer(mainRenderer);
    }

    public void deinit() {
        for (MapUI mapUI : playerMapUIs.values()) {
            if (mapUI.isOpen()) {
                mapUI.close();
            }
        }
        playerMapUIs.clear();
        mapView.removeRenderer(mainRenderer);
    }

    // -- Event Handlers --

    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                this.onPlayerInteract(event.getPlayer());
            }
        }
    }

    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            this.onPlayerInteract(event.getPlayer());
        }
    }

    public void onPlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event) {
        MapUI mapUI = playerMapUIs.get(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            event.setCancelled(true);
            mapUI.onClick();
        }
    }

    private void onPlayerInteract(Player player) {
        MapUI mapUI = playerMapUIs.get(player);

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.onClick();
        }
    }

    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        MapUI mapUI = playerMapUIs.get(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.close();
        }
    }

    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        MapUI mapUI = playerMapUIs.get(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.close();
        }
    }

    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        MapUI mapUI = playerMapUIs.get(event.getPlayer());
        if (mapUI != null && mapUI.isOpen()) {
            // This event is somewhat troublesome with giving the player the item they used
            // to have back... so just cancel it and use 'Sneak' to officially close MapUI.
            event.setCancelled(true);
        }
    }

    public void onPlayerSwapItemEvent(PlayerSwapHandItemsEvent event) {
        MapUI mapUI = playerMapUIs.get(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            event.setCancelled(true); // Player should only use Drop Item 'Q' key to exit MapUI
        }
    }

    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.onPlayerLeaveEvent(event);
    }

    public void onPlayerKickEvent(PlayerKickEvent event) {
        this.onPlayerLeaveEvent(event);
    }

    private void onPlayerLeaveEvent(PlayerEvent event) {
        MapUI mapUI = playerMapUIs.get(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.close();
        }
        playerMapUIs.remove(event.getPlayer());
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public MapView getMapView() {
        return mapView;
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

    public HashMap<Player, MapUI> getPlayerMapUIs() {
        return playerMapUIs;
    }
}
