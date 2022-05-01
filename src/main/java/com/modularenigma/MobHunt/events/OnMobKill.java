package com.modularenigma.MobHunt.events;

import com.modularenigma.MobHunt.*;
import com.modularenigma.MobHunt.helpers.CollectionMilestone;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

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

        String mobType = deadEntity.getName();

        int mobsKilledCap = 50;
        MobHuntQuery.incrementKilledMob(plugin, player, mobType);

        int killedMobs = MobHuntQuery.killedMobTypeCount(plugin, player, mobType);
        if (killedMobs > mobsKilledCap) {
            hunterController.mobKilledCapReachedResponse(player, mobType, killedMobs, mobsKilledCap);
            return;
        }

        // (1 - log_n(killedMobs)) * points
        // Where n = mobKilledCap + 1
        // https://www.geogebra.org/classic/zjfdf79t
        int basePoints = plugin.config().getMobPoints(mobType);
        double intermedium = Math.log(killedMobs) / Math.log(mobsKilledCap + 1);
        int calculatedPoints = (int) Math.max(0, Math.ceil((1 - intermedium) * basePoints));

        // Used for Milestones.
        int oldPoints = MobHuntQuery.getPoints(plugin, player);
        MobHuntQuery.addPoints(plugin, player, calculatedPoints);
        int newPoints = oldPoints + calculatedPoints;

        hunterController.mobKilledResponse(player, mobType, calculatedPoints);
        scoreboardController.reloadScoreboard(player, newPoints);

        // TODO: Include a milestone trigger if it is relevant.
        List<CollectionMilestone> milestones = plugin.config().getCollectionMilestones();
        for (int i = 0; i < milestones.size() - 1; i++) {
            CollectionMilestone curr = milestones.get(i);
            if (curr.getPoints() < oldPoints)
                continue;

            for (int j = i; j < milestones.size(); j++) {
                curr = milestones.get(j);
                if (newPoints <= curr.getPoints())
                    break;

                curr.trigger(hunterController, player);
            }
            break;
        }
    }
}