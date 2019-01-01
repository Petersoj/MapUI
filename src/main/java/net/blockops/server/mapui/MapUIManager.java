package net.blockops.server.mapui;

import net.blockops.server.mapui.map.MapUI;
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
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class MapUIManager {

    private Plugin plugin;
    private int emptyMapID;

    private MapUIListeners mapUIListeners;
    private HashMap<Player, MapUI> playerMapUIs;

    public MapUIManager(Plugin plugin, int emptyMapID) {
        this.plugin = plugin;
        this.emptyMapID = emptyMapID;

        this.mapUIListeners = new MapUIListeners(this);
        this.playerMapUIs = new HashMap<>();
    }

    public void init() {
        mapUIListeners.registerEvents();
    }

    public void deinit() {
        for (MapUI mapUI : playerMapUIs.values()) {
            mapUI.close();
        }
        playerMapUIs.clear();
    }

    public MapUI createMapUI(Player player) {
        MapUI mapUI = new MapUI(this, player);
        mapUI.init();
        return mapUI;
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

    public int getEmptyMapID() {
        return emptyMapID;
    }

    public HashMap<Player, MapUI> getPlayerMapUIs() {
        return playerMapUIs;
    }
}
