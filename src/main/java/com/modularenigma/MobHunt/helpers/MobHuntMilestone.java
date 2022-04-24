package com.modularenigma.MobHunt.helpers;

import com.modularenigma.MobHunt.MobHuntChatController;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class MobHuntMilestone {
    public static final MobHuntMilestone FIRST_EGG = new MobHuntMilestone(1, false);
    private final int atEggsFound;
    private final boolean isMajorSound;

    public MobHuntMilestone(int atEggsFound, boolean isMajorSound) {
        this.atEggsFound = atEggsFound;
        this.isMajorSound = isMajorSound;
    }

    public void trigger(MobHuntChatController eggChatController, Player player, PlayerInteractEvent event) {
        eggChatController.eggMilestoneReachedEvent(player, isMajorSound, atEggsFound);
        event.setCancelled(true);
    }
}