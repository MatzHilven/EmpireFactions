package me.matzhilven.empirefactions.data;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public abstract class Database {

    private final EmpireFactions main;
    public Connection connection;

    public Database(EmpireFactions instance) {
        main = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    //  Clan Data
    public Set<Empire> loadEmpires() {
        Set<Empire> empires = new HashSet<>();

        try {
            PreparedStatement ps = getSQLConnection().prepareStatement("SELECT * FROM clans");
            ResultSet results = ps.executeQuery();

            while (results.next()) {


            }

            return empires;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return new HashSet<>();
    }

    public void saveEmpire(Empire empire) {
//        if (!existsClan(clan.getName())) {
//            addEmpire(empire);
//            return;
//        }
//
//        try {
//            PreparedStatement ps = getSQLConnection().prepareStatement("UPDATE clans SET " +
//                    "name=?,leader=?,coLeaders=?,members=?,wins=?,losses=?," +
//                    "kills=? " +
//                    "WHERE name=?");
//            ps.setString(1, clan.getName());
//            ps.setString(2, clan.getLeader().toString());
//            ps.setString(3, StringUtils.to1String(clan.getCoLeaders()));
//            ps.setString(4, StringUtils.to1String(clan.getMembers()));
//            ps.setInt(5, clan.getWins());
//            ps.setInt(6, clan.getLosses());
//            ps.setInt(7, clan.getKills());
//            ps.setString(8, clan.getName());
//
//            ps.executeUpdate();
//        } catch (SQLException ex) {
//            main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
//        }
    }

    private void addEmpire(Empire empire) {
//        try {
//            PreparedStatement ps = getSQLConnection().prepareStatement("INSERT INTO clans " +
//                    "(name,leader,coLeaders,members,wins,losses,kills) " +
//                    "VALUES (?,?,?,?,?,?,?)");
//            ps.setString(1, clan.getName());
//            ps.setString(2, clan.getLeader().toString());
//            ps.setString(3, StringUtils.to1String(clan.getCoLeaders()));
//            ps.setString(4, StringUtils.to1String(clan.getMembers()));
//            ps.setInt(5, clan.getWins());
//            ps.setInt(6, clan.getLosses());
//            ps.setInt(7, clan.getKills());

//            ps.executeUpdate();
//        } catch (SQLException ex) {
//            main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
//        }
    }

    public void saveEmpires() {
        main.getEmpireManager().getEmpires().forEach(this::saveEmpire);
    }

    public void removeEmpire(Empire empire) {
        if (!exists(empire.getName())) return;
        try {
            PreparedStatement ps = getSQLConnection().prepareStatement("DELETE FROM empires WHERE name=?");
            ps.setString(1, empire.getName());

            ps.executeUpdate();
        } catch (SQLException ex) {
            main.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }
    }

    public boolean exists(String name) {
        try {
            PreparedStatement ps = getSQLConnection().prepareStatement("SELECT * FROM clans WHERE name=?");
            ps.setString(1, name);

            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            main.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
        }
    }

}
