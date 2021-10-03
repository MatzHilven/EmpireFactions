package me.matzhilven.empirefactions.empire;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.empire.rank.FactionRank;
import me.matzhilven.empirefactions.empire.region.Region;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

public class Empire {

    private final String name;
    private final UUID uuid;
    private final List<UUID> admins;
    private final List<UUID> moderators;
    private final List<UUID> members;
    private List<Core> cores;
    private List<Faction> subFactions;
    private UUID leader;
    private String description;
    private ChatColor color;

    private Region region;
    private Region jurisdiction;

    public Empire(String name, UUID uuid, List<UUID> admins, List<UUID> moderators, List<UUID> members, List<Core> cores,
                  List<Faction> subFactions, UUID leader, String description, ChatColor color, Region region,
                  Region jurisdiction) {
        this.name = name;
        this.uuid = uuid;
        this.admins = admins;
        this.moderators = moderators;
        this.members = members;
        this.cores = cores;
        this.subFactions = subFactions;
        this.leader = leader;
        this.description = description;
        this.color = color;
        this.region = region;
        this.jurisdiction = jurisdiction;
    }

    public Empire(String name, UUID leader) {
        this(name, UUID.randomUUID(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), leader, "", ChatColor.BLUE, new Region(), new Region());
    }

    public String getName() {
        return name;
    }

    public String getNameColored() {
        return StringUtils.colorize(color + name);
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
        String name = Bukkit.getPlayer(member).getName();
        getOnline().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player ->
                StringUtils.sendMessage(player, Messager.JOINED.replace("%player%", name)));
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
        EmpireFactions.getPlugin(EmpireFactions.class).getDb().removeFaction(subFaction);
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Region getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Region jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public boolean isIn(Player player) {
        UUID uuid = player.getUniqueId();
        return leader.toString().equals(uuid.toString()) || admins.contains(uuid) || moderators.contains(uuid) || members.contains(uuid);
    }

    public boolean isInBase(Player player) {
        return region.contains(player.getLocation().getX(), player.getLocation().getZ());
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
            if (!core.isAlive()) continue;
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

    public UUID getUniqueId() {
        return uuid;
    }

    public void addCore(Location location, CoreType coreType) {
        cores.add(new Core(coreType, location));

        EnderCrystal enderCrystal = (EnderCrystal) location.getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
        enderCrystal.setMetadata("empire", new FixedMetadataValue(EmpireFactions.getPlugin(EmpireFactions.class), getUniqueId().toString()));
    }

    public Core removeCore(Location location) {
        for (Core core : cores) {
            if (!core.isAlive()) continue;
            if (core.getLocation().distanceSquared(location) < 200) {
                core.setAlive(false);
                return core;
            }
        }
        return null;
    }

    public void setCores(List<Core> cores) {
        this.cores = cores;
    }

    public void setSubFactions(List<Faction> subFactions) {
        this.subFactions = subFactions;
    }

    public Optional<Faction> getFaction(Player player) {
        return subFactions.stream().filter(faction -> faction.isIn(player)).findFirst();
    }

    public Optional<Faction> getFaction(String name) {
        return subFactions.stream().filter(faction -> faction.getNormalizedName().equalsIgnoreCase(name)).findFirst();
    }

    public List<Player> getStaff() {
        List<Player> staff = new ArrayList<>();

        if (Bukkit.getPlayer(leader) != null) staff.add(Bukkit.getPlayer(leader));

        for (UUID uuid : admins) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) staff.add(player);
        }

        return staff;
    }
}
