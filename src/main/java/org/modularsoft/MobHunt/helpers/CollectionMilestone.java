package org.modularsoft.MobHunt.helpers;

import org.modularsoft.MobHunt.HunterController;
import lombok.Getter;
import org.bukkit.entity.Player;

public class CollectionMilestone {
    @Getter
    private final int points;
    private final boolean isMajorSound;

    public CollectionMilestone(int points, boolean isMajorSound) {
        this.points = points;
        this.isMajorSound = isMajorSound;
    }

    public void trigger(HunterController hunterController, Player player) {
        hunterController.collectionMilestoneReachedResponse(player, isMajorSound, points);
    }
}