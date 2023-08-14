package com.rmaafs.arenapvp.manager.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static org.bukkit.Bukkit.getServer;

public class MySQL {

    private Connection con;
    private String mysqlIp, mysqlPort, mysqlDB, mysqlUser, mysqlPass;

    public MySQL() {
        mysqlIp = cconfig.getString("mysql.ip");
        mysqlPort = cconfig.getString("mysql.port");
        mysqlDB = cconfig.getString("mysql.database");
        mysqlUser = cconfig.getString("mysql.username");
        mysqlPass = cconfig.getString("mysql.password");
        connect();
    }

    public void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + mysqlIp + ":" + mysqlPort + "/" + mysqlDB, mysqlUser, mysqlPass);
            getServer().getConsoleSender().sendMessage("§2ArenaPvP >>§a Successfully connected to database.");
        } catch (SQLException e) {
            getServer().getConsoleSender().sendMessage("§2ArenaPvP >> §cERROR MYSQL: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                getServer().getConsoleSender().sendMessage("§2ArenaPvP >>§a Database closed");
            }
        } catch (SQLException e) {
            getServer().getConsoleSender().sendMessage("§2ArenaPvP >> §cERROR MYSQL: " + e.getMessage());
        }
    }

    public void update(String qry) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate(qry);
            st.close();
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
    }
    
    public ResultSet query(String qry) {
        ResultSet rs = null;

        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(qry);
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }
}
