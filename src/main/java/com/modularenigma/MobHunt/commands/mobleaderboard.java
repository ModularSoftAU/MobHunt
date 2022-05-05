package com.modularenigma.MobHunt.commands;

import com.modularenigma.MobHunt.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class mobleaderboard implements CommandExecutor {
    private final MobHuntMain plugin;
    private final HunterController hunterController;

    public mobleaderboard(MobHuntMain plugin, HunterController hunterController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        int showPlayers = plugin.config().getLeaderboardShowPlayers();
        if (args.length > 0) {
            String mobType = String.join(" ", args);
            if (plugin.config().getMobPoints(mobType) == null) {
                player.sendMessage(plugin.config().getLangLeaderboardStringNotAMob());
                return true;
            }

            List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestMobTypeHunters(
                    plugin, player, showPlayers, mobType);
            String leaderboardTitle = plugin.config().getLangLeaderboardMobTitleFormat()
                    .replace("%MobType%", mobType);
            hunterController.showLeaderBoardResponse(player, bestHunters, leaderboardTitle);
        } else {
            List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestHunters(
                    plugin, player, showPlayers);
            hunterController.showLeaderBoardResponse(player, bestHunters);
        }
        return true;
    }
}
