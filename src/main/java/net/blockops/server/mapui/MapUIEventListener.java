package net.blockops.server.mapui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class MapUIEventListener implements Listener {

    private MapUIManager mapUIManager;
    private MapUIEventHandlers mapUIEventHandlers;

    protected MapUIEventListener(MapUIManager mapUIManager) {
        this.mapUIManager = mapUIManager;
        this.mapUIEventHandlers = mapUIManager.getMapUIEventHandlers();
    }

    protected void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, mapUIManager.getPlugin());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        this.mapUIEventHandlers.onPlayerInteractEvent(event);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        this.mapUIEventHandlers.onPlayerInteractEntityEvent(event);
    }

    @EventHandler
    public void onPlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event) {
        this.mapUIEventHandlers.onPlayerArmorStandManipulateEvent(event);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        this.mapUIEventHandlers.onEntityDamageByEntityEvent(event);
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        this.mapUIEventHandlers.onPlayerToggleSneakEvent(event);
    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        this.mapUIEventHandlers.onPlayerItemHeldEvent(event);
    }

    @EventHandler
    protected void onInventoryClickEvent(InventoryClickEvent event) {
        this.mapUIEventHandlers.onInventoryClickEvent(event);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        this.mapUIEventHandlers.onPlayerDropItemEvent(event);
    }

    @EventHandler
    public void onPlayerSwapItemEvent(PlayerSwapHandItemsEvent event) {
        this.mapUIEventHandlers.onPlayerSwapItemEvent(event);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.mapUIEventHandlers.onPlayerQuitEvent(event);
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        this.mapUIEventHandlers.onPlayerKickEvent(event);
    }
}
