package me.kapehh.ScoreboardSQL;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Created by Karen on 04.10.2014.
 */
public class ScoreUpdater extends BukkitRunnable {
    ScoreboardSQL plugin;

    public ScoreUpdater(ScoreboardSQL plugin) {
        this.plugin = plugin;
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(getTaskId());
    }

    @Override
    public void run() {
        if (plugin.getScoreboardPlayer() == null) {
            return;
        }
        ScoreDB.getInstance().updateScore(plugin.getScoreboardPlayer().getPlayers());
    }
}
