package net.blockops.server.mapui.map;

import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class MapPeripheralBlock {

    private MapUI mapUI;
    private Player player;

    private ArmorStand peripheralBlockArmorStand;
    private ItemStack peripheralBlockItem;
    private Location peripheralBlockLocOffset;
    private EulerAngle peripheralBlockHeadPose;
    private boolean disabled;

    public MapPeripheralBlock(MapUI mapUI) {
        this.mapUI = mapUI;
        this.player = mapUI.getPlayer();

        // Defaults
        this.peripheralBlockItem = new ItemStack(Material.BLACK_CONCRETE);
        this.peripheralBlockLocOffset = new Location(player.getWorld(), 0.15, 0.27, -0.2);
        this.peripheralBlockHeadPose = new EulerAngle(20d, 0, 0);
        this.disabled = false;
    }

    // Create client side EntityArmorStand with vision block item on head (to prevent player
    // from seeing the outside world movement when moving the cursor on the map)
    public void createPeripheralBlockArmorStand() {
        if (peripheralBlockItem == null || disabled) {
            return;
        }

        Location cursorCenterLocation = mapUI.getPlayerController().getCursorCenterLocation().clone();
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
        if (peripheralBlockItem != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketPlayOutEntityEquipment equipmentPacket =
                            new PacketPlayOutEntityEquipment(peripheralBlockArmorStand.getEntityId(),
                                    EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(peripheralBlockItem));
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(equipmentPacket);
                }
            }.runTaskLater(mapUI.getMapUIManager().getPlugin(), 1);
        }
    }

    public void destroyPeripheralBlockArmorStand() {
        if (peripheralBlockArmorStand != null && !disabled) {
            peripheralBlockArmorStand.remove();
        }
    }

    public ArmorStand getPeripheralBlockArmorStand() {
        return peripheralBlockArmorStand;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
