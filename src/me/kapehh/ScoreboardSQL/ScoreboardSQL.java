package me.kapehh.ScoreboardSQL;

import me.kapehh.main.pluginmanager.config.EventPluginConfig;
import me.kapehh.main.pluginmanager.config.EventType;
import me.kapehh.main.pluginmanager.config.PluginConfig;
import me.kapehh.main.pluginmanager.vault.PluginVault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Karen on 10.09.2014.
 */
public class ScoreboardSQL extends JavaPlugin { // TODO: Добавить команду для скрытия скореборда и релоада
    boolean isForce;
    PluginConfig pluginConfig;
    Economy economy;

    @EventPluginConfig(EventType.LOAD)
    public void onLoad() {
        FileConfiguration cfg = pluginConfig.getConfig();

        if (ScoreDB.getInstance() != null) {
            try {
                ScoreDB.getInstance().disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ScoreDB.initInstance(
            cfg.getString("connect.ip"),
            cfg.getString("connect.db"),
            cfg.getString("connect.login"),
            cfg.getString("connect.password")
        );
        try {
            ScoreDB.getInstance().connect();
            getLogger().info("Success connect to MySQL!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("PluginManager") == null) {
            getLogger().info("PluginManager not found!!!");
            getServer().getPluginManager().disablePlugin(this);
            isForce = true;
            return;
        }
        isForce = false;

        economy = PluginVault.setupEconomy();

        getServer().getPluginManager().registerEvents(new ScoreboardListener(this), this);

        pluginConfig = new PluginConfig(this);
        pluginConfig.addEventClasses(this).setup().loadData();
    }

    @Override
    public void onDisable() {
        if (isForce) {
            return;
        }

        if (ScoreDB.getInstance() != null) {
            try {
                ScoreDB.getInstance().disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Economy getEconomy() {
        return economy;
    }
}
