package org.modularsoft.MobHunt.commands;

import org.modularsoft.MobHunt.MobHuntMain;
import org.modularsoft.MobHunt.HunterController;
import org.modularsoft.MobHunt.MobHuntQuery;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class mobstats implements CommandExecutor {
    private final MobHuntMain plugin;
    private final HunterController hunterController;

    public mobstats(MobHuntMain plugin, HunterController hunterController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        // If another argument is present, then the player is trying to see the kill breakdown
        // of another player. Seeing your own stats is allowed, but seeing other people's stats
        // is only possible as an admin.
        List<MobHuntQuery.MobStat> stats;
        if (args.length > 0) {
            if (!plugin.isSenderAdmin(sender)) {
                sender.sendMessage(plugin.config().getLangInsufficientPermissions());
                return true;
            }

            // Lets us see the statistics of a player even if they are offline.
            OfflinePlayer playerToCheck = Bukkit.getServer().getOfflinePlayerIfCached(args[0]);
            if (playerToCheck == null) {
                sender.sendMessage(plugin.config().getLangStringIsNotAValidPlayer());
                return true;
            }

            stats = MobHuntQuery.killedMobStats(plugin, sender, playerToCheck.getUniqueId());
        } else {
            // See our own stats.
            stats = MobHuntQuery.killedMobStats(plugin, player);
        }

        hunterController.mobStatsResponse(player, stats);
        return true;
    }

}
