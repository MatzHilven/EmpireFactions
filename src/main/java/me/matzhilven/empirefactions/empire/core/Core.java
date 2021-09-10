package me.matzhilven.empirefactions.empire.core;

import org.bukkit.util.BoundingBox;

public class Core {

    private final CoreType coreType;
    private boolean alive;
    private BoundingBox region;

    public Core(CoreType coreType) {
        this(coreType, true);
    }

    public Core(CoreType coreType, boolean alive) {
        this.coreType = coreType;
        this.alive = alive;
    }

    public CoreType getCoreType() {
        return coreType;
    }

    public boolean isAlive() {
        return alive;
    }

    public BoundingBox getRegion() {
        return region;
    }

    public void setRegion(BoundingBox region) {
        this.region = region;
    }

    public int getPower() {
        return coreType.getPower();
    }
}
