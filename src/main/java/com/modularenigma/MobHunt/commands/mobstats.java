package com.modularenigma.MobHunt.commands;

import com.modularenigma.MobHunt.MobHuntMain;
import com.modularenigma.MobHunt.HunterController;
import com.modularenigma.MobHunt.MobHuntQuery;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        hunterController.mobCountResponse(player, MobHuntQuery.killedMobStats(plugin, player));
        return true;
    }

}
