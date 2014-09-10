package me.kapehh.ScoreboardSQL;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Karen on 10.09.2014.
 */
public class ScoreboardSQL extends JavaPlugin {

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("PluginManager") == null) {
            getLogger().info("PluginManager not found!!!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);

        ScoreDB.initInstance("", "", "", "");
        try {
            ScoreDB.getInstance().connect();
            getLogger().info("Success connect to MySQL!");
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            ScoreDB.getInstance().disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
