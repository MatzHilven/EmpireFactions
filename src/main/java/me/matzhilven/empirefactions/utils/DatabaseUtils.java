package me.matzhilven.empirefactions.utils;

import me.matzhilven.empirefactions.empire.faction.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DatabaseUtils {

    public static Location toLocation(String loc) {
        if (loc == null || loc.equals("")) return null;
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
        if (loc == null) return "";
        return loc.getWorld().getName() + ";" +
                loc.getX() + ";" +
                loc.getY() + ";" +
                loc.getZ() + ";" +
                loc.getYaw() + ";" +
                loc.getPitch();
    }

    public static String toString(List<Chunk> chunks) {
        StringBuilder str = new StringBuilder();

        for (Chunk chunk : chunks) str.append(chunk.getWorld().getName()).append(";").append(chunk.getX()).append(";").append(chunk.getZ()).append("|");

        return str.toString();
    }

    public static List<Chunk> toChunks(String str) {
        List<Chunk> chunks = new ArrayList<>();
        if (str.equals("")) return chunks;
        String[] splittedChunks = str.split("\\|");

        for (String splittedChunk : splittedChunks) {
            String[] split = splittedChunk.split(";");
            World world = Bukkit.getWorld(split[0]);
            chunks.add(world.getChunkAt(Integer.parseInt(split[1]), Integer.parseInt(split[2])));
        }

        return chunks;
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
