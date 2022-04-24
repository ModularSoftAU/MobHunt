package com.modularenigma.MobHunt.commands;

import com.modularenigma.MobHunt.MobHuntMain;
import com.modularenigma.MobHunt.MobHuntChatController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class mobs implements CommandExecutor {
    private final MobHuntMain plugin;
    private final MobHuntChatController eggChatController;

    public mobs(MobHuntMain plugin, MobHuntChatController eggChatController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.config().getLangNotAPlayer());
            return true;
        }

        eggChatController.playersOwnEggCountResponse(player);
        return true;
    }

}
