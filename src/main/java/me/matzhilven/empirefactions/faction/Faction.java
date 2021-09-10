package me.matzhilven.empirefactions.faction;

import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.UUID;

public class Faction {

    private final String name;
    private String description;

    private UUID leader;
    private ArrayList<UUID> members;

    private BoundingBox claimed;

    public Faction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public ArrayList<UUID> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<UUID> members) {
        this.members = members;
    }

    public BoundingBox getClaimed() {
        return claimed;
    }

    public void setClaimed(BoundingBox claimed) {
        this.claimed = claimed;
    }
}
