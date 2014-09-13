package me.kapehh.ScoreboardSQL;

import me.kapehh.main.pluginmanager.config.EventPluginConfig;
import me.kapehh.main.pluginmanager.config.EventType;
import me.kapehh.main.pluginmanager.config.PluginConfig;
import me.kapehh.main.pluginmanager.vault.PluginVault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Karen on 10.09.2014.
 */
public class ScoreboardSQL extends JavaPlugin implements Listener, CommandExecutor { // TODO: Добавить команду для скрытия скореборда и релоада
    ScoreboardPlayer scoreboardPlayer;
    boolean isForce;
    PluginConfig pluginConfig;
    Economy economy;

    @EventPluginConfig(EventType.LOAD)
    public void onConfigLoad() {
        FileConfiguration cfg = pluginConfig.getConfig();

        if (ScoreDB.getInstance() != null) {
            try {
                ScoreDB.getInstance().disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ScoreDB.initInstance(
            cfg.getString("connect.ip", ""),
            cfg.getString("connect.db", ""),
            cfg.getString("connect.login", ""),
            cfg.getString("connect.password", "")
        );
        try {
            ScoreDB.getInstance().connect();
            getLogger().info("Success connect to MySQL!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (scoreboardPlayer != null) {
            scoreboardPlayer.stop(false);
            scoreboardPlayer = new ScoreboardPlayer(this, scoreboardPlayer.getPlayers());
        } else {
            scoreboardPlayer = new ScoreboardPlayer(this);
        }
        scoreboardPlayer.runTaskTimer(this, 100, cfg.getInt("timer.ticks", 1000));
        getLogger().info("Success run task!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String cmd = args[0];
        if (cmd.equalsIgnoreCase("reload") && !(sender instanceof Player)) {
            pluginConfig.loadData();
            return true;
        } else if (cmd.equalsIgnoreCase("toggle") && (sender instanceof Player)) {
            Player player = (Player) sender;
            if (scoreboardPlayer.inList(player)) {
                scoreboardPlayer.stopPlayer(player);
            } else {
                scoreboardPlayer.runPlayer(player);
            }
            return true;
        }

        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (scoreboardPlayer != null) {
            scoreboardPlayer.runPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (scoreboardPlayer != null) {
            scoreboardPlayer.stopPlayer(event.getPlayer());
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

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("scoreboard").setExecutor(this);

        pluginConfig = new PluginConfig(this);
        pluginConfig.addEventClasses(this).setup().loadData();
    }

    @Override
    public void onDisable() {
        if (isForce) {
            return;
        }

        if (scoreboardPlayer != null) {
            scoreboardPlayer.stop();
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

    public ScoreboardPlayer getScoreboardPlayer() {
        return scoreboardPlayer;
    }
}
