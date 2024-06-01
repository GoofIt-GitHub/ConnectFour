package com.matthew.plugin.connectfour.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class DirectionUtil {

    private DirectionUtil() {}

    public static void faceLocation(Player player, Location location) {
        Location playerLocation = player.getLocation();
        double dx = location.getX() - playerLocation.getX();
        double dy = location.getY() - (playerLocation.getY() + player.getEyeHeight());
        double dz = location.getZ() - playerLocation.getZ();

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;

        playerLocation.setYaw(yaw);
        playerLocation.setPitch(0);

        player.teleport(playerLocation);
    }


    public static BlockFace getPlayerFacingDirection(Location playerLoc) {
        float yaw = playerLoc.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw < 225) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }

    public static Vector getOffsetForDirection(BlockFace direction) {
        switch (direction) {
            case NORTH:
                return new Vector(0, 1, 3);
            case EAST:
                return new Vector(-3, 1, 0);
            case WEST:
                return new Vector(3, 1, 0);
            default: //SOUTH
                return new Vector(0, 1, -3);
        }
    }
}
