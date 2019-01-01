package net.blockops.server.mapui.map;

import net.blockops.server.mapui.MapUIManager;
import net.blockops.server.mapui.component.MapComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class MapUI {

    private MapUIManager mapUIManager;
    private Player player;

    private PlayerController playerController;
    private MapPeripheralBlock mapPeripheralBlock;
    private ArrayList<MapComponent> mapComponents;
    private boolean dirty = false;
    private ItemStack mapItem;
    private boolean isOpen = false;
    private BukkitTask updateTask;

    public MapUI(MapUIManager mapUIManager, Player player) {
        this.mapUIManager = mapUIManager;
        this.player = player;

        this.playerController = new PlayerController(this);
        this.mapPeripheralBlock = new MapPeripheralBlock(this);
        this.mapComponents = new ArrayList<>();
    }

    public void init() {
        mapUIManager.getPlayerMapUIs().put(player, this);
        this.playerController.init();
    }

    public void deinit() {
        this.playerController.deinit();
    }

    public void open(String mapItemName, boolean createUpdateTask) {
        Validate.isTrue(!isOpen, "MapUI must be closed in order to open it!");

        this.createMapItem(mapItemName);
        this.playerController.onUIOpen();
        this.mapPeripheralBlock.createPeripheralBlockArmorStand();

        if (createUpdateTask) {
            this.createUpdateTask();
        }
        this.isOpen = true;
    }

    public void update(boolean forceRender) {
        Validate.isTrue(isOpen, "MapUI must be open in order to update it!");

        this.playerController.onUIUpdate();
    }

    public void close() {
        Validate.isTrue(isOpen, "MapUI must be open in order to close it!");

        this.playerController.onUIClose();
        this.mapPeripheralBlock.destroyPeripheralBlockArmorStand();

        if (updateTask != null) {
            this.updateTask.cancel();
        }
        this.isOpen = false;
    }

    public void onClick() {
        Bukkit.broadcastMessage("Clicked!");
    }

    public void addComponent() {

    }

    public void removeComponent() {

    }

    private void createMapItem(String mapItemName) {
        mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

        mapMeta.setDisplayName(mapItemName);
        mapMeta.setMapId(mapUIManager.getMapView().getId());

        mapItem.setItemMeta(mapMeta);
    }

    private void createUpdateTask() {
        this.updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                MapUI.this.update(false);
            }
        }.runTaskTimer(mapUIManager.getPlugin(), 0, 0);
    }

    public MapUIManager getMapUIManager() {
        return mapUIManager;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public MapPeripheralBlock getMapPeripheralBlock() {
        return mapPeripheralBlock;
    }

    public ArrayList<MapComponent> getMapComponents() {
        return mapComponents;
    }

    public boolean isDirty() {
        return dirty;
    }

    public ItemStack getMapItem() {
        return mapItem;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Player getPlayer() {
        return player;
    }

}
