package net.blockops.server.mapui.map;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerController {

    private MapUI mapUI;
    private Player player;
    private final float centerPitch = 70f;
    private final float centerYaw = 40f;
    private final float pitchBound = 20f;
    private final float yawBound = 40f;
    private Location previousLocation;
    private Location cursorCenterLocation;
    private float lastPitch;
    private float lastYaw;
    private ItemStack initialMainHandItemStack;
    private ItemStack initialOffHandItemStack;
    private boolean wasFlightAllowed;
    private boolean wasFlying;
    private final float defaultFlySpeed = 0.1f;
    private float previousFlySpeed = defaultFlySpeed;
    private int playerMoveIssueCount;
    private boolean playerMovedCursor;
    private int x;
    private int y;
    private static DataWatcherObject<Byte> baseDataWatcherObject = new DataWatcherObject<>(0, DataWatcherRegistry.a);
    private DataWatcher tempDataWatcher;
    private DataWatcher entityPlayerDataWatcher;

    public PlayerController(MapUI mapUI) {
        this.mapUI = mapUI;
        this.player = mapUI.getPlayer();

        this.tempDataWatcher = new DataWatcher(((CraftPlayer) player).getHandle());
        this.entityPlayerDataWatcher = ((CraftPlayer) player).getHandle().getDataWatcher();
    }

    public void init() {
        tempDataWatcher.register(baseDataWatcherObject, 0);
    }

    public void deinit() {

    }

    public void onUIOpen() {
        Location location = player.getLocation();
        this.previousLocation = location;

        cursorCenterLocation = location.clone();
        cursorCenterLocation.setYaw(centerYaw);
        cursorCenterLocation.setPitch(centerPitch);
        this.freezePlayer(cursorCenterLocation);
        this.calculateCursorPosition(cursorCenterLocation.getPitch(), cursorCenterLocation.getYaw());
        playerMovedCursor = true;

        initialMainHandItemStack = player.getInventory().getItemInMainHand();
        initialOffHandItemStack = player.getInventory().getItemInOffHand();
        player.getInventory().setItemInMainHand(mapUI.getMapItem());
        player.getInventory().setItemInOffHand(null);

        this.setClientInvisible(true);

        // TODO collision prevention (use either previous scoreboard or new scoreboard)
    }

    public void onUIUpdate() {
        Location location = player.getLocation();

        // Check if player moved at all (they may have deactivated flying causing gravity to move them!)
        if (location.getX() != cursorCenterLocation.getX() || location.getY() != cursorCenterLocation.getY()
                || location.getZ() != cursorCenterLocation.getZ() || !player.isFlying()) {
            this.unFreezePlayer(); // reset flying speeds and such
            this.freezePlayer(cursorCenterLocation); // reset player location and flying speed/flying

            if (player.isFlying()) { // Player was flying and therefore must have been a world movement issue
                playerMoveIssueCount++;
            }
        }

        // This ensures that the player is not involuntarily being moved indefinitely
        if (playerMoveIssueCount > 5) {
            mapUI.close();
            return;
        }

        float pitch = location.getPitch();
        float yaw = location.getYaw();

        boolean outOfBounds = clampToBounds(location);
        if (outOfBounds) {
            player.teleport(location);
        }

        if (!outOfBounds) {
            playerMovedCursor = pitch != lastPitch || yaw != lastYaw;
            lastPitch = pitch;
            lastYaw = yaw;

            if (playerMovedCursor) {
                this.calculateCursorPosition(pitch, yaw);
            }
        }
    }

    public void onUIClose() {
        this.unFreezePlayer();
        player.teleport(previousLocation);

        player.getInventory().setItemInMainHand(initialMainHandItemStack);
        player.getInventory().setItemInOffHand(initialOffHandItemStack);

        this.setClientInvisible(false);
    }

    public void teleportToCursorLocation(int x, int y) {
        Validate.isTrue(x >= 0 && y >= 0 && x < 128 && y < 128);
        // TODO teleport player to specified pitch/yaw


        this.x = x;
        this.y = y;
    }

    private boolean clampToBounds(Location location) {
        float pitch = location.getPitch();
        float yaw = location.getYaw();

        boolean outOfBounds = false;
        float lowerPitchBound = centerPitch + pitchBound;
        if (pitch > lowerPitchBound) { // Should be impossible, but just check for it anyway...
            location.setPitch(lowerPitchBound);
            outOfBounds = true;
        }
        float upperPitchBound = centerPitch - pitchBound;
        if (pitch < upperPitchBound) {
            location.setPitch(upperPitchBound);
            outOfBounds = true;
        }
        float rightBoundYaw = centerYaw + yawBound;
        if (yaw > rightBoundYaw) {
            location.setYaw(rightBoundYaw);
            outOfBounds = true;
        }
        float leftBoundYaw = centerYaw - yawBound;
        if (yaw < leftBoundYaw) {
            location.setYaw(leftBoundYaw);
            outOfBounds = true;
        }
        return outOfBounds;
    }

    private void calculateCursorPosition(float pitch, float yaw) {
        x = (int) (yaw / (centerYaw + yawBound) * 128);
        y = (int) ((pitch - 50) / (centerPitch + pitchBound - 50) * 128);
    }

    private void freezePlayer(Location atLocation) {
        if (player.getFlySpeed() == 0) {
            previousFlySpeed = defaultFlySpeed;
        } else if (player.getFlySpeed() != defaultFlySpeed) {
            previousFlySpeed = player.getFlySpeed();
        }
        wasFlying = player.isFlying();
        wasFlightAllowed = player.getAllowFlight();

        player.setFlySpeed(0f);
        player.setAllowFlight(true);
        player.setFlying(true);

        if (atLocation != null) {
            player.teleport(atLocation);
        }
    }

    private void unFreezePlayer() {
        player.setFlySpeed(previousFlySpeed);
        player.setFlying(wasFlying);
        player.setAllowFlight(wasFlightAllowed);
    }

    private void setClientInvisible(boolean invisible) {
        byte previousData = entityPlayerDataWatcher.get(baseDataWatcherObject);
        if (invisible) {
            tempDataWatcher.set(baseDataWatcherObject, (byte) (previousData | 0b0010_0000));
        } else {
            tempDataWatcher.set(baseDataWatcherObject, (byte) (previousData & ~0b0010_0000));
        }
        PacketPlayOutEntityMetadata entityMetadataPacket = new PacketPlayOutEntityMetadata(player.getEntityId(),
                tempDataWatcher, true);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(entityMetadataPacket);
    }

    public boolean didPlayerMoveCursor() {
        return playerMovedCursor;
    }

    public Location getCursorCenterLocation() {
        return cursorCenterLocation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
