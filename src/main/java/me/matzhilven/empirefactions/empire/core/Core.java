package me.matzhilven.empirefactions.empire.core;

import me.matzhilven.empirefactions.empire.region.Region;
import org.bukkit.Location;

import java.util.UUID;

public class Core {

    private final UUID uuid;
    private final CoreType coreType;
    private final Location location;

    private boolean alive;
    private Region region;

    public Core(CoreType coreType, Location location) {
        this(UUID.randomUUID(), coreType, location, true, new Region());
    }

    public Core(UUID uuid, CoreType coreType, Location location, boolean alive, Region region) {
        this.uuid = uuid;
        this.coreType = coreType;
        this.location = location;
        this.alive = alive;

        this.region = region;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public CoreType getCoreType() {
        return coreType;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getPower() {
        return coreType.getPower();
    }
}
