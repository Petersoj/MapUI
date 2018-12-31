package net.blockops.server.mapui.map;

import net.blockops.server.mapui.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MapUIPlayerController {

    private MapUI mapUI;
    private Player player;
    private final float centerPitch = 70f;
    private final float pitchBound = 20f;
    private final float yawBound = 40f;
    private float localYawBoundRight;
    private float localYawBoundLeft;
    private Location initialLocation;
    private float lastPitch;
    private float lastYaw;
    private ItemStack initialMainHandItemStack;
    private ItemStack initialOffHandItemStack;
    private float previousFlySpeed;
    private boolean wasFlying;
    private int playerMoveIssueCount;
    private boolean playerMovedCursor;

    public MapUIPlayerController(MapUI mapUI) {
        this.mapUI = mapUI;
        this.player = mapUI.getPlayer();
    }

    public void onUIOpen() {
        Location location = player.getLocation();
        location.setPitch(centerPitch);
        this.freezePlayer(location);

        localYawBoundLeft = LocationUtils.clampYaw(location.getYaw() - yawBound);
        localYawBoundRight = LocationUtils.clampYaw(location.getYaw() + yawBound);
        initialLocation = location;

        initialMainHandItemStack = player.getInventory().getItemInMainHand();
        initialOffHandItemStack = player.getInventory().getItemInOffHand();
        player.getInventory().setItemInMainHand(mapUI.getMapItem());
        player.getInventory().setItemInOffHand(null);
    }

    public void onUIUpdate() {
        Location location = player.getLocation();

        // Check if player moved at all (they may have deactivated flying causing gravity to move them!)
        if (location.getX() != initialLocation.getX() || location.getY() != initialLocation.getY()
                || location.getZ() != initialLocation.getZ()) {
            playerMoveIssueCount++;
            this.unFreezePlayer(); // reset flying speeds and such
            this.freezePlayer(initialLocation); // reset player location and flying speed/flying
        }

        // This ensures that the player is not involuntarily being moved indefinitely
        if (playerMoveIssueCount > 5) {
            mapUI.close();
            return;
        }

        float pitch = location.getPitch();
        float yaw = location.getYaw();
        if (pitch < centerPitch - pitchBound) {
            location.setPitch(centerPitch - pitchBound);
            player.teleport(location);
        }
        if (yaw > localYawBoundRight) {
            location.setYaw(localYawBoundRight);
            player.teleport(location);
        }
        if (yaw < localYawBoundLeft) {
            location.setYaw(localYawBoundLeft);
            player.teleport(location);
        }

        playerMovedCursor = pitch != lastPitch && yaw != lastYaw;
        lastPitch = pitch;
        lastYaw = yaw;
    }

    public void onUIClose() {
        this.unFreezePlayer();

        player.getInventory().setItemInMainHand(initialMainHandItemStack);
        player.getInventory().setItemInOffHand(initialOffHandItemStack);
    }

    public void freezePlayer(Location atLocation) {
        previousFlySpeed = player.getFlySpeed();
        wasFlying = player.isFlying();

        player.setFlySpeed(0f);
        player.setAllowFlight(true);
        player.setFlying(true);

        if (atLocation != null) {
            player.teleport(atLocation);
        }
    }

    public void unFreezePlayer() {
        player.setFlySpeed(previousFlySpeed);
        player.setFlying(wasFlying);
        player.setAllowFlight(wasFlying);
    }

    public boolean didPlayerMoveCursor() {
        return playerMovedCursor;
    }

    public Location getInitialLocation() {
        return initialLocation;
    }
}
