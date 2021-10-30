package me.matzhilven.empirefactions.data.mysql;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.data.DataSource;
import me.matzhilven.empirefactions.data.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends Database {

    private final DataSource dataSource;

    public MySQL(EmpireFactions main) {
        super(main);
        this.dataSource = new DataSource(main);
    }

    public Connection getSQLConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void load() {
        connection = getSQLConnection();
        Statement s;

        try {

            s = connection.createStatement();
            String empiresTable = "CREATE TABLE IF NOT EXISTS empires (" +
                    "`id` int NOT NULL AUTO_INCREMENT," +
                    "`uuid` varchar(36) NOT NULL," +
                    "`name` varchar(100) NOT NULL," +
                    "`description` text NOT NULL," +
                    "`leader` varchar(36) NOT NULL," +
                    "`admins` text NOT NULL," +
                    "`moderators` text NOT NULL," +
                    "`members` text NOT NULL," +
                    "`baseCenter` text NOT NULL," +
                    "`jurisdictionCenter` text NOT NULL," +
                    "`color` varchar(20) NOT NULL," +
                    "`region` text NOT NULL," +
                    "`jurisdiction` text NOT NULL," +
                    "PRIMARY KEY (`id`)" +
                    ");";
            s.executeUpdate(empiresTable);

            s = connection.createStatement();
            String factionsTable = "CREATE TABLE IF NOT EXISTS factions (" +
                    "`empire_uuid` varchar(36) NOT NULL," +
                    "`uuid` varchar(36) NOT NULL," +
                    "`name` varchar(100) NOT NULL," +
                    "`description` text NOT NULL," +
                    "`leader` varchar(36) NOT NULL," +
                    "`members` text NOT NULL," +
                    "`claimed` text NOT NULL," +
                    "`balance` bigint(255) NOT NULL," +
                    "`kills` int NOT NULL," +
                    "`deaths` int NOT NULL," +
                    "`allies` text NOT NULL," +
                    "`allowedAllies` text NOT NULL," +
                    "`isOpen` boolean NOT NULL," +
                    "`home` text," +
                    "`tag` text NOT NULL," +
                    "`title` text NOT NULL," +
                    "`founded` timestamp NOT NULL," +
                    "PRIMARY KEY (`uuid`)" +
                    ");";
            s.executeUpdate(factionsTable);

            s = connection.createStatement();
            String coresTable = "CREATE TABLE IF NOT EXISTS cores (" +
                    "`empire_uuid` varchar(36) NOT NULL," +
                    "`uuid` varchar(36) NOT NULL," +
                    "`type` varchar(10) NOT NULL," +
                    "`alive` bool NOT NULL," +
                    "`location` text NOT NULL," +
                    "`region` text NOT NULL," +
                    "PRIMARY KEY (`uuid`)" +
                    ");";

            s.executeUpdate(coresTable);

            s = connection.createStatement();
            String playersTable = "CREATE TABLE IF NOT EXISTS players (" +
                    "`uuid` varchar(36) NOT NULL," +
                    "`empire_uuid` varchar(36) NOT NULL," +
                    "`power` varchar(100) NOT NULL," +
                    "PRIMARY KEY (`uuid`)" +
                    ");";
            s.executeUpdate(playersTable);

            s.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
