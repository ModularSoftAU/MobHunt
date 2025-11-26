package dev.anchorlight.MobHunt.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import dev.anchorlight.MobHunt.*;

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

        // Check if a new player has joined
        boolean isNewHunter = MobHuntQuery.addNewHunter(plugin, player);
        if (isNewHunter) {
            if (plugin.config().isFeatureOnNewHunterConsoleMessageEnabled())
                plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");

            hunterController.newPlayerJoinsTheHunt(player);
        }

        // Refresh the scoreboard after ensuring the profile exists
        scoreboardController.reloadScoreboard(player, MobHuntQuery.getPoints(plugin, player));
    }
}