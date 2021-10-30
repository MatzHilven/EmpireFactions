package me.matzhilven.empirefactions.data;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.data.mysql.MySQLPlayerData;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.empire.region.Region;
import me.matzhilven.empirefactions.utils.DatabaseUtils;
import me.matzhilven.empirefactions.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public abstract class Database {

    private final EmpireFactions main;
    public Connection connection;

    public Database(EmpireFactions instance) {
        main = instance;

        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            saveEmpires();
            savePlayers();
            setFactionsPower();
        }, 20L * 60L * 2L, 20L * 60L * 2L);
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void loadEmpires() {
        Bukkit.getScheduler().runTask(main, () -> {
            PreparedStatement ps = null;
            ResultSet rs = null;
            Connection connection = getSQLConnection();

            if (connection == null) {
                Logger.severe("connection to database not found!");
                return;
            }

            try {
                ps = connection.prepareStatement("SELECT * FROM empires");
                rs = ps.executeQuery();

                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));

                    List<Core> cores = new ArrayList<>();
                    List<Faction> factions = new ArrayList<>();

                    Empire empire = new Empire(
                            rs.getString("name"),
                            uuid,
                            DatabaseUtils.toUUIDList(rs.getString("admins")),
                            DatabaseUtils.toUUIDList(rs.getString("moderators")),
                            DatabaseUtils.toUUIDList(rs.getString("members")),
                            DatabaseUtils.toLocation(rs.getString("baseCenter")),
                            DatabaseUtils.toLocation(rs.getString("jurisdictionCenter")),
                            cores,
                            factions,
                            UUID.fromString(rs.getString("leader")),
                            rs.getString("description"),
                            getColor(rs.getString("color")),
                            Region.of(rs.getString("region")),
                            Region.of(rs.getString("jurisdiction")),
                            rs.getInt("id")
                    );

                    ps = connection.prepareStatement("SELECT * FROM factions WHERE empire_uuid=?");
                    ps.setString(1, empire.getUniqueId().toString());
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        factions.add(new Faction(
                                uuid,
                                UUID.fromString(rs.getString("uuid")),
                                rs.getString("name"),
                                rs.getString("description"),
                                UUID.fromString(rs.getString("leader")),
                                DatabaseUtils.toUUIDList(rs.getString("members")),
                                DatabaseUtils.toChunks(rs.getString("claimed")),
                                rs.getLong("balance"),
                                rs.getInt("kills"),
                                rs.getInt("deaths"),
                                DatabaseUtils.toUUIDList(rs.getString("allies")),
                                DatabaseUtils.toUUIDList(rs.getString("allowedAllies")),
                                rs.getTimestamp("founded"),
                                rs.getBoolean("isOpen"),
                                DatabaseUtils.toLocation(rs.getString("home")),
                                rs.getString("tag"),
                                rs.getString("title")
                                ));
                    }

                    ps = connection.prepareStatement("SELECT * FROM cores WHERE empire_uuid=?");
                    ps.setString(1, empire.getUniqueId().toString());
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        cores.add(new Core(
                                UUID.fromString(rs.getString("uuid")),
                                CoreType.valueOf(rs.getString("type")),
                                DatabaseUtils.toLocation(rs.getString("location")),
                                rs.getBoolean("alive"),
                                Region.of(rs.getString("region"))
                        ));
                    }

                    empire.setSubFactions(factions);
                    empire.setCores(cores);

                    this.main.getEmpireManager().getEmpires().add(empire);
                }
            } catch (SQLException e) {
                main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", e);
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (rs != null) rs.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveEmpire(Empire empire) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = getSQLConnection();

            if (connection == null) {
                Logger.severe("connection to database not found!");
                return;
            }

            try {
                ps = getSQLConnection().prepareStatement("SELECT * FROM empires WHERE uuid=?");
                ps.setString(1, empire.getUniqueId().toString());
                ResultSet results = ps.executeQuery();
                if (!results.next()) {
                    addEmpire(empire);
                    return;
                }

                ps = connection.prepareStatement("UPDATE empires SET name=?, description=?, leader=?, admins=?, " +
                        "moderators=?, members=?, baseCenter=?, jurisdictionCenter=?, color=?, region=?, jurisdiction=? WHERE uuid=?");
                ps.setString(1, empire.getName());
                ps.setString(2, empire.getDescription());
                ps.setString(3, empire.getLeader().toString());
                ps.setString(4, DatabaseUtils.to1String(empire.getAdmins()));
                ps.setString(5, DatabaseUtils.to1String(empire.getModerators()));
                ps.setString(6, DatabaseUtils.to1String(empire.getMembers()));
                ps.setString(7, DatabaseUtils.toString(empire.getBaseCenter()));
                ps.setString(8, DatabaseUtils.toString(empire.getJurisdictionCenter()));
                ps.setString(9, empire.getColor().asBungee().getName().toUpperCase());
                ps.setString(10, empire.getBaseRegion().toString());
                ps.setString(11, empire.getJurisdictionRegion().toString());
                ps.setString(12, empire.getUniqueId().toString());

                ps.executeUpdate();

                for (Faction faction : empire.getSubFactions()) {
                    ps = connection.prepareStatement("INSERT INTO factions (empire_uuid, uuid, name, description," +
                            " leader, members, claimed, balance, kills, deaths, allies, allowedAllies, isOpen, home," +
                            " tag, title, founded) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE" +
                            " name=?, description=?, leader=?, members=?, claimed=?, balance=?, kills=?, deaths=?, allies=?, allowedAllies=?," +
                            " isOpen=?, home=?, tag=?, title=?, founded=?" );

                    ps.setString(1, empire.getUniqueId().toString());
                    ps.setString(2, faction.getUniqueId().toString());
                    ps.setString(3, faction.getName());
                    ps.setString(4, faction.getDescription());
                    ps.setString(5, faction.getLeader().toString());
                    ps.setString(6, DatabaseUtils.to1String(faction.getMembers()));
                    ps.setString(7, DatabaseUtils.toString(faction.getClaimed()));
                    ps.setLong(8, faction.getBalance());
                    ps.setInt(9, faction.getKills());
                    ps.setLong(10, faction.getDeaths());
                    ps.setString(11, DatabaseUtils.to1String(faction.getAllies()));
                    ps.setString(12, DatabaseUtils.to1String(faction.getAllowedAllies()));
                    ps.setBoolean(13, faction.isOpen());
                    ps.setString(14, DatabaseUtils.toString(faction.getHome()));
                    ps.setString(15, faction.getTag());
                    ps.setString(16, faction.getTitle());
                    ps.setTimestamp(17, faction.getFounded());

                    ps.setString(18, faction.getName());
                    ps.setString(19, faction.getDescription());
                    ps.setString(20, faction.getLeader().toString());
                    ps.setString(21, DatabaseUtils.to1String(faction.getMembers()));
                    ps.setString(22, DatabaseUtils.toString(faction.getClaimed()));
                    ps.setLong(23, faction.getBalance());
                    ps.setInt(24, faction.getKills());
                    ps.setLong(25, faction.getDeaths());
                    ps.setString(26, DatabaseUtils.to1String(faction.getAllies()));
                    ps.setString(27, DatabaseUtils.to1String(faction.getAllowedAllies()));
                    ps.setBoolean(28, faction.isOpen());
                    ps.setString(29, DatabaseUtils.toString(faction.getHome()));
                    ps.setString(30, faction.getTag());
                    ps.setString(31, faction.getTitle());
                    ps.setTimestamp(32, faction.getFounded());

                    ps.executeUpdate();
                }

                for (Core core : empire.getCores()) {
                    ps = connection.prepareStatement("INSERT INTO cores (empire_uuid, uuid, type, alive, location, region) " +
                            "VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE type=?, alive=?, location=?, region=?");

                    ps.setString(1, empire.getUniqueId().toString());
                    ps.setString(2, core.getUniqueId().toString());
                    ps.setString(3, core.getCoreType().getName());
                    ps.setBoolean(4, core.isAlive());
                    ps.setString(5, (DatabaseUtils.toString(core.getLocation())));
                    ps.setString(6, core.getRegion().toString());
                    ps.setString(7, core.getCoreType().getName());
                    ps.setBoolean(8, core.isAlive());
                    ps.setString(9, (DatabaseUtils.toString(core.getLocation())));
                    ps.setString(10, core.getRegion().toString());

                    ps.executeUpdate();
                }

            } catch (SQLException e) {
                main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", e);
            } finally {
                try {
                    if (ps != null) ps.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void addEmpire(Empire empire) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = getSQLConnection();

            try {
                ps = connection.prepareStatement("INSERT INTO empires (uuid, name, description, leader, admins," +
                        " moderators, members, baseCenter, jurisdictionCenter, color, region, jurisdiction)" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

                ps.setString(1, empire.getUniqueId().toString());
                ps.setString(2, empire.getName());
                ps.setString(3, empire.getDescription());
                ps.setString(4, empire.getLeader().toString());
                ps.setString(5, DatabaseUtils.to1String(empire.getAdmins()));
                ps.setString(6, DatabaseUtils.to1String(empire.getModerators()));
                ps.setString(7, DatabaseUtils.to1String(empire.getMembers()));
                ps.setString(8, DatabaseUtils.toString(empire.getBaseCenter()));
                ps.setString(9, DatabaseUtils.toString(empire.getJurisdictionCenter()));
                ps.setString(10, empire.getColor().asBungee().getName().toUpperCase());
                ps.setString(11, empire.getBaseRegion().toString());
                ps.setString(12, empire.getJurisdictionRegion().toString());

                ps.executeUpdate();

                for (Faction faction : empire.getSubFactions()) {
                    ps = connection.prepareStatement("INSERT INTO factions (empire_uuid, uuid, name, description," +
                            " leader, members, claimed, balance, kills, deaths, allies, allowedAllies, isOpen, home," +
                            " tag, title, founded) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                    ps.setString(1, empire.getUniqueId().toString());
                    ps.setString(2, faction.getUniqueId().toString());
                    ps.setString(3, faction.getName());
                    ps.setString(4, faction.getDescription());
                    ps.setString(5, faction.getLeader().toString());
                    ps.setString(6, DatabaseUtils.to1String(faction.getMembers()));
                    ps.setString(7, DatabaseUtils.toString(faction.getClaimed()));
                    ps.setLong(8, faction.getBalance());
                    ps.setInt(9, faction.getKills());
                    ps.setLong(10, faction.getDeaths());
                    ps.setString(11, DatabaseUtils.to1String(faction.getAllies()));
                    ps.setString(12, DatabaseUtils.to1String(faction.getAllowedAllies()));
                    ps.setBoolean(13, faction.isOpen());
                    ps.setString(14, DatabaseUtils.toString(faction.getHome()));
                    ps.setString(15, faction.getTag());
                    ps.setString(16, faction.getTitle());
                    ps.setTimestamp(17, faction.getFounded());

                    ps.executeUpdate();
                }

                for (Core core : empire.getCores()) {
                    ps = connection.prepareStatement("INSERT INTO cores (uuid, type, alive, location, region," +
                            " claimed) VALUES (?,?,?,?,?,?)");

                    ps.setString(1, core.getUniqueId().toString());
                    ps.setString(2, core.getCoreType().getName());
                    ps.setBoolean(3, core.isAlive());
                    ps.setString(4, (DatabaseUtils.toString(core.getLocation())));
                    ps.setString(5, core.getRegion().toString());

                    ps.executeUpdate();
                }

            } catch (SQLException e) {
                main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", e);
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveEmpires() {
        Logger.log("Saving data");
        main.getEmpireManager().getEmpires().forEach(this::saveEmpire);
    }

    private void savePlayers() {
        Bukkit.getOnlinePlayers().forEach(this::savePlayer);
    }

    public void removeFaction(Faction faction) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = getSQLConnection();

            try {
                ps = connection.prepareStatement("DELETE FROM factions WHERE uuid=?");
                ps.setString(1, faction.getUniqueId().toString());
                ps.executeUpdate();

            } catch (SQLException ex) {
                main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addPlayer(UUID uuid) {
        MySQLPlayerData playerData = new MySQLPlayerData();
        playerData.setUUID(uuid.toString());

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpireByPlayerUUID(uuid);
        String empire = optionalEmpire.isPresent() ? optionalEmpire.get().getUniqueId().toString() : "";

        if (!(exists(uuid))) {
            try {
                PreparedStatement ps = getSQLConnection().prepareStatement("INSERT INTO players" +
                        " (uuid,empire_uuid,power) VALUES(?,?,?)");
                ps.setString(1, uuid.toString());
                ps.setString(2, empire);
                ps.setInt(3, 0);

                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            try {
                PreparedStatement ps = getSQLConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
                ps.setString(1, uuid.toString());

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getString("uuid").equalsIgnoreCase(uuid.toString())) {
                        playerData.setPower(rs.getInt("power"));
                    }
                }
            } catch (SQLException ex) {
                main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
            }
        }

        PlayerData.get().put(uuid, playerData);
    }

    public void savePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData playerData = PlayerData.get(uuid);

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpireByPlayerUUID(uuid);
        String empire = optionalEmpire.isPresent() ? optionalEmpire.get().getUniqueId().toString() : "";

        try {
            PreparedStatement ps = getSQLConnection().prepareStatement("UPDATE players SET empire_uuid=?,power=? WHERE uuid=?");
            ps.setString(1, empire);
            ps.setInt(2, playerData.getPower());
            ps.setString(3, uuid.toString());

            ps.executeUpdate();
        } catch (SQLException ex) {
            main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }
    }

    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = getSQLConnection().prepareStatement("SELECT * FROM players WHERE uuid=?");
            ps.setString(1, uuid.toString());

            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setFactionsPower() {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            for (Empire empire : main.getEmpireManager().getEmpires()) {
                for (Faction faction : empire.getSubFactions()) {
                    faction.setPower();
                }
            }
        });
    }


    private ChatColor getColor(String name) {
        for (ChatColor color : ChatColor.values()) {
            if (color.asBungee().getName().equalsIgnoreCase(name)) {
                return color;
            }
        }

        return ChatColor.BLUE;
    }


}
