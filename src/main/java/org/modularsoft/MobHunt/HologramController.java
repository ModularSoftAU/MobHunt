package org.modularsoft.MobHunt;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class HologramController {
    private final MobHuntMain plugin;

    public HologramController(MobHuntMain plugin) {
        this.plugin = plugin;
    }

    public void createHunterLeaderBoardHologram(Player player) {
        if (DHAPI.getHologram("mh_leaderboard") != null)
            return;

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

        List<MobHuntQuery.MobHunter> bestHunters = MobHuntQuery.getBestHunters(
                plugin, player, plugin.config().getLeaderboardShowPlayers());

        DHAPI.addHologramLine(DHAPI.getHologram("mh_leaderboard"), "test");

        DHAPI.createHologram("mh_leaderboard", location, true);
    }

}
