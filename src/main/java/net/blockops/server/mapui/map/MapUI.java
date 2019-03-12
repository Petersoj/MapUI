package net.blockops.server.mapui.map;

import net.blockops.server.mapui.MapUIManager;
import net.blockops.server.mapui.util.Initializers;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MapUI implements Initializers {

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

    @Override
    public void init() {
        playerController.init();
    }

    public void deinit() {
        this.close();
        playerController.deinit();
    }

    public void open(String mapItemName, boolean createUpdateTask, int motionlessCloseTicks) {
        Validate.isTrue(!isOpen, "MapUI must be closed in order to open it!");

        this.createMapItem(mapItemName);

        playerController.configureLocations(player.getLocation());
        mapPeripheralBlock.createPeripheralBlockArmorStand();
        playerController.onUIOpen();

        if (createUpdateTask) {
            this.createUpdateTask(motionlessCloseTicks);
        }

        isOpen = true;
    }

    public void update() {
        Validate.isTrue(isOpen, "MapUI must be open in order to update it!");

        playerController.update();
        viewController.update();
    }

    public void close() {
        if (!isOpen) {
            return;
        }

        playerController.onUIClose();
        mapPeripheralBlock.destroyPeripheralBlockArmorStand();

        if (updateTask != null) {
            updateTask.cancel();
        }

        isOpen = false;
    }

    public void onClick() {
        viewController.onClick();
    }

    private void createMapItem(String mapItemName) {
        mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

        mapMeta.setDisplayName(mapItemName);
        mapMeta.setMapId(mapUIManager.getMapView().getId());

        mapItem.setItemMeta(mapMeta);
    }

    private void createUpdateTask(int motionlessCloseTicks) {
        updateTask = new BukkitRunnable() {
            int motionlessCount = 0;
            boolean checkMotionless = motionlessCloseTicks > 0;

            @Override
            public void run() {
                MapUI.this.update();

                if (checkMotionless) {
                    if (!playerController.didPlayerDirectionChange()) {
                        motionlessCount++;
                    } else {
                        motionlessCount = 0;
                    }

                    if (motionlessCount >= motionlessCloseTicks) {
                        MapUI.this.close();
                    }
                }
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

    public void setMapPeripheralBlock(MapPeripheralBlock mapPeripheralBlock) {
        this.mapPeripheralBlock = mapPeripheralBlock;
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
