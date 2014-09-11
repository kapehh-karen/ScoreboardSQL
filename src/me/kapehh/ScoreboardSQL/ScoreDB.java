package me.kapehh.ScoreboardSQL;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Karen on 10.09.2014.
 */
public class ScoreDB {
    private static ScoreDB scoreDB = null;

    public static ScoreDB getInstance() {
        return scoreDB;
    }

    public static void initInstance(String ip, String db, String login, String password) {
        scoreDB = new ScoreDB(ip, db, login, password);
    }

    Connection connection = null;
    String ip;
    String db;
    String login;
    String password;

    public ScoreDB(String ip, String db, String login, String password) {
        this.ip = ip;
        this.db = db;
        this.login = login;
        this.password = password;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + db, login, password);
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public Map<String, Object> getScore(Player player) {
        try {
            if (connection == null) {
                return null;
            }

            boolean isNull = true;
            HashMap<String, Object> map = new HashMap<String, Object>();
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM player WHERE name=?");
            sql.setString(1, player.getName());

            ResultSet result = sql.executeQuery();
            if (result.next()) {
                map.put("kills", result.getInt("kills"));
                map.put("deaths", result.getInt("deaths"));
                map.put("mobs", result.getInt("mobs"));
                map.put("prefix", result.getString("prefix"));
                isNull = false;
            }
            result.close();

            sql.close();
            return isNull ? null : map;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
