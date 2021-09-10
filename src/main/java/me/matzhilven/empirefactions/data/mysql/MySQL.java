package me.matzhilven.empirefactions.data.mysql;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.data.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class MySQL extends Database {

    private final EmpireFactions main;

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQL(EmpireFactions main) {
        super(main);
        this.main = main;
        this.host = main.getConfig().getString("mysql.host");
        this.port = main.getConfig().getInt("mysql.port");
        this.database = main.getConfig().getString("mysql.database");
        this.username = main.getConfig().getString("mysql.username");
        this.password = main.getConfig().getString("mysql.password");
    }

    public Connection getSQLConnection() {

        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database,
                    username, password);
            return connection;
        } catch (SQLException ex) {
            main.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            main.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS clans (" +
                    "`name` varchar(100) NOT NULL," +
                    "`leader` varchar(36) NOT NULL," +
                    "`coLeaders` text(100000000) NOT NULL," +
                    "`members` text(100000000) NOT NULL," +
                    "`wins` int(255) NOT NULL," +
                    "`losses` int(255) NOT NULL," +
                    "`kills` int(255) NOT NULL," +
                    "PRIMARY KEY (`name`)" +
                    ");";
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement s = connection.createStatement();
            String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS players (" +
                    "`uuid` varchar(36) NOT NULL," +
                    "`wins` int(255) NOT NULL," +
                    "`losses` int(255) NOT NULL," +
                    "`kills` int(255) NOT NULL," +
                    "`deaths` int(255) NOT NULL," +
                    "`killstreak` int(255) NOT NULL," +
                    "PRIMARY KEY (`uuid`)" +
                    ");";
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
