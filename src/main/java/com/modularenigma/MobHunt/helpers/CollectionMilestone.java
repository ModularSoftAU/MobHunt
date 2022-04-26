package com.modularenigma.MobHunt.helpers;

import com.modularenigma.MobHunt.HunterController;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class CollectionMilestone {
    private final int points;
    private final boolean isMajorSound;

    public CollectionMilestone(int points, boolean isMajorSound) {
        this.points = points;
        this.isMajorSound = isMajorSound;
    }

    public void trigger(HunterController hunterController, Player player, PlayerInteractEvent event) {
        hunterController.collectionMilestoneReachedResponse(player, isMajorSound, points);
        event.setCancelled(true);
    }
}