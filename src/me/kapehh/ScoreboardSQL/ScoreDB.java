package me.kapehh.ScoreboardSQL;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
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

    private static String join(List<String> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first)
                first = false;
            else
                sb.append(conjunction);
            sb.append(item);
        }
        return sb.toString();
    }

    // WAT

    Map<String, Map<String, Object>> cacheScores = new HashMap<String, Map<String, Object>>();

    public void updateScore(List<Player> players) {
        try {
            if (connection == null || players.size() <= 0) {
                return;
            }

            List<String> playersName = new ArrayList<String>();
            for (Player player : players) {
                playersName.add("'" + player.getName() + "'");
            }
            String arrs = join(playersName, ",");
            String strSql = "SELECT * FROM player WHERE name IN (" + arrs + ")";

            cacheScores.clear();

            Map<String, Object> map;
            Statement sql = connection.createStatement();

            ResultSet result = sql.executeQuery(strSql);
            while (result.next()) {
                map = new HashMap<String, Object>();
                map.put("kills", result.getInt("kills"));
                map.put("deaths", result.getInt("deaths"));
                map.put("mobs", result.getInt("mobs"));
                map.put("prefix", result.getString("prefix"));
                cacheScores.put(result.getString("name"), map);
            }
            result.close();

            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getScore(Player player) {
        /*try {
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
        }*/
        if (!cacheScores.containsKey(player.getName())) {
            return null;
        } else {
            return cacheScores.get(player.getName());
        }
    }
}
