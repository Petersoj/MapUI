package net.blockops.server.mapui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class MapUIListeners implements Listener {

    private MapUIManager mapUIManager;

    public MapUIListeners(MapUIManager mapUIManager) {
        this.mapUIManager = mapUIManager;
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, mapUIManager.getPlugin());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        this.mapUIManager.onPlayerInteractEvent(event);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        this.mapUIManager.onPlayerInteractEntityEvent(event);
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        this.mapUIManager.onPlayerToggleSneakEvent(event);
    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        this.mapUIManager.onPlayerItemHeldEvent(event);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        this.mapUIManager.onPlayerDropItemEvent(event);
    }

    @EventHandler
    public void onPlayerSwapItemEvent(PlayerSwapHandItemsEvent event) {
        this.mapUIManager.onPlayerSwapItemEvent(event);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.mapUIManager.onPlayerQuitEvent(event);
    }

}
