package com.modularenigma.MobHunt;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class ScoreboardController {
    private final MobHuntMain plugin;

    public ScoreboardController(MobHuntMain plugin) {
        this.plugin = plugin;
    }

    public void reloadScoreboard(Player player, int points) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("PlayerHeadScoreboard", "dummy", Component.text(plugin.config().getLangScoreboardTitle()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> scoreboardContent = plugin.config().getLangScoreboardContent();
        for (int i = 0; i < scoreboardContent.size(); i++) {
            Score line = objective.getScore(scoreboardContent.get(i)
                    .replace("%Points%", "" + points));
            line.setScore(scoreboardContent.size() - i);
        }
        player.setScoreboard(board);
    }
}
