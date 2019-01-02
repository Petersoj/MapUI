package net.blockops.server.mapui.map;

import net.blockops.server.mapui.MapUIManager;
import net.blockops.server.mapui.component.MapComponent;
import net.blockops.server.mapui.component.components.MapCursor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;

import java.util.ArrayList;

public class MapUI {

    private MapUIManager mapUIManager;
    private Player player;

    private PlayerController playerController;
    private MapPeripheralBlock mapPeripheralBlock;
    private ArrayList<MapComponent> mapComponents;
    private boolean dirty = false;
    private MapCursor mapCursor;
    private ItemStack mapItem;
    private boolean isOpen = false;

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

    public void open(String mapItemName) {
        Validate.isTrue(!isOpen, "MapUI must be closed in order to open it!");

        this.createMapItem(mapItemName);
        this.playerController.onUIOpen();
        this.mapPeripheralBlock.createPeripheralBlockArmorStand();

        this.isOpen = true;
    }

    // Method should be called every tick (by MainRenderer object)
    public void update(MapCanvas mapCanvas) {
        Validate.isTrue(isOpen, "MapUI must be open in order to update it!");

        this.playerController.onUIUpdate();

        if (playerController.didPlayerMoveCursor() && mapCursor != null) {
            mapCursor.setLocation(playerController.getX(), playerController.getY());
            if (!dirty) {
                mapCursor.drawPreviousPixels(mapCanvas);
                mapCursor.drawCurrentPixels(mapCanvas);
            }
        }

        if (dirty) {
            Bukkit.broadcastMessage("Dirty Draw");
            if (mapCursor != null) {
                mapCursor.drawPreviousPixels(mapCanvas);
            }
            for (MapComponent mapComponent : mapComponents) {
                if (mapComponent instanceof MapCursor) {
                    throw new IllegalStateException("MapCursor should not be in MapUI components! Use setCursor()");
                } else {
                    mapComponent.draw(mapCanvas);
                }
            }
            if (mapCursor != null) {
                mapCursor.drawCurrentPixels(mapCanvas);
            }
            dirty = false;
        }
    }

    public void close() {
        Validate.isTrue(isOpen, "MapUI must be open in order to close it!");

        this.playerController.onUIClose();
        this.mapPeripheralBlock.destroyPeripheralBlockArmorStand();

        this.isOpen = false;
    }

    public void onClick() {
        Bukkit.broadcastMessage("Clicked!");
    }

    public void addComponent(MapComponent mapComponent) {
        mapComponents.add(mapComponent);
    }

    public void removeComponent(MapComponent mapComponent) {
        mapComponents.remove(mapComponent);
    }

    public void clearComponents() {
        mapComponents.clear();
    }

    private void createMapItem(String mapItemName) {
        mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

        mapMeta.setDisplayName(mapItemName);
        mapMeta.setMapId(mapUIManager.getMapView().getId());

        mapItem.setItemMeta(mapMeta);
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

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public MapCursor getMapCursor() {
        return mapCursor;
    }

    public void setMapCursor(MapCursor mapCursor) {
        this.mapCursor = mapCursor;
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
