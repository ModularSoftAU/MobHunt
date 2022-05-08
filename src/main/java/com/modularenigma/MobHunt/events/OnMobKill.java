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

        // If the killcap has been reached, perform no further calculations for
        // a performance boost.
        int killedMobs = MobHuntQuery.killedMobTypeCount(plugin, player, mobType);
        int killCap = plugin.config().getKillCap();
        if (killedMobs == killCap) {
            // Only for the final kill, show the kill cap reached response to remind
            // the player to hunt other mobs.
            hunterController.mobKilledCapReachedResponse(player, mobType, killedMobs, killCap);
            return;
        } else if (killedMobs > killCap) {
            return;
        }

        /*
         * Visually: https://www.geogebra.org/classic/zjfdf79t
         *
         * calculatedPoints = (1 - log_n(killedMobs)) * basePoints     st. n == killCap + 1
         *
         *  OR
         *
         * calculatedPoints = sqrt(2 - 2^((killedMobs - 1) / killCap)) * basePoints
         *
         * This is more forgiving when killing the same enemy, right until you get
         * near the killCap, then it drops off quickly. Need to ensure killedMobs <= killCap
         * or the sqrt will have a negative number.
         *
         * Here we do the points calculation using the formula above.
         */
        int basePoints = plugin.config().getMobPoints(mobType);

        // Method A
        // double intermedium = Math.log(killedMobs) / Math.log(killCap + 1);
        // int calculatedPoints = (int) Math.max(0, Math.ceil((1 - intermedium) * basePoints));

        // Method B
        double intermedium = Math.pow(2, (killedMobs - 1) / (double)killCap);
        int calculatedPoints = (int) Math.ceil(Math.sqrt(2 - Math.min(2, intermedium)) * basePoints);

        // Add the points, but note what the points will be before and after the calculation.
        // This information can be used to calculate if a Milestone has been reached/surpassed.
        int oldPoints = MobHuntQuery.getPoints(plugin, player);
        int newPoints = oldPoints + calculatedPoints;

        MobHuntQuery.addPoints(plugin, player, calculatedPoints);

        hunterController.mobKilledResponse(player, mobType, calculatedPoints);
        scoreboardController.reloadScoreboard(player, newPoints);

        // Check to see if the player has equaled, or surpassed a Milestone. If so,
        // play that Milestone. eg.
        //
        // Milestones @ 5, 10, 15, 20
        // points = 7 ---> 17
        // Trigger Milestones 10, 15
        List<CollectionMilestone> milestones = plugin.config().getCollectionMilestones();
        for (int i = 0; i < milestones.size() - 1; i++) {
            CollectionMilestone curr = milestones.get(i);
            // This elimininates all Milestone less than the starting amount.
            // In the example, this is what eliminates Milestone 5 (as it should)
            // have been triggered already.
            if (curr.getPoints() <= oldPoints)
                continue;

            // Then we iterate through the rest of the Milestones
            for (int j = i; j < milestones.size(); j++) {
                curr = milestones.get(j);

                // This is our stopping condition. If we find a Milestone
                // whose points is greater than the new points, then we
                // don't yet have enough points to trigger those Milestones.
                // In the example, this is what prevents Milestone 20 from
                // triggering.
                if (curr.getPoints() > newPoints)
                    break;

                // Everything inbetween has therefore been surpassed, so
                // trigger the Milestone.
                curr.trigger(hunterController, player);
            }
            break;
        }
    }
}