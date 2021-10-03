package me.matzhilven.empirefactions.empire.region;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Region {

    private double minX;
    private double minZ;
    private double maxX;
    private double maxZ;

    public Region(double x1, double z1, double x2, double z2) {
        this.resize(x1, z1, x2, z2);
    }

    public Region() {
        this(0, 0, 0, 0);
    }

    public static Region of(String s) {
        Validate.notNull(s, "String is null!");

        String[] split = s.split(";");

        return new Region(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
    }

    public static Region of(Block corner1, Block corner2) {
        Validate.notNull(corner1, "Corner1 is null!");
        Validate.notNull(corner2, "Corner2 is null!");
        Validate.isTrue(Objects.equals(corner1.getWorld(), corner2.getWorld()), "Blocks from different worlds!");

        int x1 = corner1.getX();

        int z1 = corner1.getZ();
        int x2 = corner2.getX();

        int z2 = corner2.getZ();

        int minX = Math.min(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2) + 1;
        int maxZ = Math.max(z1, z2) + 1;

        return new Region(minX, minZ, maxX, maxZ);
    }


    public Region resize(double x1, double z1, double x2, double z2) {
        NumberConversions.checkFinite(x1, "x1 not finite");
        NumberConversions.checkFinite(z1, "z1 not finite");
        NumberConversions.checkFinite(x2, "x2 not finite");
        NumberConversions.checkFinite(z2, "z2 not finite");

        this.minX = Math.min(x1, x2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxZ = Math.max(z1, z2);
        return this;
    }

    public Vector getMin() {
        return new Vector(minX, 0, minZ);
    }

    public double getMinX() {
        return minX;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public Vector getMax() {
        return new Vector(maxX, 10000000, maxZ);
    }

    public double getWidthX() {
        return (this.maxX - this.minX);
    }

    public double getWidthZ() {
        return (this.maxZ - this.minZ);
    }

    public double getCenterX() {
        return (this.minX + this.getWidthX() * 0.5D);
    }

    public double getCenterZ() {
        return (this.minZ + this.getWidthZ() * 0.5D);
    }

    public Region copy(Region other) {
        Validate.notNull(other, "Other region is null!");
        return this.resize(other.getMinX(), other.getMinZ(), other.getMaxX(), other.getMaxZ());
    }

    public Region expand(double negativeX, double negativeZ, double positiveX, double positiveZ) {
        if (negativeX == 0.0D && negativeZ == 0.0D && positiveX == 0.0D && positiveZ == 0.0D) {
            return this;
        }
        double newMinX = this.minX - negativeX;
        double newMinZ = this.minZ - negativeZ;
        double newMaxX = this.maxX + positiveX;
        double newMaxZ = this.maxZ + positiveZ;

        // limit shrinking:
        if (newMinX > newMaxX) {
            double centerX = this.getCenterX();
            if (newMaxX >= centerX) {
                newMinX = newMaxX;
            } else if (newMinX <= centerX) {
                newMaxX = newMinX;
            } else {
                newMinX = centerX;
                newMaxX = centerX;
            }
        }
        if (newMinZ > newMaxZ) {
            double centerZ = this.getCenterZ();
            if (newMaxZ >= centerZ) {
                newMinZ = newMaxZ;
            } else if (newMinZ <= centerZ) {
                newMaxZ = newMinZ;
            } else {
                newMinZ = centerZ;
                newMaxZ = centerZ;
            }
        }
        return this.resize(newMinX, newMinZ, newMaxX, newMaxZ);
    }

    public Region expand(double x, double z) {
        return this.expand(x, z, x, z);
    }

    public Region expand(Vector expansion) {
        Validate.notNull(expansion, "Expansion is null!");
        double x = expansion.getX();
        double z = expansion.getZ();
        return this.expand(x, z, x, z);
    }

    public Region expand(double expansion) {
        return this.expand(expansion, expansion, expansion, expansion);
    }

    public Region expand(double dirX, double dirZ, double expansion) {
        if (expansion == 0.0D) return this;
        if (dirX == 0.0D && dirZ == 0.0D) return this;

        double negativeX = (dirX < 0.0D ? (-dirX * expansion) : 0.0D);
        double negativeZ = (dirZ < 0.0D ? (-dirZ * expansion) : 0.0D);
        double positiveX = (dirX > 0.0D ? (dirX * expansion) : 0.0D);
        double positiveZ = (dirZ > 0.0D ? (dirZ * expansion) : 0.0D);
        return this.expand(negativeX, negativeZ, positiveX, positiveZ);
    }


    public Region union(double posX, double posZ) {
        double newMinX = Math.min(this.minX, posX);
        double newMinZ = Math.min(this.minZ, posZ);
        double newMaxX = Math.max(this.maxX, posX);
        double newMaxZ = Math.max(this.maxZ, posZ);
        if (newMinX == this.minX && newMinZ == this.minZ && newMaxX == this.maxX && newMaxZ == this.maxZ) {
            return this;
        }
        return this.resize(newMinX, newMinZ, newMaxX, newMaxZ);
    }


    public Region union(Vector position) {
        Validate.notNull(position, "Position is null!");
        return this.union(position.getX(), position.getZ());
    }


    public Region union(Location position) {
        Validate.notNull(position, "Position is null!");
        return this.union(position.getX(), position.getZ());
    }

    public Region union(Region other) {
        Validate.notNull(other, "Other region is null!");
        if (this.contains(other)) return this;
        double newMinX = Math.min(this.minX, other.minX);
        double newMinZ = Math.min(this.minZ, other.minZ);
        double newMaxX = Math.max(this.maxX, other.maxX);
        double newMaxZ = Math.max(this.maxZ, other.maxZ);
        return this.resize(newMinX, newMinZ, newMaxX, newMaxZ);
    }

    public Region intersection(Region other) {
        Validate.notNull(other, "Other region is null!");
        Validate.isTrue(this.overlaps(other), "The regions do not overlap!");
        double newMinX = Math.max(this.minX, other.minX);
        double newMinZ = Math.max(this.minZ, other.minZ);
        double newMaxX = Math.min(this.maxX, other.maxX);
        double newMaxZ = Math.min(this.maxZ, other.maxZ);
        return this.resize(newMinX, newMinZ, newMaxX, newMaxZ);
    }

    public Region shift(double shiftX, double shiftZ) {
        if (shiftX == 0.0D && shiftZ == 0.0D) return this;
        return this.resize(this.minX + shiftX, this.minZ + shiftZ,
                this.maxX + shiftX, this.maxZ + shiftZ);
    }

    private boolean overlaps(double minX, double minZ, double maxX, double maxZ) {
        return this.minX < maxX && this.maxX > minX
                && this.minZ < maxZ && this.maxZ > minZ;
    }

    public boolean overlaps(Region other) {
        Validate.notNull(other, "Other region is null!");
        return this.overlaps(other.minX, other.minZ, other.maxX, other.maxZ);
    }

    public boolean overlaps(Vector min, Vector max) {
        Validate.notNull(min, "Min is null!");
        Validate.notNull(max, "Max is null!");
        double x1 = min.getX();
        double z1 = min.getZ();
        double x2 = max.getX();
        double z2 = max.getZ();
        return this.overlaps(Math.min(x1, x2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(z1, z2));
    }

    public boolean contains(double x, double z) {
        return x >= this.minX && x < this.maxX
                && z >= this.minZ && z < this.maxZ;
    }

    private boolean contains(double minX, double minZ, double maxX, double maxZ) {
        return this.minX <= minX && this.maxX >= maxX
                && this.minZ <= minZ && this.maxZ >= maxZ;
    }

    public boolean contains(Region other) {
        Validate.notNull(other, "Other region is null!");
        return this.contains(other.minX, other.minZ, other.maxX, other.maxZ);
    }


    public boolean contains(Vector min, Vector max) {
        Validate.notNull(min, "Min is null!");
        Validate.notNull(max, "Max is null!");
        double x1 = min.getX();
        double z1 = min.getZ();
        double x2 = max.getX();
        double z2 = max.getZ();
        return this.contains(Math.min(x1, x2), Math.min(z1, z2),
                Math.max(x1, x2), Math.max(z1, z2));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Region)) return false;
        Region other = (Region) obj;
        if (Double.doubleToLongBits(maxX) != Double.doubleToLongBits(other.maxX)) return false;
        if (Double.doubleToLongBits(maxZ) != Double.doubleToLongBits(other.maxZ)) return false;
        if (Double.doubleToLongBits(minX) != Double.doubleToLongBits(other.minX)) return false;
        return Double.doubleToLongBits(minZ) == Double.doubleToLongBits(other.minZ);
    }

    @Override
    public String toString() {
        return minX + ";" + minZ + ";" + maxX + ";" + maxZ;
    }


    @Override
    public Region clone() {
        try {
            return (Region) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
