package org.modularsoft.MobHunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.modularsoft.MobHunt.HunterController;
import org.modularsoft.MobHunt.MobHuntMain;
import org.modularsoft.MobHunt.MobHuntQuery;

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

        List<String> leaderboardText;
        // If another argument is present, then we assume this command is asking for the
        // leaderboard of a specific Mob Type.
        if (args.length > 0) {
            // Since many Mob Types have a space in them (eg. Ender Dragon), we first
            // combine the arguments into one string.
            String mobType = String.join(" ", args);

            // This is a quick and dirty hack to check if the Mob Exists by looking to
            // see if its points are defined the config.
            if (plugin.config().getMobPoints(mobType) == null) {
                player.sendMessage(plugin.config().getLangLeaderboardStringNotAMob());
                return true;
            }

            // Get the best hunters by mobType.
            List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestMobTypeHunters(
                    plugin, player, showPlayers, mobType);

            // Show the leaderboard with a specific title.
            String leaderboardTitle = plugin.config().getLangLeaderboardMobTitleFormat()
                    .replace("%MobType%", mobType);
            leaderboardText = hunterController.getLeaderboardText(bestHunters, leaderboardTitle);
        } else {
            // This is for seeing the overall leaderboard for points.
            List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestHunters(
                    plugin, player, showPlayers);
            leaderboardText = hunterController.getLeaderboardText(bestHunters);
        }

        int centrePixel = HunterController.minecraftMessageLengthInPixels(
                plugin.config().getLangLeaderboardHeader()) / 2;
        for (String line: leaderboardText) {
            player.sendMessage(HunterController.centreMessage(line, centrePixel));
        }

        return true;
    }
}
