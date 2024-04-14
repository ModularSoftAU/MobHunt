package org.modularsoft.MobHunt.events;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.modularsoft.MobHunt.*;

import static org.bukkit.Bukkit.getServer;

public class OnHunterJoin implements Listener {
    private final MobHuntMain plugin;
    private final HunterController hunterController;
    private final ScoreboardController scoreboardController;
    private final HologramController hologramController;

    public OnHunterJoin(MobHuntMain plugin, HunterController hunterController,
                        ScoreboardController scoreboardController, HologramController hologramController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
        this.scoreboardController = scoreboardController;
        this.hologramController = hologramController;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        // Refresh the scoreboard
        scoreboardController.reloadScoreboard(player, MobHuntQuery.getPoints(plugin, player));

        // Setup and display the leaderboard hologram
        Location hologramLocation = new Location(getServer().getWorld("world"), 229, 71, 93);
        DHAPI.createHologram("my_hologram", hologramLocation, true);

        // Check if a new player has joined
        if (MobHuntQuery.addNewHunter(plugin, player)) {
            if (plugin.config().isFeatureOnNewHunterConsoleMessageEnabled())
                plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");

            hunterController.newPlayerJoinsTheHunt(player);
        }
    }
}