package net.blockops.server.mapui.map;

import net.blockops.server.mapui.MapUIManager;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MapUI {

    private MapUIManager mapUIManager;
    private Player player;

    private MapUIPlayerController mapUIPlayerController;
    private ItemStack mapItem;
    private boolean isOpen = false;
    private BukkitTask updateTask;
    private ArmorStand peripheralBlockArmorStand;

    public MapUI(MapUIManager mapUIManager, Player player) {
        this.mapUIManager = mapUIManager;
        this.player = player;

        this.mapUIPlayerController = new MapUIPlayerController(this);
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

    }

    public void createMapItem(String mapItemName) {
        mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

        mapMeta.setDisplayName(mapItemName);
        mapMeta.setMapId(mapUIManager.getEmptyMapID());

        mapItem.setItemMeta(mapMeta);
    }

    public void createPeripheralBlockArmorStand() {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityArmorStand entityArmorStand = new EntityArmorStand(entityPlayer.world);

        peripheralBlockArmorStand = (ArmorStand) entityArmorStand.getBukkitEntity();
        peripheralBlockArmorStand.setVisible(false);
        peripheralBlockArmorStand.setInvulnerable(true);

        if (mapUIManager.getPeripheralBlockItem() != null) {
            peripheralBlockArmorStand.setHelmet(mapUIManager.getPeripheralBlockItem());
        }
        if (mapUIManager.getArmorStandHeadPoseOverride() != null) {
            peripheralBlockArmorStand.setHeadPose(mapUIManager.getArmorStandHeadPoseOverride());
        }
        Location initialLocation = mapUIPlayerController.getInitialLocation();
        if (mapUIManager.getArmorStandLocOffset() != null) {
            initialLocation.add(mapUIManager.getArmorStandLocOffset());
        }
        entityArmorStand.setLocation(initialLocation.getX(), initialLocation.getY(),
                initialLocation.getZ(), initialLocation.getYaw() - 180, 0f);

        PacketPlayOutSpawnEntityLiving spawnEntityPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
        entityPlayer.playerConnection.sendPacket(spawnEntityPacket);
    }

    public void destroyPeripheralBlockArmorStand() {
        PacketPlayOutEntityDestroy entityDestroyPacket =
                new PacketPlayOutEntityDestroy(peripheralBlockArmorStand.getEntityId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(entityDestroyPacket);
    }

    public void createUpdateTask() {
        this.updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                MapUI.this.update(false);
            }
        }.runTaskTimer(mapUIManager.getPlugin(), 0, 0);
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
}
