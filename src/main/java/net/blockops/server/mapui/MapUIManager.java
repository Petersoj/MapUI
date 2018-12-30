package net.blockops.server.mapui;

import net.blockops.server.mapui.map.MapUI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;

public class MapUIManager {

    private Plugin plugin;
    private int emptyMapID;
    private MapUIListeners mapUIListeners;
    private HashMap<Player, MapUI> playerMapUIs;
    private ItemStack peripheralBlockItem;
    private Location armorStandLocOffset;
    private EulerAngle armorStandHeadPoseOverride;

    public MapUIManager(Plugin plugin, int emptyMapID) {
        this.plugin = plugin;
        this.emptyMapID = emptyMapID;

        this.mapUIListeners = new MapUIListeners(this);
        this.playerMapUIs = new HashMap<>();
    }

    public void setPeripheralBlockParams(ItemStack peripheralBlockItem, Location armorStandLocOffset,
                                         EulerAngle armorStandHeadPoseOverride) {
        this.peripheralBlockItem = peripheralBlockItem;
        this.armorStandLocOffset = armorStandLocOffset;
        this.armorStandHeadPoseOverride = armorStandHeadPoseOverride;
    }

    public void init() {
        this.mapUIListeners.registerEvents();
    }

    public void deinit() {
        for (MapUI mapUI : playerMapUIs.values()) {
            mapUI.close();
        }
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

    private void onPlayerInteract(Player player) {
        MapUI mapUI = playerMapUIs.get(player);

        if (mapUI != null && mapUI.isOpen()) {
            mapUI.onClick();
        }
    }

    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {

    }

    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {

    }

    public void onPlayerSwapItemEvent(PlayerSwapHandItemsEvent event) {

    }

    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.playerMapUIs.get(event.getPlayer()).close();
        this.playerMapUIs.remove(event.getPlayer());
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ItemStack getPeripheralBlockItem() {
        return peripheralBlockItem;
    }
}
