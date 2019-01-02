package net.blockops.server.mapui.map;

import net.blockops.server.mapui.MapUIManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MapUI {

    private MapUIManager mapUIManager;
    private Player player;

    private BukkitTask updateTask;
    private PlayerController playerController;
    private ViewController viewController;
    private MapPeripheralBlock mapPeripheralBlock;
    private ItemStack mapItem;
    private boolean isOpen = false;

    public MapUI(MapUIManager mapUIManager, Player player) {
        this.mapUIManager = mapUIManager;
        this.player = player;

        this.playerController = new PlayerController(this);
        this.viewController = new ViewController(this);
        this.mapPeripheralBlock = new MapPeripheralBlock(this);
    }

    public void init() {
        mapUIManager.getPlayerMapUIs().put(player, this);
        this.playerController.init();
    }

    public void deinit() {
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

    public void update() {
        Validate.isTrue(isOpen, "MapUI must be open in order to update it!");

        this.playerController.update();
        this.viewController.update();
    }

    public void close() {
        Validate.isTrue(isOpen, "MapUI must be open in order to close it!");

        this.playerController.onUIClose();
        this.mapPeripheralBlock.destroyPeripheralBlockArmorStand();

        if (updateTask != null) {
            updateTask.cancel();
        }

        this.isOpen = false;
    }

    public void onClick() {
        this.viewController.onClick();
    }

    private void createMapItem(String mapItemName) {
        mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

        mapMeta.setDisplayName(mapItemName);
        mapMeta.setMapId(mapUIManager.getMapView().getId());

        mapItem.setItemMeta(mapMeta);
    }

    private void createUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                MapUI.this.update();
            }
        }.runTaskTimer(mapUIManager.getPlugin(), 0, 0);
    }

    public MapUIManager getMapUIManager() {
        return mapUIManager;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public ViewController getViewController() {
        return viewController;
    }

    public MapPeripheralBlock getMapPeripheralBlock() {
        return mapPeripheralBlock;
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
