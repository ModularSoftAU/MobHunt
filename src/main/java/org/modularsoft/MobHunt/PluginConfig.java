package org.modularsoft.MobHunt;

import org.modularsoft.MobHunt.helpers.CollectionMilestone;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PluginConfig {
    private final MobHuntMain plugin;
    private FileConfiguration config;

    @Getter private String databaseHost;
    @Getter private int databasePort;
    @Getter private String databaseName;
    @Getter private String databaseUsername;
    @Getter private String databasePassword;

    @Getter private String adminRole;

    @Getter private boolean featureOnEnableConsoleMessageEnabled;
    @Getter private boolean featureOnDisableConsoleMessageEnabled;
    @Getter private boolean featureOnNewHunterConsoleMessageEnabled;
    @Getter private boolean featureMilestoneMessageEnabled;

    @Getter private int killCap;

    @Getter private Sound soundMinorCollectionMilestone;
    @Getter private Sound soundMajorCollectionMilestone;

    @Getter private List<CollectionMilestone> collectionMilestones;
    @Getter private int leaderboardShowPlayers;

    @Getter private String langDatabaseConnectionError;
    @Getter private String langOnMobClear;
    @Getter private String langNotAPlayer;
    @Getter private String langInsufficientPermissions;
    @Getter private String langStringIsNotAValidPlayer;
    @Getter private String langStatsTitle;
    @Getter private String langStatsFormat;
    @Getter private List<String> langMobHelp;
    @Getter private List<String> langNewHunter;
    @Getter private String langMobKilled;
    @Getter private String langMobKilledCapReached;

    @Getter private String langCollectionMilestoneReached;

    @Getter private String langLeaderboardStringNotAMob;
    @Getter private String langLeaderboardHeader;
    @Getter private String langLeaderboardMobTitleFormat;
    @Getter private String langLeaderboardNoMobsKilled;
    @Getter private String langLeaderboardFirstColor;
    @Getter private String langLeaderboardSecondColor;
    @Getter private String langLeaderboardThirdColor;
    @Getter private String langLeaderboardOtherColor;
    @Getter private String langLeaderboardFormat;
    @Getter private String langScoreboardTitle;
    @Getter private List<String> langScoreboardContent;

    public PluginConfig(MobHuntMain plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        databaseHost = config.getString("Database.Host");
        databasePort = config.getInt("Database.Port");
        databaseName = config.getString("Database.Name");
        databaseUsername = config.getString("Database.Username");
        databasePassword = config.getString("Database.Password");

        adminRole = config.getString("AdminRole");

        featureOnEnableConsoleMessageEnabled = config.getBoolean("Features.OnEnabledConsoleMessage");
        featureOnDisableConsoleMessageEnabled = config.getBoolean("Features.OnDisabledConsoleMessage");
        featureOnNewHunterConsoleMessageEnabled = config.getBoolean("Features.OnNewHunterConsoleMessage");
        featureMilestoneMessageEnabled = config.getBoolean("Features.MilestoneMessages");

        killCap = config.getInt("MobHunt.KillCap");

        soundMinorCollectionMilestone = Sound.valueOf(config.getString("Sounds.MinorCollectionMilestone"));
        soundMajorCollectionMilestone = Sound.valueOf(config.getString("Sounds.MajorCollectionMilestone"));

        collectionMilestones = new ArrayList<>();
        for (Integer minor : config.getIntegerList("Milestones.Messages.Minor"))
            collectionMilestones.add(new CollectionMilestone(minor, false));
        for (Integer major : config.getIntegerList("Milestones.Messages.Major"))
            collectionMilestones.add(new CollectionMilestone(major, true));
        collectionMilestones.sort(Comparator.comparingInt(CollectionMilestone::getPoints));

        leaderboardShowPlayers = config.getInt("Leaderboard.ShowPlayers");

        langDatabaseConnectionError =    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Database.ConnectionError")));
        langOnMobClear =                 ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Command.OnMobClear")));
        langNotAPlayer =                 ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Command.NotAPlayer")));
        langInsufficientPermissions =    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Command.InsufficientPermissions")));
        langStringIsNotAValidPlayer =    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Command.StringIsNotAValidPlayer")));
        langStatsTitle =                 ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Command.Stats.Title")));
        langStatsFormat =                ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Command.Stats.Format")));
        langMobHelp = new ArrayList<>();
        for (String s : config.getStringList("Lang.Command.Help"))
            langMobHelp.add(ChatColor.translateAlternateColorCodes('&', s));
        langNewHunter = new ArrayList<>();
        for (String s : config.getStringList("Lang.MobHunt.NewHunter"))
            langNewHunter.add(ChatColor.translateAlternateColorCodes('&', s));
        langMobKilled =                  ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.MobHunt.Kill")));
        langMobKilledCapReached =        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.MobHunt.CapReached")));
        langCollectionMilestoneReached = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.MobHunt.CollectionMilestoneReached")));
        langLeaderboardStringNotAMob =   ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.StringNotAMob")));
        langLeaderboardHeader =          ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.Header")));
        langLeaderboardMobTitleFormat =  ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.MobTitleFormat")));
        langLeaderboardNoMobsKilled =    ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.NoMobsKilled")));
        langLeaderboardFirstColor =      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.FirstColor")));
        langLeaderboardSecondColor =     ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.SecondColor")));
        langLeaderboardThirdColor =      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.ThirdColor")));
        langLeaderboardOtherColor =      ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.OtherColor")));
        langLeaderboardFormat =          ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Leaderboard.Format")));
        langScoreboardTitle =            ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("Lang.Scoreboard.Title")));
        langScoreboardContent = new ArrayList<>();
        for (String s : config.getStringList("Lang.Scoreboard.Content"))
            langScoreboardContent.add(ChatColor.translateAlternateColorCodes('&', s));
    }

    public Integer getMobPoints(String mobType) {
        if (config.isInt("MobHunt.Points." + mobType))
            return config.getInt("MobHunt.Points." + mobType);
        return null;
    }
}
