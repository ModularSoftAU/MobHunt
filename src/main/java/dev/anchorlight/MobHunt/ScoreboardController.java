package dev.anchorlight.MobHunt;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.List;
import java.util.UUID;

public class ScoreboardController {
    private final MobHuntMain plugin;

    public ScoreboardController(MobHuntMain plugin) {
        this.plugin = plugin;
    }

    public void reloadScoreboard(Player player, int points) {
        if (!isSidebarEnabled(player.getUniqueId())) {
            clearScoreboard(player);
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null)
            return;

        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("MobHuntScoreboard", "dummy", Component.text(plugin.config().getLangScoreboardTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> scoreboardContent = plugin.config().getLangScoreboardContent();
        for (int i = 0; i < scoreboardContent.size(); i++) {
            Score line = objective.getScore(scoreboardContent.get(i)
                    .replace("%Points%", "" + points));
            line.setScore(scoreboardContent.size() - i);
        }
        player.setScoreboard(board);
    }

    public void clearScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null)
            return;

        player.setScoreboard(manager.getMainScoreboard());
    }

    public boolean toggleSidebar(Player player) {
        UUID uuid = player.getUniqueId();
        boolean enable = !isSidebarEnabled(uuid);
        setSidebarEnabled(player, enable);
        return enable;
    }

    public void setSidebarEnabled(Player player, boolean enabled) {
        UUID uuid = player.getUniqueId();
        plugin.getPlayerDataStorage().ensurePlayerProfile(uuid, player.getName());
        plugin.getPlayerDataStorage().setSidebarEnabled(uuid, enabled);

        if (enabled)
            reloadScoreboard(player, MobHuntQuery.getPoints(plugin, player));
        else
            clearScoreboard(player);
    }

    public boolean isSidebarEnabled(UUID uuid) {
        return plugin.getPlayerDataStorage().isSidebarEnabled(uuid);
    }
}
