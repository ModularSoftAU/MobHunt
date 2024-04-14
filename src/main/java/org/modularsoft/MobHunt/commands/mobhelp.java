package org.modularsoft.MobHunt.commands;

import org.modularsoft.MobHunt.HunterController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class mobhelp implements CommandExecutor {
    private final HunterController hunterController;

    public mobhelp(HunterController hunterController) {
        this.hunterController = hunterController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        hunterController.mobHelpResponse(sender);
        return true;
    }

}
