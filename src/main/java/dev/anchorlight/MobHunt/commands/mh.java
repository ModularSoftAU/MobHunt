package dev.anchorlight.MobHunt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import dev.anchorlight.MobHunt.HunterController;
import dev.anchorlight.MobHunt.MobHuntMain;
import dev.anchorlight.MobHunt.MobHuntQuery;
import dev.anchorlight.MobHunt.ScoreboardController;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class mh implements CommandExecutor {
    private final MobHuntMain plugin;
    private final HunterController hunterController;
    private final ScoreboardController scoreboardController;

    public mh(MobHuntMain plugin, HunterController hunterController, ScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
        this.scoreboardController = scoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        ParsedCommand parsed = parseCommand(label, args);
        if (parsed.subcommand().isEmpty()) {
            sendUsage(player);
            return true;
        }

        switch (parsed.subcommand()) {
            case "help" -> hunterController.mobHelpResponse(player);
            case "leaderboard" -> handleLeaderboard(player, args, parsed.argStart());
            case "scoreboard" -> handleScoreboardToggle(player, args, parsed.argStart());
            case "toggle" -> handleToggle(player, args, parsed.argStart());
            case "stats" -> handleStats(player, args, parsed.argStart());
            default -> sendUsage(player);
        }

        return true;
    }

    private ParsedCommand parseCommand(String label, String[] args) {
        String lowerLabel = label.toLowerCase(Locale.ROOT);
        String subcommand = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";
        int argStart = 1;

        switch (lowerLabel) {
            case "mobleaderboard", "leaderboard", "lb" -> {
                subcommand = "leaderboard";
                argStart = 0;
            }
            case "mobhelp", "howtoplay" -> {
                subcommand = "help";
                argStart = 0;
            }
            case "mobscoreboard", "mhsb", "mobsb" -> {
                subcommand = "scoreboard";
                argStart = 0;
            }
            case "mobstats" -> {
                subcommand = "stats";
                argStart = 0;
            }
            case "mhtoggle" -> {
                subcommand = "toggle";
                argStart = 0;
            }
            default -> {}
        }

        return new ParsedCommand(subcommand, argStart);
    }

    private void handleLeaderboard(Player player, String[] args, int argStart) {
        int showPlayers = plugin.config().getLeaderboardShowPlayers();
        List<String> leaderboardText;

        if (args.length > argStart) {
            String mobType = String.join(" ", List.of(args).subList(argStart, args.length));
            if (plugin.config().getMobPoints(mobType) == null) {
                player.sendMessage(plugin.config().getLangLeaderboardStringNotAMob());
                return;
            }

            List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestMobTypeHunters(
                    plugin, player, showPlayers, mobType);
            String leaderboardTitle = plugin.config().getLangLeaderboardMobTitleFormat()
                    .replace("%MobType%", mobType);
            leaderboardText = hunterController.getLeaderboardText(bestHunters, leaderboardTitle);
        } else {
            List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestHunters(
                    plugin, player, showPlayers);
            leaderboardText = hunterController.getLeaderboardText(bestHunters);
        }

        int centrePixel = HunterController.minecraftMessageLengthInPixels(
                plugin.config().getLangLeaderboardHeader()) / 2;
        for (String line : leaderboardText) {
            player.sendMessage(HunterController.centreMessage(line, centrePixel));
        }
    }

    private void handleScoreboardToggle(Player player, String[] args, int argStart) {
        String action = args.length > argStart ? args[argStart].toLowerCase(Locale.ROOT) : "toggle";
        switch (action) {
            case "on", "enable", "show" -> {
                scoreboardController.setSidebarEnabled(player, true);
                player.sendMessage(plugin.config().getLangScoreboardToggleOn());
            }
            case "off", "disable", "hide" -> {
                scoreboardController.setSidebarEnabled(player, false);
                player.sendMessage(plugin.config().getLangScoreboardToggleOff());
            }
            case "toggle" -> {
                boolean enabled = scoreboardController.toggleSidebar(player);
                player.sendMessage(enabled ? plugin.config().getLangScoreboardToggleOn() : plugin.config().getLangScoreboardToggleOff());
            }
            default -> player.sendMessage(plugin.config().getLangScoreboardToggleUsage());
        }
    }

    private void handleToggle(Player player, String[] args, int argStart) {
        if (args.length <= argStart) {
            player.sendMessage(plugin.config().getLangToggleUsage());
            return;
        }

        String target = args[argStart].toLowerCase(Locale.ROOT);
        String action = args.length > argStart + 1 ? args[argStart + 1].toLowerCase(Locale.ROOT) : "toggle";

        switch (target) {
            case "scoreboard", "sidebar", "leaderboard" -> handleScoreboardToggle(player, args, argStart + 1);
            case "notifications", "points", "chat" -> handleNotificationToggle(player, action);
            default -> player.sendMessage(plugin.config().getLangToggleUnknownTarget());
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
            case "toggle" -> {
                boolean enabled = plugin.getPlayerDataStorage().togglePointNotifications(uuid);
                player.sendMessage(enabled ? plugin.config().getLangNotificationsToggleOn() : plugin.config().getLangNotificationsToggleOff());
            }
            default -> player.sendMessage(plugin.config().getLangToggleUsage());
        }
    }

    private void handleStats(Player player, String[] args, int argStart) {
        if (args.length > argStart) {
            player.sendMessage(ChatColor.RED + "Use /mhadmin stats <player> to inspect other hunters.");
            return;
        }

        List<MobHuntQuery.MobStat> stats = MobHuntQuery.killedMobStats(plugin, player);
        hunterController.mobStatsResponse(player, stats);
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /mh <help|leaderboard|stats|scoreboard|toggle>");
        player.sendMessage(ChatColor.GRAY + "Examples: /mh leaderboard blaze, /mh scoreboard off, /mh toggle notifications");
    }

    private record ParsedCommand(String subcommand, int argStart) {
    }
}
