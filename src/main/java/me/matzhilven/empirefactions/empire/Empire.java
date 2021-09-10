package me.matzhilven.empirefactions.empire;

import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.empire.rank.FactionRank;
import me.matzhilven.empirefactions.faction.Faction;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Empire {

    private final String name;
    private String description;
    private ChatColor color;

    private UUID leader;

    private List<UUID> admins;
    private List<UUID> moderators;
    private List<UUID> members;

    private List<Core> cores;
    private List<Faction> subFactions;

    private BoundingBox region;
    private BoundingBox jurisdiction;

    public Empire(String name, UUID leader) {
        this.name = name;
        this.description = "";
        this.color = ChatColor.BLUE;

        this.leader = leader;

        this.admins = new ArrayList<>();
        this.moderators = new ArrayList<>();
        this.members = new ArrayList<>();

        this.cores = Arrays.asList(new Core(CoreType.BASE), new Core(CoreType.OUTPOST),
                new Core(CoreType.OUTPOST), new Core(CoreType.OUTPOST));

        this.subFactions = new ArrayList<>();

        this.region = new BoundingBox();
        this.jurisdiction = new BoundingBox();
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description.equals("") ? "N/A" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public boolean addAdmin(UUID admin) {
        if (admins.size() == 5) return false;
        admins.add(admin);
        return true;
    }

    public void removeAdmin(UUID admin) {
        admins.remove(admin);
    }

    public List<UUID> getModerators() {
        return moderators;
    }

    public void addModerator(UUID moderator) {
        moderators.add(moderator);
    }

    public void removeModerator(UUID moderator) {
        moderators.remove(moderator);
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID member) {
        members.add(member);
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public List<Core> getCores() {
        return cores;
    }

    public List<Faction> getSubFactions() {
        return subFactions;
    }

    public void addSubFaction(Faction subFaction) {
        subFactions.add(subFaction);
    }

    public void removeSubFaction(Faction subFaction) {
        subFactions.remove(subFaction);
    }

    public BoundingBox getRegion() {
        return region;
    }

    public void setRegion(BoundingBox region) {
        this.region = region;
    }

    public BoundingBox getJurisdiction() {
        return jurisdiction;
    }

    public boolean isIn(Player player) {
        UUID uuid = player.getUniqueId();
        return leader == uuid || admins.contains(uuid) || moderators.contains(uuid) || members.contains(uuid);
    }

    public boolean isInBase(Player player) {
        return region.contains(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
    }

    public List<UUID> getAll() {
        List<UUID> all = new ArrayList<>();
        all.add(leader);
        all.addAll(admins);
        all.addAll(moderators);
        all.addAll(members);
        return all;
    }

    public List<UUID> getOnline() {
        return getAll().stream().filter(uuid -> Bukkit.getOfflinePlayer(uuid).isOnline()).collect(Collectors.toList());
    }

    public int getPower() {
        int power = 0;

        for (Core core : cores) {
            power += core.getPower();
        }

        return power;
    }

    public FactionRank getRank(Player player) {
        UUID uuid = player.getUniqueId();
        if (leader == uuid) return FactionRank.LEADER;
        if (admins.contains(uuid)) return FactionRank.ADMIN;
        if (moderators.contains(uuid)) return FactionRank.MODERATOR;
        return FactionRank.MEMBER;
    }

    public boolean promote(Player player, Player target) {
        FactionRank rank = getRank(target);
        switch (rank) {
            case LEADER:
            case ADMIN:
                StringUtils.sendMessage(player, Messager.PROMOTE_ADMIN);
                return false;
            case MODERATOR:
                if (admins.size() == 5) {
                    StringUtils.sendMessage(player, Messager.MAX_ADMINS);
                    return true;
                }
                moderators.remove(target.getUniqueId());
                admins.add(target.getUniqueId());
                return true;
            case MEMBER:
                members.remove(target.getUniqueId());
                moderators.add(target.getUniqueId());
        }
        return true;
    }

    public boolean demote(Player player, Player target) {
        FactionRank rank = getRank(target);
        switch (rank) {
            case LEADER:
            case ADMIN:
                if (getRank(player) != FactionRank.LEADER) {
                    StringUtils.sendMessage(player, Messager.INVALID_PERMISSION);
                    return false;
                }
                admins.remove(target.getUniqueId());
                moderators.add(target.getUniqueId());
                return true;
            case MODERATOR:
                moderators.remove(target.getUniqueId());
                members.add(target.getUniqueId());
                return true;
        }
        return true;
    }
}
