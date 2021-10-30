package me.matzhilven.empirefactions.empire;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.empire.rank.EmpireRank;
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

    private final int id;
    private final UUID uuid;
    private final List<UUID> admins;
    private final List<UUID> moderators;
    private final List<UUID> members;
    private String name;
    private Location baseCenter;
    private Location jurisdictionCenter;
    private List<Core> cores;
    private List<Faction> subFactions;
    private UUID leader;
    private String description;
    private ChatColor color;

    private Region baseRegion;
    private Region jurisdictionRegion;

    public Empire(String name, UUID uuid, List<UUID> admins, List<UUID> moderators, List<UUID> members, Location baseCenter,
                  Location jurisdictionCenter, List<Core> cores, List<Faction> subFactions, UUID leader, String description,
                  ChatColor color, Region region, Region jurisdiction, int id) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.admins = admins;
        this.moderators = moderators;
        this.members = members;
        this.baseCenter = baseCenter;
        this.jurisdictionCenter = jurisdictionCenter;
        this.cores = cores;
        this.subFactions = subFactions;
        this.leader = leader;
        this.description = description;
        this.color = color;
        this.baseRegion = region;
        this.jurisdictionRegion = jurisdiction;

        if (id == 0) return;
        if (baseCenter != null) {
            int baseSize = EmpireFactions.getPlugin(EmpireFactions.class).getConfig().getInt("region-size." + id + ".base");
            setBaseRegion(new Region(baseCenter.getX() - baseSize, baseCenter.getZ() - baseSize,
                    baseCenter.getX() + baseSize, baseCenter.getZ() + baseSize));
        }

        if (jurisdictionCenter != null) {
            int jurisdictionSize = EmpireFactions.getPlugin(EmpireFactions.class).getConfig().getInt("region-size." + id + ".jurisdiction");
            setJurisdictionRegion(new Region(jurisdictionCenter.getX() - jurisdictionSize, jurisdictionCenter.getZ() - jurisdictionSize,
                    jurisdictionCenter.getX() + jurisdictionSize, jurisdictionCenter.getZ() + jurisdictionSize));
        }
    }

    public Empire(String name, UUID leader, Location baseCenter, Location jurisdictionCenter, int id) {
        this(name, UUID.randomUUID(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), baseCenter, jurisdictionCenter,
                new ArrayList<>(), new ArrayList<>(), leader, "", ChatColor.BLUE, new Region(), new Region(), id);
    }

    public void setName(String name) {
        this.name = name;
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
        admins.add(this.leader);

        this.leader = leader;

        members.remove(leader);
        moderators.remove(leader);
        admins.remove(leader);
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public List<UUID> getModerators() {
        return moderators;
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

    public List<Core> getCores() {
        return cores;
    }

    public void setCores(List<Core> cores) {
        this.cores = cores;
    }

    public List<Faction> getSubFactions() {
        return subFactions;
    }

    public void setSubFactions(List<Faction> subFactions) {
        this.subFactions = subFactions;
    }

    public void addSubFaction(Faction subFaction) {
        subFactions.add(subFaction);
    }

    public void removeSubFaction(Faction subFaction) {
        subFactions.remove(subFaction);
        EmpireFactions.getPlugin(EmpireFactions.class).getDb().removeFaction(subFaction);
    }

    public Region getBaseRegion() {
        return baseRegion;
    }

    public void setBaseRegion(Region baseRegion) {
        this.baseRegion = baseRegion;
    }

    public Region getJurisdictionRegion() {
        return jurisdictionRegion;
    }

    public void setJurisdictionRegion(Region jurisdictionRegion) {
        this.jurisdictionRegion = jurisdictionRegion;
    }

    public boolean isIn(Player player) {
        UUID uuid = player.getUniqueId();
        return leader.toString().equals(uuid.toString()) || admins.contains(uuid) || moderators.contains(uuid) || members.contains(uuid);
    }

    public boolean isInBase(Player player) {
        return baseRegion.contains(player.getLocation().getX(), player.getLocation().getZ());
    }

    public Location getBaseCenter() {
        return baseCenter;
    }

    public void setBaseCenter(Location baseCenter) {
        this.baseCenter = baseCenter;
        int baseSize = EmpireFactions.getPlugin(EmpireFactions.class).getConfig().getInt("region-size." + id + ".base");
        setBaseRegion(new Region(baseCenter.getX() - baseSize, baseCenter.getZ() - baseSize,
                baseCenter.getX() + baseSize, baseCenter.getZ() + baseSize));
    }

    public Location getJurisdictionCenter() {
        return jurisdictionCenter;
    }

    public void setJurisdictionCenter(Location jurisdictionCenter) {
        this.jurisdictionCenter = jurisdictionCenter;
        int jurisdictionSize = EmpireFactions.getPlugin(EmpireFactions.class).getConfig().getInt("region-size." + id + ".jurisdiction");
        setJurisdictionRegion(new Region(jurisdictionCenter.getX() - jurisdictionSize, jurisdictionCenter.getZ() - jurisdictionSize,
                jurisdictionCenter.getX() + jurisdictionSize, jurisdictionCenter.getZ() + jurisdictionSize));
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

    public EmpireRank getRank(Player player) {
        UUID uuid = player.getUniqueId();
        if (leader.toString().equals(uuid.toString())) return EmpireRank.LEADER;
        if (admins.contains(uuid)) return EmpireRank.ADMIN;
        if (moderators.contains(uuid)) return EmpireRank.MODERATOR;
        return EmpireRank.MEMBER;
    }

    public boolean promote(Player player, Player target) {
        EmpireRank rank = getRank(target);
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
        EmpireRank rank = getRank(target);
        switch (rank) {
            case LEADER:
            case ADMIN:
                if (getRank(player) != EmpireRank.LEADER) {
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

    public Optional<Faction> getFaction(Player player) {
        return subFactions.stream().filter(faction -> faction.isIn(player)).findFirst();
    }

    public Optional<Faction> byPlayerUUID(UUID playerUUID) {
        return subFactions.stream().filter(faction -> faction.isIn(playerUUID)).findFirst();
    }

    public Optional<Faction> byFactionUUID(UUID factionUUID) {
        return subFactions.stream().filter(faction -> faction.getUniqueId().toString().equals(factionUUID.toString())).findFirst();
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

    public boolean isInJurisdiction(Player player) {
        return jurisdictionRegion.contains(player.getLocation().getX(), player.getLocation().getZ());
    }

    public int getKills() {
        return subFactions.stream().map(Faction::getKills).mapToInt(Integer::intValue).sum();
    }

    public int getDeaths() {
        return subFactions.stream().map(Faction::getDeaths).mapToInt(Integer::intValue).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empire empire = (Empire) o;
        return Objects.equals(uuid, empire.uuid);
    }
}
