package com.modularenigma.MobHunt.commands;

import com.modularenigma.MobHunt.MobHuntMain;
import com.modularenigma.MobHunt.MobHuntScoreboardController;
import com.modularenigma.MobHunt.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class clearmobs implements CommandExecutor {
    private final MobHuntMain plugin;
    private final MobHuntChatController eggChatController;
    private final MobHuntScoreboardController scoreboardController;

    public clearmobs(MobHuntMain plugin, MobHuntChatController eggChatController, MobHuntScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
        this.scoreboardController = scoreboardController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        if (!sender.hasPermission("easteregghunt.clearegg") || !sender.isOp()) {
            sender.sendMessage(plugin.config().getLangInsufficientPermissions());
            return true;
        }

        if (!MobHuntQuery.clearEggs(plugin, player))
            return true;

        eggChatController.playerClearedTheirEggsResponse(player);
        scoreboardController.reloadScoreboard(player, MobHuntQuery.foundEggsCount(plugin, player));
        return true;
    }
}
