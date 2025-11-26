package dev.anchorlight.MobHunt.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import dev.anchorlight.MobHunt.HunterController;
import dev.anchorlight.MobHunt.MobHuntMain;
import dev.anchorlight.MobHunt.MobHuntQuery;
import dev.anchorlight.MobHunt.ScoreboardController;

import java.util.Locale;

public class mhadmin implements CommandExecutor {
    private final MobHuntMain plugin;
    private final HunterController hunterController;
    private final ScoreboardController scoreboardController;

    public mhadmin(MobHuntMain plugin, HunterController hunterController, ScoreboardController scoreboardController) {
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

        if (!plugin.isSenderAdmin(sender)) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        ParsedCommand parsed = parseCommand(label, args);
        if (parsed.subcommand().isEmpty()) {
            sendUsage(player);
            return true;
        }

        switch (parsed.subcommand()) {
            case "clear" -> handleClear(player, args, parsed.argStart());
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
            case "mobclear" -> {
                subcommand = "clear";
                argStart = 0;
            }
            case "mobadmin" -> {
                subcommand = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";
                argStart = subcommand.isEmpty() ? 0 : 1;
            }
            default -> {}
        }

        return new ParsedCommand(subcommand, argStart);
    }

    private void handleClear(Player sender, String[] args, int argStart) {
        if (args.length > argStart) {
            OfflinePlayer toClear = sender.getServer().getOfflinePlayerIfCached(args[argStart]);
            if (toClear == null) {
                sender.sendMessage(plugin.config().getLangStringIsNotAValidPlayer());
                return;
            }

            if (MobHuntQuery.clearMobs(plugin, sender, toClear.getUniqueId())) {
                hunterController.playerClearedTheirPointsResponse(sender, toClear.getName());

                Player onlinePlayer = toClear.getPlayer();
                if (onlinePlayer != null) {
                    scoreboardController.reloadScoreboard(onlinePlayer, MobHuntQuery.getPoints(plugin, onlinePlayer));
                }
            }
            return;
        }

        if (MobHuntQuery.clearMobs(plugin, sender)) {
            hunterController.playerClearedTheirPointsResponse(sender);
            scoreboardController.reloadScoreboard(sender, MobHuntQuery.getPoints(plugin, sender));
        }
    }

    private void handleStats(Player sender, String[] args, int argStart) {
        if (args.length <= argStart) {
            sender.sendMessage(ChatColor.RED + "Usage: /mhadmin stats <player>");
            return;
        }

        OfflinePlayer playerToCheck = sender.getServer().getOfflinePlayerIfCached(args[argStart]);
        if (playerToCheck == null) {
            sender.sendMessage(plugin.config().getLangStringIsNotAValidPlayer());
            return;
        }

        hunterController.mobStatsResponse(sender, MobHuntQuery.killedMobStats(plugin, sender, playerToCheck.getUniqueId()));
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /mhadmin <clear|stats> [player]");
        player.sendMessage(ChatColor.GRAY + "Examples: /mhadmin clear Steve, /mhadmin stats Alex");
    }

    private record ParsedCommand(String subcommand, int argStart) {
    }
}
