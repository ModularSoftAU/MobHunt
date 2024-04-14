package org.modularsoft.MobHunt.commands;

import org.modularsoft.MobHunt.MobHuntMain;
import org.modularsoft.MobHunt.ScoreboardController;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.modularsoft.MobHunt.HunterController;
import org.modularsoft.MobHunt.MobHuntQuery;

public class mobclear implements CommandExecutor {
    private final MobHuntMain plugin;
    private final HunterController hunterController;
    private final ScoreboardController scoreboardController;

    public mobclear(MobHuntMain plugin, HunterController hunterController, ScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
        this.scoreboardController = scoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        // Only an admin can use this command.
        if (!plugin.isSenderAdmin(sender)) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        // If another argument is present, clear the stats of another player.
        // By using OfflinePlayer, we can remove the stats of a player who is not
        // currently online.
        if (args.length > 0) {
            // Check to see if the player has logged in before.
            OfflinePlayer toClear = Bukkit.getServer().getOfflinePlayerIfCached(args[0]);
            if (toClear == null) {
                sender.sendMessage(plugin.config().getLangStringIsNotAValidPlayer());
                return true;
            }

            // Clear the offlinePlayers stats
            if (MobHuntQuery.clearMobs(plugin, sender, toClear.getUniqueId())) {
                hunterController.playerClearedTheirPointsResponse(sender, toClear.getName());

                // If the player is also online, reload their scoreboard.
                Player onlinePlayer = toClear.getPlayer();
                if (onlinePlayer != null)
                    scoreboardController.reloadScoreboard(onlinePlayer, MobHuntQuery.getPoints(plugin, onlinePlayer));
            }
        } else if (MobHuntQuery.clearMobs(plugin, player)) {
            // This is for clearing your own points.
            hunterController.playerClearedTheirPointsResponse(player);
            scoreboardController.reloadScoreboard(player, MobHuntQuery.getPoints(plugin, player));
        }
        return true;
    }
}
