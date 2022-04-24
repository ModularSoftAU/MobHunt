package com.modularenigma.MobHunt.events;

import com.modularenigma.MobHunt.*;
import com.modularenigma.MobHunt.helpers.MobHuntMilestone;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;

public class MobKillEvent implements Listener {
    private final MobHuntMain plugin;
    private final MobHuntChatController eggChatController;
    private final MobHuntScoreboardController eggScoreboardController;
    private final Map<Integer, MobHuntMilestone> milestones;

    public MobKillEvent(MobHuntMain plugin,
                        MobHuntChatController eggChatController,
                        MobHuntScoreboardController eggScoreboardController) {
        this.plugin = plugin;
        this.eggChatController = eggChatController;
        this.eggScoreboardController = eggScoreboardController;
        this.milestones = plugin.config().getEggMilestones();
    }

    @EventHandler
    public void onEggFind(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller().getPlayer();

        MobHuntQuery.insertKilledMob(plugin, player, event.getEntity().getName());

//        if (!isFindEggEvent(event))
//            return;
//
//        event.setCancelled(true);
//
//        Player player = event.getPlayer();
//        Block block = event.getClickedBlock();
//        int x = block.getX(); // Can't be null. Would have been found by isFindEggEvent
//        int y = block.getY();
//        int z = block.getZ();
//        if (EggQuery.hasAlreadyCollectedEgg(plugin, player, x, y, z)) {
//            eggChatController.eggAlreadyFoundResponse(player);
//            return;
//        }
//
//        eggWorldController.playerCollectedEgg(player, block, x, y, z);
//
//        int foundEggs = EggQuery.foundEggsCount(plugin, player);
//        eggScoreboardController.reloadScoreboard(player, foundEggs);
//
//        if (foundEggs == 1) {
//            eggChatController.eggMilestoneReachedEvent(player, false, foundEggs);
//            return;
//        }
//
//        // Trigger any milestones if they are relevant. We'll use the milestone text if it's available
//        // otherwise we'll draw the default text to the screen.
//        if (milestones.containsKey(foundEggs)) {
//            milestones.get(foundEggs).trigger(eggChatController, eggHatController, player, event);
//        } else {
//            eggChatController.eggFoundResponse(player, foundEggs);
//        }
    }
}