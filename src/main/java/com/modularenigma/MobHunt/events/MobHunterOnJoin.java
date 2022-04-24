package com.modularenigma.MobHunt.events;

import com.modularenigma.MobHunt.MobHuntMain;
import com.modularenigma.MobHunt.MobHuntChatController;
import com.modularenigma.MobHunt.MobHuntQuery;
import com.modularenigma.MobHunt.MobHuntScoreboardController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MobHunterOnJoin implements Listener {
    private final MobHuntMain plugin;
    private final MobHuntChatController eggChatController;
    private final MobHuntScoreboardController eggScoreboardController;

    public MobHunterOnJoin(MobHuntMain plugin, MobHuntChatController eggChatController,
                           MobHuntScoreboardController eggScoreboardController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
        this.eggScoreboardController = eggScoreboardController;
    }

    @EventHandler
    public void onMobHunterJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();

        // Give the new player a scoreboard
        eggScoreboardController.reloadScoreboard(player, MobHuntQuery.foundEggsCount(plugin, player));

        if (MobHuntQuery.addNewHunter(plugin, player)) {
            // New player joined
            plugin.getServer().getConsoleSender().sendMessage(username + " is a new player, creating a player profile.");
            plugin.getServer().getConsoleSender().sendMessage("Added a new hunter, " + username + ".");
            eggChatController.newPlayerJoinsTheHunt(player);
        }
    }
}
