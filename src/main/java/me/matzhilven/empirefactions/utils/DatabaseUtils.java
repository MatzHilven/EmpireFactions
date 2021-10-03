package me.matzhilven.empirefactions.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DatabaseUtils {

    public static Location toLocation(String loc) {
        String[] split = loc.split(";");
        return new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5])
        );
    }

    public static String toString(Location loc) {
        return loc.getWorld().getName() + ";" +
                loc.getX() + ";" +
                loc.getY() + ";" +
                loc.getZ() + ";" +
                loc.getYaw() + ";" +
                loc.getPitch();
    }

    public static String to1String(List<UUID> uuids) {
        return uuids.stream().map(UUID::toString).collect(Collectors.joining(","));
    }

    public static List<UUID> toUUIDList(String uuids) {
        List<UUID> rUUIDs = new ArrayList<>();
        if (uuids == null || uuids.equals("")) return rUUIDs;

        for (String uuid : uuids.split(",")) {
            rUUIDs.add(UUID.fromString(uuid.trim()));
        }
        return rUUIDs;
    }


}
