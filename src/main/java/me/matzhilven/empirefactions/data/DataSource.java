package me.matzhilven.empirefactions.data;

import com.zaxxer.hikari.HikariDataSource;
import me.matzhilven.empirefactions.EmpireFactions;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private final HikariDataSource ds;

    public DataSource(EmpireFactions main) {
        ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://"
                + main.getConfig().getString("mysql.host") +
                ":" + main.getConfig().getInt("mysql.port") + "/"
                + main.getConfig().getString("mysql.database"));
        ds.setUsername(main.getConfig().getString("mysql.username"));
        ds.setPassword(main.getConfig().getString("mysql.password"));

    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
