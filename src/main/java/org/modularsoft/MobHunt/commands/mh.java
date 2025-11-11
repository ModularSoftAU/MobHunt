package org.modularsoft.MobHunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.modularsoft.MobHunt.MobHuntMain;
import org.modularsoft.MobHunt.ScoreboardController;

import java.util.Locale;
import java.util.UUID;

public class mh implements CommandExecutor {
    private final MobHuntMain plugin;
    private final ScoreboardController scoreboardController;

    public mh(MobHuntMain plugin, ScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.scoreboardController = scoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("toggle")) {
            sender.sendMessage(plugin.config().getLangToggleUsage());
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.config().getLangToggleUsage());
            return true;
        }

        String target = args[1].toLowerCase(Locale.ROOT);
        String action = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "toggle";

        switch (target) {
            case "scoreboard", "sidebar", "leaderboard" -> handleScoreboardToggle(player, action);
            case "notifications", "points", "chat" -> handleNotificationToggle(player, action);
            default -> sender.sendMessage(plugin.config().getLangToggleUnknownTarget());
        }

        return true;
    }

    private void handleScoreboardToggle(Player player, String action) {
        switch (action) {
            case "on", "enable", "show" -> {
                scoreboardController.setSidebarEnabled(player, true);
                player.sendMessage(plugin.config().getLangScoreboardToggleOn());
            }
            case "off", "disable", "hide" -> {
                scoreboardController.setSidebarEnabled(player, false);
                player.sendMessage(plugin.config().getLangScoreboardToggleOff());
            }
            default -> {
                boolean enabled = scoreboardController.toggleSidebar(player);
                player.sendMessage(enabled ? plugin.config().getLangScoreboardToggleOn() : plugin.config().getLangScoreboardToggleOff());
            }
        }
    }

    private void handleNotificationToggle(Player player, String action) {
        UUID uuid = player.getUniqueId();
        switch (action) {
            case "on", "enable", "show" -> {
                plugin.getPlayerDataStorage().setPointNotificationsEnabled(uuid, true);
                player.sendMessage(plugin.config().getLangNotificationsToggleOn());
            }
            case "off", "disable", "hide" -> {
                plugin.getPlayerDataStorage().setPointNotificationsEnabled(uuid, false);
                player.sendMessage(plugin.config().getLangNotificationsToggleOff());
            }
            default -> {
                boolean enabled = plugin.getPlayerDataStorage().togglePointNotifications(uuid);
                player.sendMessage(enabled ? plugin.config().getLangNotificationsToggleOn() : plugin.config().getLangNotificationsToggleOff());
            }
        }
    }
}
