package com.modularenigma.MobHunt.events;

import com.modularenigma.MobHunt.MobHuntMain;
import com.modularenigma.MobHunt.HunterController;
import com.modularenigma.MobHunt.MobHuntQuery;
import com.modularenigma.MobHunt.ScoreboardController;
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

        // Give the new player a scoreboard
        scoreboardController.reloadScoreboard(player, MobHuntQuery.foundEggsCount(plugin, player));

        // Check if a new player has joined
        if (MobHuntQuery.addNewHunter(plugin, player)) {
            if (plugin.config().isFeatureOnNewHunterConsoleMessageEnabled())
                plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");

            hunterController.newPlayerJoinsTheHunt(player);
        }
    }
}
