package org.modularsoft.MobHunt;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobHuntQuery {
    public record MobHunter(@Getter String name, @Getter int points) { }
    public record MobStat(@Getter String mobType, @Getter int mobsKilled) { }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param player The player to check.
     * @param mobType The mobType to check.
     * @return The number of kills player has got of mobType.
     */
    public static int killedMobTypeCount(MobHuntMain plugin, Player player, String mobType) {
        return plugin.getPlayerDataStorage().getMobKillCount(player.getUniqueId(), mobType);
    }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param player The player to check.
     * @return The MobHunt statistics of the player.
     */
    public static List<MobStat> killedMobStats(MobHuntMain plugin, Player player) {
        return killedMobStats(plugin, player, player.getUniqueId());
    }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param sender he sender to send error messages to
     * @param playerUUID The player's uuid to retrieve the stats of
     * @return The MobHunt statistics of the player.
     */
    public static List<MobStat> killedMobStats(MobHuntMain plugin, CommandSender sender, UUID playerUUID) {
        return new ArrayList<>(plugin.getPlayerDataStorage().getMobStats(playerUUID));
    }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param player The player to check.
     * @return The number of points the player has.
     */
    public static int getPoints(MobHuntMain plugin, Player player) {
        return plugin.getPlayerDataStorage().getPoints(player.getUniqueId());
    }

    /**
     * Adds points to the players score.
     * @param plugin The MobHuntMain main plugin.
     * @param player The player to give points
     * @param points The number of points to give.
     */
    public static void addPoints(MobHuntMain plugin, Player player, int points) {
        plugin.getPlayerDataStorage().addPoints(player.getUniqueId(), points);
    }

    /**
     * Clears all the mobs killed of the player to 0
     * @param plugin The MobHuntMain main plugin.
     * @param player The player to clear.
     * @return Returns true if the clear was successful.
     */
    public static boolean clearMobs(MobHuntMain plugin, Player player) {
        return clearMobs(plugin, player, player.getUniqueId());
    }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param sender The sender to send error messages to.
     * @param playerUUID The player's uuid to clear the stats from.
     * @return Returns true if the clear was successful.
     */
    public static boolean clearMobs(MobHuntMain plugin, CommandSender sender, UUID playerUUID) {
        if (plugin.getPlayerDataStorage().clearPlayerData(playerUUID))
            return true;

        sender.sendMessage(plugin.config().getLangStorageError());
        return false;
    }

    /**
     * Increment the number of killed mobType mobs a player has.
     * @param plugin The MobHuntMain main plugin.
     * @param player The player who killed the mob.
     * @param mobType The mobType the player killed.
     */
    public static void incrementKilledMob(MobHuntMain plugin, Player player, String mobType) {
        plugin.getPlayerDataStorage().incrementMobKillCount(player.getUniqueId(), mobType);
    }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param player The player who joined.
     * @return Returns true if the player specified was indeed a new player.
     */
    public static boolean addNewHunter(MobHuntMain plugin, Player player) {
        return plugin.getPlayerDataStorage().ensurePlayerProfile(player.getUniqueId(), player.getName());
    }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param player The player who issued the command.
     * @param topHunters The limit of the size of the hunters list.
     * @return Returns a list of the Best Hunters. Index 0 is the best player and so on...
     */
    public static List<MobHunter> getBestHunters(MobHuntMain plugin, @Nullable Player player, int topHunters) {
        return new ArrayList<>(plugin.getPlayerDataStorage().getTopHunters(topHunters));
    }

    /**
     * @param plugin The MobHuntMain main plugin.
     * @param player The player who issued the command.
     * @param topHunters The limit of the size of the hunters list.
     * @param mobType The mobType to specifically query for.
     * @return Returns a list of the Best Hunters. Index 0 is the best player and so on...
     */
    public static List<MobHunter> getBestMobTypeHunters(MobHuntMain plugin, Player player, int topHunters, String mobType) {
        return new ArrayList<>(plugin.getPlayerDataStorage().getTopHuntersForMob(mobType, topHunters));
    }
}
