package me.kapehh.ScoreboardSQL;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;

/**
 * Created by Karen on 10.09.2014.
 */
public class ScoreboardListener implements Listener {
    protected static final String SB_NAME = "SQLStats";
    protected static final String DUMMY_CRITERIA = "dummy";
    protected static final String PREFFIX_SCORE = ChatColor.GREEN + "" + ChatColor.BOLD;

    Economy economy;

    public ScoreboardListener(ScoreboardSQL plugin) {
        economy = plugin.getEconomy();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Map<String, Object> map = ScoreDB.getInstance().getScore(player);

        if (map == null) {
            return;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(SB_NAME, DUMMY_CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.BOLD + player.getName() + " - " + ChatColor.RED + "" + ChatColor.BOLD + map.get("prefix").toString());

        Score score;

        if (economy != null) {
            score = objective.getScore(PREFFIX_SCORE + "Динарий");
            score.setScore((int) economy.getBalance(player.getName()));
        }

        score = objective.getScore(PREFFIX_SCORE + "Убийства");
        score.setScore((Integer) map.get("kills"));

        score = objective.getScore(PREFFIX_SCORE + "Смертей");
        score.setScore((Integer) map.get("deaths"));

        score = objective.getScore(PREFFIX_SCORE + "Мобов");
        score.setScore((Integer) map.get("mobs"));

        player.setScoreboard(objective.getScoreboard());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
            player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
