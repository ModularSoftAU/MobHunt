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
        String mobType = deadEntity.getName();
        Player player = deadEntity.getKiller();
        if (player == null)
            return;

        MobHuntQuery.incrementKilledMob(plugin, player, mobType);

        int killedMobs = MobHuntQuery.killedMobTypeCount(plugin, player, mobType);
        int killCap = plugin.config().getKillCap();
        if (killedMobs == killCap) {
            hunterController.mobKilledCapReachedResponse(player, mobType, killedMobs, killCap);
            return;
        } else if (killedMobs > killCap) {
            return;
        }

        // Visually: https://www.geogebra.org/classic/zjfdf79t
        //
        // (1 - log_n(killedMobs)) * points     st. n == killCap + 1
        //
        //  OR
        //
        // sqrt(2 - 2^((killedMobs - 1) / killCap)) * points
        //
        // This is more forgiving when killing the same enemy, right until you get
        // near the killCap, then it drops off quickly. Need to ensure x <= killCap
        // or the sqrt will have a negative number.
        int basePoints = plugin.config().getMobPoints(mobType);
        double intermedium = Math.log(killedMobs) / Math.log(killCap + 1);
        int calculatedPoints = (int) Math.max(0, Math.ceil((1 - intermedium) * basePoints));

        // Used for Milestones.
        int oldPoints = MobHuntQuery.getPoints(plugin, player);
        MobHuntQuery.addPoints(plugin, player, calculatedPoints);
        int newPoints = oldPoints + calculatedPoints;

        hunterController.mobKilledResponse(player, mobType, calculatedPoints);
        scoreboardController.reloadScoreboard(player, newPoints);

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