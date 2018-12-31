package net.blockops.server.mapui.util;

public class LocationUtils {

    public static float clampYaw(float yaw) {
        float angle = yaw % 360;
        return angle < 0 ? angle + 360 : angle;
    }

}
