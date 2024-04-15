package org.modularsoft.MobHunt;

import org.modularsoft.MobHunt.helpers.DefaultFontInfo;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HunterController {
    private final MobHuntMain plugin;

    public HunterController(MobHuntMain plugin) {
        this.plugin = plugin;
    }

    /**
     * For when the player kills a Mob.
     * @param player The player who killed the mob.
     * @param mobType The mobType the player killed.
     * @param points The number of points awarded for killing the mob.
     */
    public void mobKilledResponse(Player player, String mobType, int points) {
        player.sendMessage(plugin.config().getLangMobKilled()
                .replace("%MobType%", mobType)
                .replace("%Points%", "" + points)
                .replace("%Plural%", points == 1 ? "" : "s"));
    }

    /**
     * For when the player has killed cap number of mobType mobs. This notifies
     * the player that they will no longer receive points for killing mobType
     * @param player The player who killed the mob
     * @param mobType The mobType the player killed
     * @param kills The number of kills the player has gotten for said mobtype
     * @param killCap The killCap threshold
     */
    public void mobKilledCapReachedResponse(Player player, String mobType, int kills, int killCap) {
        player.sendMessage(plugin.config().getLangMobKilledCapReached()
                .replace("%MobType%", mobType)
                .replace("%Kills%", "" + kills)
                .replace("%Cap%", "" + killCap));
    }

    /**
     * For when the player wants to see a breakdown of the kills they have gotten
     * across all the mobs.
     * @param player The player to send the stats to.
     * @param stats A (presorted) list of of stats to render.
     */
    public void mobStatsResponse(Player player, List<MobHuntQuery.MobStat> stats) {
        int centrePixel = minecraftMessageLengthInPixels(
                plugin.config().getLangLeaderboardHeader()) / 2;

        String title = plugin.config().getLangStatsTitle()
                .replace("%Player%", player.getName());

        player.sendMessage(centreMessage(title, centrePixel));

        for (MobHuntQuery.MobStat stat : stats) {
            String statMessage = plugin.config().getLangStatsFormat()
                    .replace("%MobType%", stat.mobType())
                    .replace("%Kills%", "" + stat.mobsKilled());
            player.sendMessage(centreMessage(statMessage, centrePixel));
        }
    }

    /**
     * For when a player wants to see the rules of MobHunt.
     * @param sender The sender to tell the rules to.
     */
    public void mobHelpResponse(CommandSender sender) {
        for (String s : plugin.config().getLangMobHelp())
            sender.sendMessage(s);
    }

    /**
     * For when a player reaches a points Milestone. This tells other people in
     * the world when the player reaches a Milestone. If it is a Major Milestone,
     * then the sound changes, but other players will only hear the minor milestone
     * sound. They will only hear the sound if they are closeby to the player who
     * achieved the Milestone.
     * @param player The player who achieved the Milestone.
     * @param isMajorSound Whether the player surpasssed a Major Milestone or not.
     * @param milestonePoints The number of points associated with the Milestone.
     */
    public void collectionMilestoneReachedResponse(Player player, boolean isMajorSound, int milestonePoints) {
        if (!plugin.config().isFeatureMilestoneMessageEnabled())
            return;

        // TODO: Congratulate the player on reaching a milestone. On major milestones
        //       notify other players as well. Revise and test this.
        Sound majorSound = plugin.config().getSoundMajorCollectionMilestone();
        Sound minorSound = plugin.config().getSoundMinorCollectionMilestone();

        if (isMajorSound)
            player.playSound(player.getLocation(), majorSound, 1, 1);
        else
            player.playSound(player.getLocation(), minorSound, 1, 1);

        // Tell other players about the milestone
        String broadcastMessage = plugin.config().getLangCollectionMilestoneReached()
                .replace("%Player%", player.getName())
                .replace("%Points%", "" + milestonePoints);

        // TODO: Revise and test this. Other players should be able to hear the
        //       milestone. If not, then this needs to change AND the PlayerHeadHunt
        //       plugin code.
        player.getWorld().playSound(player.getLocation(), minorSound, 1, 1);
        for (Player otherPlayers : Bukkit.getOnlinePlayers())
            otherPlayers.sendMessage(broadcastMessage);
    }

    /**
     * When a new player joins tell them the rules.
     * @param player The player who joined.
     */
    public void newPlayerJoinsTheHunt(Player player) {
        for (String s : plugin.config().getLangNewHunter())
            player.sendMessage(s.replace("%Player%", player.getName()));
    }

    /**
     * When a player clears their own points.
     * @param player The player who cleared their own points.
     */
    public void playerClearedTheirPointsResponse(Player player) {
        playerClearedTheirPointsResponse(player, player.getName());
    }

    /**
     * When a sender wants to clear the points of another player silently,
     * they would call this function. Only the sender is notified of the
     * clear.
     * @param messageRecipient The person to send the cleared message to.
     * @param aboutPlayerName The player whose points were cleared.
     */
    public void playerClearedTheirPointsResponse(CommandSender messageRecipient, String aboutPlayerName) {
        messageRecipient.sendMessage(plugin.config().getLangOnMobClear()
                .replace("%Player%", aboutPlayerName));
    }

    /**
     * From: https://stackoverflow.com/a/6810409
     * This function converts a rank to its ordinal representation
     * @param rank Rank to convert to ordinal.
     * @return The oridinal String.
     */
    private static String rankToOrdinal(int rank) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        return switch (rank % 100) {
            case 11, 12, 13 -> rank + "th";
            default -> rank + suffixes[rank % 10];
        };
    }

    /**
     * From: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * @param message The message to measure its length.
     * @return The length of the message in pixels (" " characters).
     */
    public static int minecraftMessageLengthInPixels(String message) {
        if (message == null || message.equals(""))
            return 0;

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§'){
                previousCode = true;
            } else if (previousCode){
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        return messagePxSize;
    }

    /**
     * From: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * @param pixels The number of pixels to pad.
     * @return The padded String.
     */
    private static String getPixelPadding(int pixels) {
        StringBuilder sb = new StringBuilder();
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        while (compensated < pixels){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString();
    }

    /**
     * From: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * Sends the message to the chat such that it is in the middle of the chat box.
     * @param message The message to send.
     * @param centrePixel The pixel to treat as the centre of the message.
     */
    public static String centreMessage(String message, int centrePixel) {
        int messagePxSize = minecraftMessageLengthInPixels(message);
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = centrePixel - halvedMessageSize;
        return getPixelPadding(toCompensate) + message;
    }

    /**
     * For when a player wants to see who the best hunters are.
     * @param bestHunters A (presorted) list of stats to show on the leaderboard.
     */
    public List<String> getLeaderboardText(List<MobHuntQuery.MobHunter> bestHunters) {
        return getLeaderboardText(bestHunters, null);
    }

    /**
     * When you want to see the leaderboard with a heading of a specific mob.
     * @param bestHunters A (presorted) list of stats to show on the leaderboard.
     * @param title The title to give the leaderboard.
     */
    public List<String> getLeaderboardText(List<MobHuntQuery.MobHunter> bestHunters, String title) {
        List<String> lines = new ArrayList<>();

        // Show the header first
        lines.add(plugin.config().getLangLeaderboardHeader());
        lines.add("");

        if (title != null)
            lines.add(title);

        if (bestHunters.size() == 0) {
            // If there are no hunters we should probably tell the player
            lines.add(plugin.config().getLangLeaderboardNoMobsKilled());
        } else {
            for (int i = 0; i < bestHunters.size(); i++) {
                MobHuntQuery.MobHunter hunter = bestHunters.get(i);

                // We probably shouldn't list players who have no points.
                // Once we find a player with 0 points then the rest will
                // also have 0 as it is sorted.
                if (hunter.points() == 0)
                    break;

                int rank = i + 1;
                String rankingColour = switch (rank) {
                    case 1 -> plugin.config().getLangLeaderboardFirstColor();
                    case 2 -> plugin.config().getLangLeaderboardSecondColor();
                    case 3 -> plugin.config().getLangLeaderboardThirdColor();
                    default -> plugin.config().getLangLeaderboardOtherColor();
                };

                String rankingMessage = plugin.config().getLangLeaderboardFormat()
                        .replace("%Color%", rankingColour)
                        .replace("%Ranking%", rankToOrdinal(rank))
                        .replace("%Player%", hunter.name())
                        .replace("%Points%", "" + hunter.points());
                lines.add(rankingMessage);
            }
        }

        lines.add("");
        lines.add(plugin.config().getLangLeaderboardHeader());
        return lines;
    }
}
