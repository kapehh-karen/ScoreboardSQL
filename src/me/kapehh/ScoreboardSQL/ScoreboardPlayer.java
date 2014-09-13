package me.kapehh.ScoreboardSQL;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Karen on 11.09.2014.
 */
public class ScoreboardPlayer extends BukkitRunnable {
    protected static final String SB_NAME = "SQLStats";
    protected static final String DUMMY_CRITERIA = "dummy";
    protected static final String PREFFIX_SCORE = ChatColor.GREEN + "" + ChatColor.BOLD;

    List<Player> players;
    Economy economy;

    public ScoreboardPlayer(ScoreboardSQL plugin) {
        this(plugin, new ArrayList<Player>());
    }

    public ScoreboardPlayer(ScoreboardSQL plugin, List<Player> players) {
        this.economy = plugin.getEconomy();
        this.players = players;
    }

    public boolean inList(Player player) {
        return players.contains(player);
    }

    public void runPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            create(player);
            update(player);
        }
    }

    public void stopPlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            remove(player);
        }
    }

    public void stop() {
        stop(true);
    }

    public void stop(boolean clearPlayers) {
        if (clearPlayers) {
            while (players.size() > 0) {
                stopPlayer(players.get(0));
            }
        }
        Bukkit.getScheduler().cancelTask(getTaskId());
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public void run() {
        for (Player player : players) {
            update(player);
        }
    }

    private Objective create(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(SB_NAME, DUMMY_CRITERIA);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }

    private void update(Player player) {
        Map<String, Object> map = ScoreDB.getInstance().getScore(player);

        if (map == null) {
            return;
        }

        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective == null || !objective.getName().equalsIgnoreCase(SB_NAME)) {
            remove(player);
            objective = create(player);
        }
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

    private void remove(Player player) {
        if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
            player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
