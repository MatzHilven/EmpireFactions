package me.matzhilven.empirefactions.empire.faction;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.data.PlayerData;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Faction {

    private final UUID empireUUID;

    private final UUID uuid;
    private final List<UUID> members;
    private final List<UUID> invited;
    private final List<UUID> allies;
    private final List<UUID> allowedAllies;
    private final List<UUID> inChat;
    private final List<Player> inChatSpy;
    private final Timestamp founded;
    private String name;
    private int power;
    private String description;
    private UUID leader;
    private List<Chunk> claimed;
    private long balance;
    private int kills;
    private int deaths;
    private String tag;
    private String title;
    private Location home;
    private boolean isOpen;
    private boolean isMuted;

    public Faction(String name, UUID leader, UUID empireUUID) {
        this(empireUUID, UUID.randomUUID(), name, "N/A", leader, new ArrayList<>(), new ArrayList<>(),
                0, 0, 0, new ArrayList<>(), new ArrayList<>(), Timestamp.valueOf(LocalDateTime.now()),
                false, null, "", "");
    }

    public Faction(UUID empireUUID, UUID uuid, String name, String description, UUID leader, List<UUID> members,
                   List<Chunk> claimed, long balance, int kills, int deaths, List<UUID> allies,
                   List<UUID> allowedAllies, Timestamp founded, boolean isOpen, Location home, String tag,
                   String title) {
        this.empireUUID = empireUUID;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.description = description;
        this.leader = leader;
        this.members = members;
        this.power = 0;
        this.balance = balance;
        this.kills = kills;
        this.deaths = deaths;
        this.allowedAllies = allowedAllies;
        this.allies = allies;
        this.invited = new ArrayList<>();
        this.claimed = claimed;
        this.inChat = new ArrayList<>();
        this.founded = founded;
        this.isOpen = isOpen;
        this.home = home;
        this.tag = tag;
        this.title = title;

        this.isMuted = false;
        this.inChatSpy = new ArrayList<>();
        setPower();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        members.add(this.leader);
        this.leader = leader;
        members.remove(leader);
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<Chunk> getClaimed() {
        return claimed;
    }

    public void setClaimed(List<Chunk> claimed) {
        this.claimed = claimed;
    }

    public int getAmountClaimed() {
        return claimed.size();
    }

    public boolean addMember(Player player) {
        UUID member = player.getUniqueId();
        if (members.contains(member)) return true;
        if (members.size() == EmpireFactions.getPlugin(EmpireFactions.class).getConfig().getInt("max-players-in-faction"))
            return false;

        invited.remove(member);
        members.add(member);

        getOnline().forEach(player1 -> StringUtils.sendMessage(player1, Messager.JOINED_FACTION.replace("%player%", player.getName())));
        setPower();
        return true;
    }

    public boolean isIn(Player player) {
        return isIn(player.getUniqueId());
    }

    public boolean isIn(UUID uuid) {
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

    public void addChunk(Chunk chunk) {
        claimed.add(chunk);
    }

    public void removeChunk(Chunk chunk) {
        claimed.remove(chunk);
    }

    public boolean isClaimed(Chunk chunk) {
        return claimed.stream().anyMatch(chunk1 -> chunk.getWorld().getChunkAt(chunk1.getX(), chunk1.getZ()).equals(chunk));
    }

    public boolean sendMessage(Player sender, String msg) {
        if (isMuted()) return false;
        String message = Messager.CHAT_FORMAT.replace("%sender%", sender.getDisplayName())
                .replace("%rank%", isLeader(sender) ? "Leader" : "Member").replace("%message%", msg);
        getOnline().forEach(player -> StringUtils.sendMessage(player, message));
        for (Player player : inChatSpy) {
            if (!player.isOnline()) {
                removeFromChatSpy(player);
                continue;
            }
            StringUtils.sendMessage(player, message);
        }
        return true;
    }

    public int getPower() {
        return power;
    }

    public void setPower() {
        int power = 0;
        for (UUID uuid : getAll()) {
            if (Bukkit.getPlayer(uuid) != null) {
                power += PlayerData.get(uuid).getPower();
                continue;
            }
            try {
                PreparedStatement ps = EmpireFactions.getPlugin(EmpireFactions.class).getDb().getSQLConnection().prepareStatement("SELECT power FROM players WHERE uuid=?");
                ps.setString(1, uuid.toString());

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    power += rs.getInt("power");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        this.power = power;
    }

    public long getBalance() {
        return balance;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void addMoney(long balance) {
        this.balance += balance;
    }

    public List<UUID> getAllies() {
        return allies;
    }

    public List<UUID> getAllowedAllies() {
        return allowedAllies;
    }

    public boolean isInChat(Player player) {
        return inChat.contains(player.getUniqueId());
    }

    public void removeFromChat(Player player) {
        inChat.remove(player.getUniqueId());
    }

    public void addToChat(Player player) {
        inChat.add(player.getUniqueId());
    }

    public boolean isInChatSpy(Player player) {
        return inChatSpy.contains(player);
    }

    public void removeFromChatSpy(Player player) {
        inChatSpy.remove(player);
    }

    public void addToChatSpy(Player player) {
        inChatSpy.add(player);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getRank(UUID uuid) {
        return leader.toString().equals(uuid.toString()) ? "Leader" : "Member";
    }

    public Timestamp getFounded() {
        return founded;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean removeMoney(long amount) {
        if (balance == 0 || balance < amount) return false;
        balance -= amount;
        return true;
    }

    public void clearChunks() {
        claimed.clear();
    }

    public boolean addAlly(Faction faction) {
        if (allies.contains(faction.getUniqueId())) return false;
        allies.add(faction.getUniqueId());
        return true;
    }

    public void removeAlly(Faction faction) {
        allies.remove(faction.getUniqueId());
        allowedAllies.remove(faction.getUniqueId());
    }

    public boolean addAllowedAlly(Faction faction) {
        if (allowedAllies.contains(faction.getUniqueId())) return false;
        allowedAllies.add(faction.getUniqueId());
        allies.remove(faction.getUniqueId());
        return true;
    }

    public boolean isAlly(Faction faction) {
        return allies.contains(faction.getUniqueId()) || allowedAllies.contains(faction.getUniqueId());
    }

    public boolean isAllowedAlly(Faction faction) {
        return allowedAllies.contains(faction.getUniqueId());
    }

    public void kick(Player target) {
        members.remove(target.getUniqueId());
        setPower();
    }

    public boolean canClaim() {
        return power > claimed.size();
    }

    public boolean isRaidable() {
        return power < claimed.size();
    }

}
