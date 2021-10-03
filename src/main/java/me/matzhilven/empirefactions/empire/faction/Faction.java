package me.matzhilven.empirefactions.empire.faction;


import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Faction {

    private final UUID empireUUID;

    private final UUID uuid;
    private final String name;
    private String description;

    private UUID leader;
    private List<UUID> members;
    private List<UUID> invited;

    private List<Chunk> claimed;

    public Faction(String name, UUID leader, UUID empireUUID) {
        this(empireUUID, UUID.randomUUID(), name, "N/A", leader, new ArrayList<>(), new ArrayList<>());
    }

    public Faction(UUID empireUUID, UUID uuid, String name, String description, UUID leader, List<UUID> members, List<Chunk> claimed) {
        this.empireUUID = empireUUID;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.description = description;
        this.leader = leader;
        this.members = members;
        this.invited = new ArrayList<>();
        this.claimed = claimed;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getNameColored() {
        Optional<Empire> optionalEmpire = EmpireFactions.getPlugin(EmpireFactions.class).getEmpireManager().getEmpire(uuid);
        return optionalEmpire.map(empire -> empire.getColor() + name).orElse(name);
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

    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<UUID> members) {
        this.members = members;
    }

    public List<Chunk> getClaimed() {
        return claimed;
    }

    public void setClaimed(List<Chunk> claimed) {
        this.claimed = claimed;
    }

    public int getChunks() {
        return 1;
    }

    public boolean addMember(Player player) {
        UUID member = player.getUniqueId();
        if (members.contains(member)) return false;
        if (members.size() == EmpireFactions.getPlugin(EmpireFactions.class).getConfig().getInt("max-players-in-faction")) return false;

        invited.remove(member);
        members.add(member);


        getOnline().forEach(player1 -> StringUtils.sendMessage(player1, Messager.JOINED_FACTION.replace("%player%", player.getName())));
        return true;
    }

    public boolean isIn(Player player) {
        UUID uuid = player.getUniqueId();
        if (leader.toString().equals(uuid.toString())) return true;
        return members.contains(uuid);
    }

    public boolean isLeader(Player player) {
        return player.getUniqueId().toString().equals(leader.toString());
    }

    public UUID getEmpireID() {
        return empireUUID;
    }

    public void addInvite(Player player) {
        invited.add(player.getUniqueId());
    }

    public boolean isInvited(Player player) {
        return invited.contains(player.getUniqueId());
    }

    public String getNormalizedName() {
        return StringUtils.removeColorCodes(getName());
    }

    public List<UUID> getAll() {
        List<UUID> all = new ArrayList<>();
        all.add(leader);
        all.addAll(members);
        return all;
    }

    public List<Player> getOnline() {
        return getAll().stream().filter(uuid -> Bukkit.getOfflinePlayer(uuid).isOnline()).map(Bukkit::getPlayer).collect(Collectors.toList());
    }
}
