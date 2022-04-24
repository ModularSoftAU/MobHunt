package com.modularenigma.MobHunt.commands;

import com.modularenigma.MobHunt.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class leaderboard implements CommandExecutor {
    private final MobHuntMain plugin;
    private final MobHuntChatController eggChatController;

    public leaderboard(MobHuntMain plugin, MobHuntChatController eggChatController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        List<MobHuntQuery.EggHunter> bestHunters = MobHuntQuery.getBestHunters(plugin, player, 5);
        eggChatController.showLeaderBoardResponse(player, bestHunters);
        return true;
    }
}
