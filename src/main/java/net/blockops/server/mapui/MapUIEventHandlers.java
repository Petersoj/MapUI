package net.blockops.server.mapui;

import net.blockops.server.mapui.map.MapUI;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
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
import org.bukkit.scheduler.BukkitRunnable;

public class MapUIEventHandlers {

    private MapUIManager mapUIManager;

    public MapUIEventHandlers(MapUIManager mapUIManager) {
        this.mapUIManager = mapUIManager;
    }

    protected void onPlayerInteractEvent(PlayerInteractEvent event) {
        MapUI mapUI = getPlayerMapUI(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            event.setCancelled(true);

            if (event.getHand() == EquipmentSlot.HAND) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    mapUI.onClick();
                }
            }
        }
    }

    protected void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        MapUI mapUI = getPlayerMapUI(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            event.setCancelled(true);

            if (event.getHand() == EquipmentSlot.HAND) {
                mapUI.onClick();
            }
        }
    }

    protected void onPlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event) {
        for (MapUI mapUI : mapUIManager.getPlayerMapUIs().values()) {
            if (mapUI.getMapPeripheralBlock().getPeripheralBlockArmorStand().equals(event.getRightClicked())
                    && mapUI.getPlayer() != event.getPlayer()) {
                event.setCancelled(true);
                return;
            }
        }

        MapUI mapUI = getPlayerMapUI(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            event.setCancelled(true);

            if (event.getHand() == EquipmentSlot.HAND) {
                mapUI.onClick();
            }
        }
    }

    protected void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {

            MapUI mapUI = getPlayerMapUI((Player) event.getDamager());

            if (mapUI != null && mapUI.isOpen()) {
                event.setCancelled(true);
            }
        }
    }

    protected void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        MapUI mapUI = getPlayerMapUI(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.close();
        }
    }

    protected void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        MapUI mapUI = getPlayerMapUI(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.close();
        }
    }

    protected void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            MapUI mapUI = getPlayerMapUI((Player) event.getWhoClicked());

            if (mapUI != null && mapUI.isOpen()) {
                event.setCancelled(true); // Player can't move stuff in their inventory while MapUI open

                // Close their inventory 2 ticks later (to avoid any problems stated in Javadocs for this event)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getWhoClicked().closeInventory();
                    }
                }.runTaskLater(mapUI.getMapUIManager().getPlugin(), 2);
            }
        }
    }

    protected void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        MapUI mapUI = getPlayerMapUI(event.getPlayer());
        if (mapUI != null && mapUI.isOpen()) {
            // This event is somewhat troublesome with giving the player the item they used
            // to have back... so just cancel it and use 'Sneak' to officially close MapUI.
            event.setCancelled(true);
        }
    }

    protected void onPlayerSwapItemEvent(PlayerSwapHandItemsEvent event) {
        MapUI mapUI = getPlayerMapUI(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            event.setCancelled(true); // Player should only use Drop Item 'Q' key to exit MapUI
        }
    }

    protected void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.onPlayerLeaveEvent(event);
    }

    protected void onPlayerKickEvent(PlayerKickEvent event) {
        this.onPlayerLeaveEvent(event);
    }

    private void onPlayerLeaveEvent(PlayerEvent event) {
        MapUI mapUI = getPlayerMapUI(event.getPlayer());

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.close();
        }
        mapUIManager.getPlayerMapUIs().remove(event.getPlayer());
    }

    private MapUI getPlayerMapUI(Player player) {
        return mapUIManager.getPlayerMapUIs().get(player);
    }
}
