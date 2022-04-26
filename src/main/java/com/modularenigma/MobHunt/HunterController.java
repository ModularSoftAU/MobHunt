package com.modularenigma.MobHunt;

import com.modularenigma.MobHunt.helpers.DefaultFontInfo;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class HunterController {
    private final MobHuntMain plugin;

    public HunterController(MobHuntMain plugin) {
        this.plugin = plugin;
    }

    public void collectionMilestoneReachedResponse(Player player, boolean isMajorSound, int points) {
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
                .replace("%Points%", "" + points);

        // TODO: Consider adding a local notification for a player killing their first
        //       mob. Revise and test this. Other players should be able to hear the
        //       milestone. If not, then this needs to change AND the PlayerHeadHunt
        //       plugin code.
        player.getWorld().playSound(player.getLocation(), minorSound, 1, 1);
        for (Player otherPlayers : Bukkit.getOnlinePlayers())
            otherPlayers.sendMessage(broadcastMessage);
    }

    /**
     * When a new player joins.
     * @param player The player who joined.
     */
    public void newPlayerJoinsTheHunt(Player player) {
        for (String s : plugin.config().getLangNewHunter())
            player.sendMessage(s.replace("%Player%", player.getName()));
    }

    /**
     * When a player wants to see a breakdown of their points.
     * @param player The player to respond to.
     */
    public void playersOwnPointsResponse(Player player) {
        // TODO: Need to introduce a format of showing these statistics to the player
        //       Potentially need a different to command to show a breakdown and/or
        //       only the points. Needs further testing and design.
//        player.sendMessage(plugin.config().getLangPoints()
//                .replace("%FOUNDEGGS%", "" + MobHuntQuery.foundEggsCount(plugin, player))
//                .replace("%NUMBEROFEGGS%", "" + plugin.config().getTotalEggs()));
    }

    public void playerClearedTheirPointsResponse(Player player) {
        player.sendMessage(plugin.config().getLangOnMobClear()
                .replace("%Player%", player.getName()));
    }

    /**
     * From: https://stackoverflow.com/a/6810409
     * This function converts a rank to its ordinal representation
     * @param rank Rank to convert to ordinal
     * @return The oridinal String
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
     * @param message The message to measure its length
     * @return The length of the message in pixels (" " characters)
     */
    private static int minecraftMessageLengthInPixels(String message) {
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
     * @param pixels The number of pixels to pad
     * @return The padded String
     */
    private static String getPixelPadding(int pixels) {
        StringBuilder sb = new StringBuilder();
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        while(compensated < pixels){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString();
    }

    /**
     * From: https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
     * Sends the message to the chat such that it is in the middle of the chat box.
     * @param player The player sending the message
     * @param message The message to send
     * @param centrePixel The pixel to treat as the centre of the message
     */
    private static void sendMessageInCentre(Player player, String message, int centrePixel) {
        int messagePxSize = minecraftMessageLengthInPixels(message);
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = centrePixel - halvedMessageSize;
        player.sendMessage(getPixelPadding(toCompensate) + message);
    }

    public void showLeaderBoardResponse(Player player, List<MobHuntQuery.MobHunter> bestHunters) {
        int centrePixel = minecraftMessageLengthInPixels(
                plugin.config().getLangLeaderboardHeader()) / 2;

        // Show the header first
        sendMessageInCentre(player, plugin.config().getLangLeaderboardHeader(), centrePixel);
        player.sendMessage("");

        if (bestHunters.size() == 0) {
            // If there are no hunters we should probably tell the player
            sendMessageInCentre(player, plugin.config().getLangLeaderboardNoMobsKilled(), centrePixel);
        } else {
            for (int i = 0; i < bestHunters.size(); i++) {
                MobHuntQuery.MobHunter hunter = bestHunters.get(i);

                // We probably shouldn't list players who have no points.
                // Once we find a player with 0 points then the rest will
                // also have 0 as it is sorted.
                if (hunter.points() == 0)
                    return;

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
                sendMessageInCentre(player, rankingMessage, centrePixel);
            }
        }

        player.sendMessage("");
        sendMessageInCentre(player, plugin.config().getLangLeaderboardHeader(), centrePixel);
    }
}
