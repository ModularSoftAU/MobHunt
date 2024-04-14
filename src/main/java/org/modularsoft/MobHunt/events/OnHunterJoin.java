package org.modularsoft.MobHunt.events;

import org.modularsoft.MobHunt.MobHuntMain;
import org.modularsoft.MobHunt.HunterController;
import org.modularsoft.MobHunt.MobHuntQuery;
import org.modularsoft.MobHunt.ScoreboardController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnHunterJoin implements Listener {
    private final MobHuntMain plugin;
    private final HunterController hunterController;
    private final ScoreboardController scoreboardController;

    public OnHunterJoin(MobHuntMain plugin, HunterController hunterController,
                        ScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
        this.scoreboardController = scoreboardController;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        // Refresh the scoreboard
        scoreboardController.reloadScoreboard(player, MobHuntQuery.getPoints(plugin, player));

        // Check if a new player has joined
        if (MobHuntQuery.addNewHunter(plugin, player)) {
            if (plugin.config().isFeatureOnNewHunterConsoleMessageEnabled())
                plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");

            hunterController.newPlayerJoinsTheHunt(player);
        }
    }
}
