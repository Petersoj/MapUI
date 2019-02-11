package net.blockops.server.mapui.map;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

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
    private boolean playerDirectionChanged;
    private int cursorX;
    private int cursorY;
    private static DataWatcherObject<Byte> baseDataWatcherObject = new DataWatcherObject<>(0, DataWatcherRegistry.a);
    private DataWatcher temporaryDataWatcher;
    private DataWatcher entityPlayerDataWatcher;
    private Scoreboard collisionScoreboard;
    private Team collisionRuleTeam;

    public PlayerController(MapUI mapUI) {
        this.mapUI = mapUI;
        this.player = mapUI.getPlayer();

        this.temporaryDataWatcher = new DataWatcher(((CraftPlayer) player).getHandle());
        this.entityPlayerDataWatcher = ((CraftPlayer) player).getHandle().getDataWatcher();
    }

    public void init() {
        temporaryDataWatcher.register(baseDataWatcherObject, (byte) 0);
    }

    public void onUIOpen() {
        Location location = player.getLocation();
        this.previousLocation = location;

        cursorCenterLocation = location.clone();
        cursorCenterLocation.setYaw(centerYaw);
        cursorCenterLocation.setPitch(centerPitch);
        this.freezePlayer(cursorCenterLocation);

        this.calculateCursorPosition(cursorCenterLocation.getPitch(), cursorCenterLocation.getYaw());
        playerDirectionChanged = true;

        this.setClientCollidable(false);

        initialMainHandItemStack = player.getInventory().getItemInMainHand();
        initialOffHandItemStack = player.getInventory().getItemInOffHand();
        player.getInventory().setItemInMainHand(mapUI.getMapItem());
        player.getInventory().setItemInOffHand(null);

        this.setClientInvisible(true);
    }

    public void update() {
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
        if (playerMoveIssueCount >= 5) {
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
            playerDirectionChanged = pitch != lastPitch || yaw != lastYaw;
            lastPitch = pitch;
            lastYaw = yaw;

            if (playerDirectionChanged) {
                this.calculateCursorPosition(pitch, yaw);
            }
        }
    }

    public void onUIClose() {
        this.unFreezePlayer();
        player.teleport(previousLocation);
        playerMoveIssueCount = 0;

        this.setClientCollidable(true);

        player.getInventory().setItemInMainHand(initialMainHandItemStack);
        player.getInventory().setItemInOffHand(initialOffHandItemStack);

        this.setClientInvisible(false);
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
        cursorX = (int) (yaw / (centerYaw + yawBound) * 128);
        cursorY = (int) ((pitch - 50) / (centerPitch + pitchBound - 50) * 128); // -50 because min pitch is 50
    }

    public void teleportToCursorLocation(int x, int y) {
        Validate.isTrue(x >= 0 && y >= 0 && x < 128 && y < 128, "Out of bounds X or Y!");

        Location location = player.getLocation();
        this.clampToBounds(location); // Just in case

        float pitch = ((float) (y / 128)) * (centerPitch + pitchBound - 50) + 50; // -50 because min pitch is 50
        float yaw = ((float) (x / 128)) * (centerYaw + yawBound);

        location.setPitch(pitch);
        location.setYaw(yaw);

        player.teleport(location);
    }

    private void freezePlayer(Location atLocation) {
        if (player.getFlySpeed() == 0) {
            previousFlySpeed = defaultFlySpeed;
        } else if (player.getFlySpeed() != defaultFlySpeed) {
            previousFlySpeed = player.getFlySpeed();
        }
        wasFlying = player.isFlying();
        wasFlightAllowed = player.getAllowFlight();

        player.setVelocity(new Vector(0, 0, 0));
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
        byte newData;

        if (invisible) {
            newData = (byte) (previousData | 0b0010_0000);
        } else {
            newData = (byte) (previousData & ~0b0010_0000);
        }
        temporaryDataWatcher.set(baseDataWatcherObject, newData);

        PacketPlayOutEntityMetadata entityMetadataPacket = new PacketPlayOutEntityMetadata(player.getEntityId(),
                temporaryDataWatcher, true);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(entityMetadataPacket);
    }

    private void setClientCollidable(boolean collidable) {
        if (collisionScoreboard == null) { // First time run
            collisionScoreboard = player.getScoreboard();
            if (collisionScoreboard == null) {
                collisionScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(collisionScoreboard);
            } else { // Player already has a scoreboard
                for (Team team : collisionScoreboard.getTeams()) {
                    if (team.getOption(Team.Option.COLLISION_RULE) == Team.OptionStatus.NEVER) {
                        return; // Collision rule never has already been set by something else so don't mess with it
                    }
                }
            }
        }
        if (collisionRuleTeam == null) {
            final String collisionTeamName = "no_collision";
            collisionRuleTeam = collisionScoreboard.getTeam(collisionTeamName);
            if (collisionRuleTeam == null) {
                collisionRuleTeam = collisionScoreboard.registerNewTeam(collisionTeamName);
            }
        }

        // Set collision rule no matter what because collision rule has not been set by another scoreboard/plugin
        if (collidable) {
            collisionRuleTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
        } else {
            collisionRuleTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    public boolean didPlayerDirectionChange() {
        return playerDirectionChanged;
    }

    public Location getCursorCenterLocation() {
        return cursorCenterLocation;
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }
}
