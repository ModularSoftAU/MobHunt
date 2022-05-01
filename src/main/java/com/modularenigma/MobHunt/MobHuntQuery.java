package com.modularenigma.MobHunt;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MobHuntQuery {
    public record MobHunter(@Getter String name, @Getter int points) { }

    /**
     * @param plugin The EasterEggHunt main plugin
     * @param player The player to check
     * @return Returns the number of eggs found by the player
     */
    public static int killedMobsCount(MobHuntMain plugin, Player player, String mobType) {
        String playerUUID = "" + player.getUniqueId();

        try {
            // Check how many eggs the player has collected.
            PreparedStatement foundEggsCount = plugin.getConnection().prepareStatement(
                    "SELECT mobsKilled FROM mobs WHERE uuid=? AND mobType=?");
            foundEggsCount.setString(1, playerUUID);
            foundEggsCount.setString(2, mobType);
            ResultSet results = foundEggsCount.executeQuery();

            if (results.next())
                return results.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return 0;
    }

    public static int getPoints(MobHuntMain plugin, Player player) {
        String playerUUID = "" + player.getUniqueId();

        try {
            // Check how many eggs the player has collected.
            PreparedStatement getPoints = plugin.getConnection().prepareStatement(
                    "SELECT n FROM points WHERE uuid=?");
            getPoints.setString(1, playerUUID);
            ResultSet results = getPoints.executeQuery();

            if (results.next())
                return results.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return 0;
    }

    public static void addPoints(MobHuntMain plugin, Player player, int points) {
        String playerUUID = "" + player.getUniqueId();

        try {
            // Check how many eggs the player has collected.
            PreparedStatement getPoints = plugin.getConnection().prepareStatement(
                    "UPDATE points SET n = n + ? WHERE uuid=?");
            getPoints.setInt(1, points);
            getPoints.setString(2, playerUUID);
            getPoints.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
    }

    /**
     * Clears the number of eggs found by the player to 0
     * @param plugin The EasterEggHunt main plugin
     * @param player The player to reset
     * @return Returns true if the clear was successful.
     */
    public static boolean clearMobs(MobHuntMain plugin, Player player) {
        String playerUUID = "" + player.getUniqueId();

        try {
            PreparedStatement clearEggsStatement = plugin.getConnection().prepareStatement(
                    "DELETE FROM mobs WHERE uuid=?");
            clearEggsStatement.setString(1, playerUUID);
            clearEggsStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return false;
    }

    /**
     * Insert killed mob into database
     * @param plugin The MobHunt main plugin
     * @param player The player who found the egg
     * @param mobType The type of mob that was killed
     * @return The number of mobs killed
     */
    public static int insertKilledMob(MobHuntMain plugin, Player player, String mobType) {
        String playerUUID = "" + player.getUniqueId();

        try {
            // Check if a player has been added into the database already.
            PreparedStatement findStatement = plugin.getConnection().prepareStatement(
                    "SELECT mobsKilled FROM mobs WHERE uuid=? AND mobType=?");
            findStatement.setString(1, playerUUID);
            findStatement.setString(2, mobType);
            ResultSet results = findStatement.executeQuery();

            // The player already exists
            if (results.next()) {
                int nextKilled = results.getInt(1) + 1;
                PreparedStatement updateMobCount = plugin.getConnection().prepareStatement(
                        "UPDATE mobs SET mobsKilled = mobsKilled + 1 WHERE uuid=? AND mobType=?");
                updateMobCount.setString(1, playerUUID);
                updateMobCount.setString(2, mobType);
                updateMobCount.executeUpdate();
                return nextKilled;
            } else {
                PreparedStatement insertMobCount = plugin.getConnection().prepareStatement(
                        "INSERT INTO mobs (uuid, mobType) VALUES (?, ?)");
                insertMobCount.setString(1, playerUUID);
                insertMobCount.setString(2, mobType);
                insertMobCount.executeUpdate();
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return 0;
    }

    /**
     * @param plugin The EasterEggHunt main plugin
     * @param player The player who joined
     * @return Returns true if the player specified was indeed a new player.
     */
    public static boolean addNewHunter(MobHuntMain plugin, Player player) {
        String playerUUID = "" + player.getUniqueId();
        String username = player.getName();

        try {
            // Check if a player has been added into the database already.
            PreparedStatement findstatement = plugin.getConnection().prepareStatement(
                    "SELECT * FROM points WHERE uuid=?");
            findstatement.setString(1, playerUUID);
            ResultSet results = findstatement.executeQuery();

            // The player already exists
            if (results.next())
                return false;

            PreparedStatement newPlayerStatement = plugin.getConnection().prepareStatement(
                    "INSERT INTO points (uuid, username) VALUES (?, ?)");
            newPlayerStatement.setString(1, playerUUID);
            newPlayerStatement.setString(2, username);
            newPlayerStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return false;
    }

    /**
     * @param plugin The EasterEggHunt main plugin
     * @param player The player who issued the command
     * @return Returns a list of the Best Hunters. idx 0 is the best player and so on...
     */
    public static List<MobHunter> getBestHunters(MobHuntMain plugin, Player player, int topHunters) {
        List<MobHunter> bestHunters = new ArrayList<>();

        try {
            // Check if a player has been added into the database already.
            PreparedStatement bestHuntersStatement = plugin.getConnection().prepareStatement(
                    "SELECT username, n FROM points ORDER BY n DESC LIMIT ?");
            bestHuntersStatement.setInt(1, topHunters);
            ResultSet results = bestHuntersStatement.executeQuery();

            // The player already exists
            while (results.next()) {
                String name = results.getString(1);
                int points = results.getInt(2);
                bestHunters.add(new MobHunter(name, points));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(plugin.config().getLangDatabaseConnectionError());
        }
        return bestHunters;
    }
}
