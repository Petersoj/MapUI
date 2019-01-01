package net.blockops.server.mapui.map;

import net.blockops.server.mapui.MapUIManager;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

public class MapUI {

    private MapUIManager mapUIManager;
    private Player player;

    private MapUIPlayerController mapUIPlayerController;
    private ItemStack mapItem;
    private boolean isOpen = false;
    private BukkitTask updateTask;
    private ArmorStand peripheralBlockArmorStand;
    private ItemStack peripheralBlockItem;
    private Location peripheralBlockLocOffset;
    private EulerAngle peripheralBlockHeadPose;


    public MapUI(MapUIManager mapUIManager, Player player) {
        this.mapUIManager = mapUIManager;
        this.player = player;

        this.mapUIPlayerController = new MapUIPlayerController(this);
    }

    public void init() {
        mapUIManager.getPlayerMapUIs().put(player, this);
        this.mapUIPlayerController.init();
    }

    public void deinit() {
        this.mapUIPlayerController.deinit();
    }

    public void open(String mapItemName, boolean createUpdateTask) {
        Validate.isTrue(!isOpen, "MapUI must be closed in order to open it!");

        this.createMapItem(mapItemName);

        this.mapUIPlayerController.onUIOpen();

        // Create client side EntityArmorStand with vision block item on head (to prevent player
        // from seeing the outside world movement when moving the cursor on the map)
        this.createPeripheralBlockArmorStand();

        if (createUpdateTask) {
            this.createUpdateTask();
        }
        this.isOpen = true;
    }

    public void update(boolean forceRender) {
        Validate.isTrue(isOpen, "MapUI must be open in order to update it!");

        this.mapUIPlayerController.onUIUpdate();
    }

    public void close() {
        Validate.isTrue(isOpen, "MapUI must be open in order to close it!");

        this.mapUIPlayerController.onUIClose();

        this.destroyPeripheralBlockArmorStand();

        if (updateTask != null) {
            this.updateTask.cancel();
        }
        this.isOpen = false;
    }

    public void onClick() {
        Bukkit.broadcastMessage("Clicked!");
    }

    private void createMapItem(String mapItemName) {
        mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

        mapMeta.setDisplayName(mapItemName);
        mapMeta.setMapId(mapUIManager.getEmptyMapID());

        mapItem.setItemMeta(mapMeta);
    }

    private void createPeripheralBlockArmorStand() {
        Location cursorCenterLocation = mapUIPlayerController.getCursorCenterLocation().clone();
        if (peripheralBlockLocOffset != null) {
            cursorCenterLocation.add(peripheralBlockLocOffset);
        }
        peripheralBlockArmorStand =
                (ArmorStand) player.getWorld().spawnEntity(cursorCenterLocation, EntityType.ARMOR_STAND);
        peripheralBlockArmorStand.setVisible(false);
        peripheralBlockArmorStand.setInvulnerable(true);
        peripheralBlockArmorStand.setGravity(false);
        peripheralBlockArmorStand.setArms(true); // So the player can interact with the AS

        if (peripheralBlockHeadPose != null) {
            peripheralBlockArmorStand.setHeadPose(peripheralBlockHeadPose);
        }

        // Send Equipment packet with peripheralBlockItem later cause this tick will override it on the client...
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutEntityEquipment equipmentPacket =
                        new PacketPlayOutEntityEquipment(peripheralBlockArmorStand.getEntityId(),
                                EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(peripheralBlockItem));
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(equipmentPacket);
            }
        }.runTaskLater(mapUIManager.getPlugin(), 2);
    }

    private void destroyPeripheralBlockArmorStand() {
        peripheralBlockArmorStand.remove();
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

    public ArmorStand getPeripheralBlockArmorStand() {
        return peripheralBlockArmorStand;
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

    public ItemStack getPeripheralBlockItem() {
        return peripheralBlockItem;
    }

    public void setPeripheralBlockItem(ItemStack peripheralBlockItem) {
        this.peripheralBlockItem = peripheralBlockItem;
    }

    public Location getPeripheralBlockLocOffset() {
        return peripheralBlockLocOffset;
    }

    public void setPeripheralBlockLocOffset(Location peripheralBlockLocOffset) {
        this.peripheralBlockLocOffset = peripheralBlockLocOffset;
    }

    public EulerAngle getPeripheralBlockHeadPose() {
        return peripheralBlockHeadPose;
    }

    public void setPeripheralBlockHeadPose(EulerAngle peripheralBlockHeadPose) {
        this.peripheralBlockHeadPose = peripheralBlockHeadPose;
    }

}
