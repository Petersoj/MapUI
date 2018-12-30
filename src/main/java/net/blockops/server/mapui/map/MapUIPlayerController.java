package net.blockops.server.mapui.map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MapUIPlayerController {

    private Player player;
    private final float centerPitch = 70f;
    private float centerYaw;
    private float previousFlySpeed;
    private boolean wasFlying;
    private int playerMoveIssueCount;

    public MapUIPlayerController(Player player) {
        this.player = player;
    }

    public void onOpen() {
        Location location = player.getLocation();

        location.setPitch(centerPitch);
        this.centerYaw = location.getYaw();

        this.freezePlayer(location);
    }

    public void update() {
        // Just use player.getLocation() here!

        // Make sure player is in Viewing bounds (not above less than 50 pitch & 40 on each side yaw)
        // Make sure player is not moving location. If they did, then TP them back.
        // Check if player is not flying anymore, if so, set them flying
    }

    public void freezePlayer(Location atLocation) {
        this.previousFlySpeed = player.getFlySpeed();
        this.wasFlying = player.isFlying();

        this.player.setFlySpeed(0f);
        this.player.setAllowFlight(true);
        this.player.setFlying(true);

        if (atLocation != null) {
            this.player.teleport(atLocation);
        }
    }

    public void unFreezePlayer() {
        this.player.setFlySpeed(previousFlySpeed);
        this.player.setFlying(wasFlying);
        this.player.setAllowFlight(wasFlying);
    }
}
