package org.modularsoft.MobHunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.modularsoft.MobHunt.MobHuntMain;
import org.modularsoft.MobHunt.ScoreboardController;

import java.util.Locale;

public class mobscoreboard implements CommandExecutor {
    private final MobHuntMain plugin;
    private final ScoreboardController scoreboardController;

    public mobscoreboard(MobHuntMain plugin, ScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.scoreboardController = scoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (args.length == 0) {
            boolean enabled = scoreboardController.toggleSidebar(player);
            sender.sendMessage(enabled ? plugin.config().getLangScoreboardToggleOn() : plugin.config().getLangScoreboardToggleOff());
            return true;
        }

        String option = args[0].toLowerCase(Locale.ROOT);
        switch (option) {
            case "on", "enable", "show" -> {
                scoreboardController.setSidebarEnabled(player, true);
                sender.sendMessage(plugin.config().getLangScoreboardToggleOn());
            }
            case "off", "disable", "hide" -> {
                scoreboardController.setSidebarEnabled(player, false);
                sender.sendMessage(plugin.config().getLangScoreboardToggleOff());
            }
            case "toggle" -> {
                boolean enabled = scoreboardController.toggleSidebar(player);
                sender.sendMessage(enabled ? plugin.config().getLangScoreboardToggleOn() : plugin.config().getLangScoreboardToggleOff());
            }
            default -> sender.sendMessage(plugin.config().getLangScoreboardToggleUsage());
        }

        return true;
    }
}
