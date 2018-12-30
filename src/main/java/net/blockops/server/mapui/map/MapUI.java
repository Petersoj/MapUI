package net.blockops.server.mapui.map;

import net.blockops.server.mapui.MapUIManager;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MapUI {

    private MapUIManager mapUIManager;
    private Player player;
    private BukkitTask updateTask;
    private boolean isOpen = false;
    private ArmorStand peripheralBlockArmorStand;

    public MapUI(MapUIManager mapUIManager, Player player) {
        this.mapUIManager = mapUIManager;
        this.player = player;
    }

    public void open(int hotBarSlotToPutMapIn, boolean createUpdateTask) {

        // freeze the player & teleport to Pitch/Yaw = -70/same Yaw
        // give empty and usable map to player
        // put peripheral vision block item where player is

        if (createUpdateTask) {
            this.updateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    MapUI.this.update(false);
                }
            }.runTaskTimer(mapUIManager.getPlugin(), 0, 0);
        }

        this.isOpen = true;
    }

    public void close() {
        if (!isOpen) {
            return;
        }

        // unfreeze player
        // remove map and put back whatever item was there.

        this.updateTask.cancel();

        this.isOpen = false;
    }

    public void update(boolean forceRender) {

    }

    public void onClick() {

    }

    public ArmorStand getPeripheralBlockArmorStand() {
        return peripheralBlockArmorStand;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
