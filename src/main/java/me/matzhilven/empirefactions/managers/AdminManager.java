package me.matzhilven.empirefactions.managers;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdminManager {

    private final Set<UUID> inAdminMode;

    public AdminManager() {
        this.inAdminMode = new HashSet<>();
    }

    public void addPlayer(Player player) {
        inAdminMode.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        inAdminMode.remove(player.getUniqueId());
    }

    public boolean isIn(Player player) {
        return inAdminMode.contains(player.getUniqueId());
    }
}
