package com.modularenigma.MobHunt.events;

import com.modularenigma.MobHunt.*;
import com.modularenigma.MobHunt.helpers.CollectionMilestone;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

public class OnMobKill implements Listener {
    private final MobHuntMain plugin;
    private final HunterController hunterController;
    private final ScoreboardController scoreboardController;

    public OnMobKill(MobHuntMain plugin,
                     HunterController hunterController,
                     ScoreboardController scoreboardController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
        this.scoreboardController = scoreboardController;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        Player player = deadEntity.getKiller();
        if (player == null)
            return;

        int points = plugin.getConfig().getInt("MobHunt.Points." + deadEntity.getName());

        // TODO: Do some maths to ensure we aren't querying past the cap
        MobHuntQuery.insertKilledMob(plugin, player, deadEntity.getName());

        int killedMobs = MobHuntQuery.killedMobsCount(plugin, player, deadEntity.getName());
        plugin.getServer().getConsoleSender().sendMessage(
                player.getName() + " killed their " + killedMobs + deadEntity.getName() + " for " + points + " points");

        MobHuntQuery.addPoints(plugin, player, points);
        scoreboardController.reloadScoreboard(player, MobHuntQuery.getPoints(plugin, player));

        // TODO: Include a response based on the above condition:
        //       Reached the cap or received points.
        hunterController.mobKilledResponse(player, deadEntity.getName(), points);

        // TODO: Include a milestone trigger if it is relevant.
        Map<Integer, CollectionMilestone> milestones = plugin.config().getCollectionMilestones();
    }
}