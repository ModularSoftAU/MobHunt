package com.modularenigma.MobHunt.commands;

import com.modularenigma.MobHunt.MobHuntMain;
import com.modularenigma.MobHunt.HunterController;
import com.modularenigma.MobHunt.MobHuntQuery;
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

        List<MobHuntQuery.MobStat> stats;
        if (args.length > 0) {
            if (!plugin.isSenderAdmin(sender)) {
                sender.sendMessage(plugin.config().getLangInsufficientPermissions());
                return true;
            }

            OfflinePlayer playerToCheck = Bukkit.getServer().getOfflinePlayerIfCached(args[0]);
            if (playerToCheck == null) {
                sender.sendMessage(plugin.config().getLangStringIsNotAValidPlayer());
                return true;
            }

            stats = MobHuntQuery.killedMobStats(plugin, sender, playerToCheck.getUniqueId());
        } else {
            stats = MobHuntQuery.killedMobStats(plugin, player);
        }

        hunterController.mobCountResponse(player, stats);
        return true;
    }

}
