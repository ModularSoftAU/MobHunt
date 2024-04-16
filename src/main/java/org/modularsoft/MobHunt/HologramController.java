package org.modularsoft.MobHunt;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class HologramController {
    private final MobHuntMain plugin;
    private final HunterController hunterController;

    public HologramController(MobHuntMain plugin, HunterController hunterController) {
        this.plugin = plugin;
        this.hunterController = hunterController;
    }

    public void reloadHunterLeaderboard() {
        Hologram leaderboardHologram = DHAPI.getHologram("mh_leaderboard");
        if (leaderboardHologram == null) {
            double x = plugin.config().getHologramLocationX();
            double y = plugin.config().getHologramLocationY();
            double z = plugin.config().getHologramLocationZ();
            String worldName = plugin.config().getHologramLocationWorld();

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("World '" + worldName + "' not found.");
                return;
            }

            Location location = new Location(world, x, y, z);
            leaderboardHologram = DHAPI.createHologram("mh_leaderboard", location, false);
        }

        List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestHunters(
                plugin, null, 10);
        List<String> lines = hunterController.getLeaderboardText(bestHunters);
        DHAPI.setHologramLines(leaderboardHologram, lines);
    }
}
