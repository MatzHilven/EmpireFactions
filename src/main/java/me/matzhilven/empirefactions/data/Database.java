package me.matzhilven.empirefactions.data;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.empire.region.Region;
import me.matzhilven.empirefactions.utils.DatabaseUtils;
import me.matzhilven.empirefactions.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public abstract class Database {

    private final EmpireFactions main;
    public Connection connection;

    public Database(EmpireFactions instance) {
        main = instance;
        loadEmpires();
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void loadEmpires() {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
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
                            cores,
                            factions,
                            UUID.fromString(rs.getString("leader")),
                            rs.getString("description"),
                            getColor(rs.getString("color")),
                            Region.of(rs.getString("region")),
                            Region.of(rs.getString("jurisdiction"))
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
                                new ArrayList<>()
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
                        "moderators=?, members=?, color=?, region=?, jurisdiction=? WHERE uuid=?");
                ps.setString(1, empire.getName());
                ps.setString(2, empire.getDescription());
                ps.setString(3, empire.getLeader().toString());
                ps.setString(4, DatabaseUtils.to1String(empire.getAdmins()));
                ps.setString(5, DatabaseUtils.to1String(empire.getModerators()));
                ps.setString(6, DatabaseUtils.to1String(empire.getMembers()));
                ps.setString(7, empire.getColor().asBungee().getName().toUpperCase());
                ps.setString(8, empire.getRegion().toString());
                ps.setString(9, empire.getJurisdiction().toString());
                ps.setString(10, empire.getUniqueId().toString());

                ps.executeUpdate();

                for (Faction faction : empire.getSubFactions()) {
                    ps = connection.prepareStatement("UPDATE factions SET name=?, description=?, leader=?, " +
                            "members=?, claimed=? WHERE uuid=?");

                    ps = connection.prepareStatement("INSERT INTO factions (empire_uuid, uuid, name, description, leader, members, claimed) " +
                            "VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=?, description=?, leader=?, members=?, claimed=?");

                    ps.setString(1, empire.getUniqueId().toString());
                    ps.setString(2, faction.getUniqueId().toString());
                    ps.setString(3, faction.getName());
                    ps.setString(4, faction.getDescription());
                    ps.setString(5, faction.getLeader().toString());
                    ps.setString(6, DatabaseUtils.to1String(faction.getMembers()));
                    ps.setString(7, faction.getClaimed().toString());
                    ps.setString(8, faction.getName());
                    ps.setString(9, faction.getDescription());
                    ps.setString(10, faction.getLeader().toString());
                    ps.setString(11, DatabaseUtils.to1String(faction.getMembers()));
                    ps.setString(12, faction.getClaimed().toString());

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
                        " moderators, members, color, region, jurisdiction) VALUES (?,?,?,?,?,?,?,?,?,?)");

                ps.setString(1, empire.getUniqueId().toString());
                ps.setString(2, empire.getName());
                ps.setString(3, empire.getDescription());
                ps.setString(4, empire.getLeader().toString());
                ps.setString(5, DatabaseUtils.to1String(empire.getAdmins()));
                ps.setString(6, DatabaseUtils.to1String(empire.getModerators()));
                ps.setString(7, DatabaseUtils.to1String(empire.getMembers()));
                ps.setString(8, empire.getColor().asBungee().getName().toUpperCase());
                ps.setString(9, empire.getRegion().toString());
                ps.setString(10, empire.getJurisdiction().toString());

                ps.executeUpdate();

                for (Faction faction : empire.getSubFactions()) {
                    ps = connection.prepareStatement("INSERT INTO factions (uuid, name, description, leader, members," +
                            " claimed) VALUES (?,?,?,?,?,?)");

                    ps.setString(1, faction.getUniqueId().toString());
                    ps.setString(2, faction.getName());
                    ps.setString(3, faction.getDescription());
                    ps.setString(4, faction.getLeader().toString());
                    ps.setString(5, DatabaseUtils.to1String(faction.getMembers()));
                    ps.setString(6, faction.getClaimed().toString());

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
        Logger.log("saving data");
        main.getEmpireManager().getEmpires().forEach(this::saveEmpire);
    }

    public void removeEmpire(Empire empire) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            PreparedStatement ps = null;
            Connection connection = getSQLConnection();

            try {
                ps = getSQLConnection().prepareStatement("SELECT * FROM empires WHERE uuid=?");
                ps.setString(1, empire.getUniqueId().toString());
                ResultSet results = ps.executeQuery();
                if (!results.next()) {
                    return;
                }

                ps = connection.prepareStatement("DELETE FROM empires WHERE uuid=?");
                ps.setString(1, empire.getUniqueId().toString());
                ps.executeUpdate();

                ps = connection.prepareStatement("DELETE FROM factions WHERE empire_uuid=?");
                ps.setString(1, empire.getUniqueId().toString());
                ps.executeUpdate();

                ps = connection.prepareStatement("DELETE FROM cores WHERE empire_uuid=?");
                ps.setString(1, empire.getUniqueId().toString());
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

    private ChatColor getColor(String name) {
        for (ChatColor color : ChatColor.values()) {
            if (color.asBungee().getName().equalsIgnoreCase(name)) {
                return color;
            }
        }

        return ChatColor.BLUE;
    }


}
