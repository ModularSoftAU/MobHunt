package com.modularenigma.MobHunt.commands;

import com.modularenigma.MobHunt.MobHuntMain;
import com.modularenigma.MobHunt.ScoreboardController;
import com.modularenigma.MobHunt.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        if (!sender.hasPermission(plugin.config().getAdminRole()) || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        if (MobHuntQuery.clearMobs(plugin, player)) {
            hunterController.playerClearedTheirPointsResponse(player);
            scoreboardController.reloadScoreboard(player, MobHuntQuery.getPoints(plugin, player));
        }
        return true;
    }
}
