package org.edenmc.kingdoms;

import com.mysql.jdbc.CommunicationsException;
import org.bukkit.Bukkit;

import java.sql.*;

/**
 * Created by Jack on 6/22/2017.
 */
public class MySQL {

    static String prefix = Kingdoms.mySQL.get("TablePrefix");
    public static Connection con;

    public static void connect() {
        String host = Kingdoms.mySQL.get("Host");
        String database = Kingdoms.mySQL.get("Database");
        String username = Kingdoms.mySQL.get("Username");
        String password = Kingdoms.mySQL.get("Password");

        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database,username,password);
            Bukkit.getServer().broadcastMessage("Connected to MySQL database!");
        }catch(Exception e) {
            System.out.println(e);
            Bukkit.getServer().broadcastMessage("Kingdoms could not connect to the MySQL server. Disabling plugin.");
            Bukkit.getServer().getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("Kingdoms"));
            return;
        }

        checkTables(con);

    }

    public static void checkTables(Connection con) {
        String playerTable = prefix + "_players";
        try {
            Statement createTable = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + playerTable +
                    " (uuid VARCHAR(36) not NULL, " +
                    " balance INTEGER, " +
                    " race VARCHAR(16), " +
                    " racelevel INTEGER, " +
                    " PRIMARY KEY (uuid))";
            createTable.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void enterData(String table, String[] columns, String[] data) {
        try {
            if (con.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = prefix + "_" + table;
        try {
            Statement statement = con.createStatement();
            String sql = "INSERT INTO " + table + " (";
            for (String column : columns) {
                sql = sql + column + ", ";
            }
            sql = sql.substring(0,sql.length() - 2);
            sql = sql + ") VALUES(";
            for (String entry : data) {
                try {
                    Integer.parseInt(entry);
                    sql = sql + entry + ", ";
                } catch (NumberFormatException e) {
                    sql = sql + "'" + entry + "', ";
                }
            }
            sql = sql.substring(0,sql.length() - 2);
            sql = sql + ") ON DUPLICATE KEY UPDATE ";
            for (int i = 1; i < columns.length; i++) {
                try {
                    Integer.parseInt(data[i]);
                    sql = sql + columns[i] + "=" + data[i] + ", ";
                } catch (NumberFormatException e) {
                    sql = sql + columns[i] + "='" + data[i] + "', ";
                }
            }
            sql = sql.substring(0,sql.length() - 2);
            statement.executeUpdate(sql);
        } catch (CommunicationsException e) {
            System.out.println(e);
            connect();
            enterData(table,columns,data);

        } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    public static String getData(String table, String check, String column, String condition) {
        try {
            if (con.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = prefix + "_" + table;
        try {
            Statement statement = con.createStatement();
            String query = "SELECT " + column + " FROM " + table + " WHERE " + check + "='" + condition + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                if (rs.getObject(1) != null) {
                    return rs.getObject(1).toString();
                }
                return null;
            }
        } catch (CommunicationsException e) {
            e.printStackTrace();
            connect();
            return getData(table,check,column,condition);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return "";
    }

    public static void terminate() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
