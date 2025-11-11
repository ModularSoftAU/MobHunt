package org.modularsoft.MobHunt;

import org.bukkit.scheduler.BukkitScheduler;
import org.modularsoft.MobHunt.commands.mh;
import org.modularsoft.MobHunt.commands.mobclear;
import org.modularsoft.MobHunt.commands.mobhelp;
import org.modularsoft.MobHunt.commands.mobstats;
import org.modularsoft.MobHunt.commands.mobleaderboard;
import org.modularsoft.MobHunt.commands.mobscoreboard;
import org.modularsoft.MobHunt.events.OnHunterJoin;
import org.modularsoft.MobHunt.events.OnMobKill;
import org.modularsoft.MobHunt.storage.PlayerDataStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MobHuntMain extends JavaPlugin {
    private PluginConfig config;
    private PlayerDataStorage playerDataStorage;
    private ConsoleCommandSender console;
    private HologramController hologramController;
    private ScoreboardController scoreboardController;

    public PluginConfig config() {
        return config;
    }

    @Override
    public void onEnable() {
        // Generate configuration file
        saveDefaultConfig();
        config = new PluginConfig(this);
        console = getServer().getConsoleSender();

        playerDataStorage = new PlayerDataStorage(this);

        HunterController hunterController = new HunterController(this);
        scoreboardController = new ScoreboardController(this);
        boolean hologramsAvailable = getServer().getPluginManager().isPluginEnabled("DecentHolograms");
        if (!hologramsAvailable) {
            getLogger().warning("DecentHolograms plugin is not enabled, hologram features will be skipped.");
        } else {
            hologramController = new HologramController(this, hunterController);
        }

        // Plugin Event Register
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new OnHunterJoin(this, hunterController, scoreboardController), this);
        pluginManager.registerEvents(new OnMobKill(this, hunterController, scoreboardController), this);

        // Command Registry
        Objects.requireNonNull(getCommand("mobstats")).setExecutor(new mobstats(this, hunterController));
        Objects.requireNonNull(getCommand("mobclear")).setExecutor(new mobclear(this, hunterController, scoreboardController));
        Objects.requireNonNull(getCommand("mobleaderboard")).setExecutor(new mobleaderboard(this, hunterController));
        Objects.requireNonNull(getCommand("mobhelp")).setExecutor(new mobhelp(hunterController));
        Objects.requireNonNull(getCommand("mobscoreboard")).setExecutor(new mobscoreboard(this, scoreboardController));
        Objects.requireNonNull(getCommand("mh")).setExecutor(new mh(this, scoreboardController));

        if (config.isFeatureOnEnableConsoleMessageEnabled()) {
            console.sendMessage(ChatColor.GREEN + getDescription().getName() + " is now enabled.");
            console.sendMessage(ChatColor.GREEN + "Running Version: " + getDescription().getVersion());
            console.sendMessage(ChatColor.GREEN + "GitHub Repository: https://github.com/ModularSoftAU/MobHunt");
            console.sendMessage(ChatColor.GREEN + "Created By: " + getDescription().getAuthors());
        }

        if (hologramController != null) {
            // Create hologram if it doesn't exist.
            hologramController.reloadHunterLeaderboard();

            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(this, hologramController::reloadHunterLeaderboard, 0L, 20L * 10);
        }
    }

    @Override
    public void onDisable() {
        if (config.isFeatureOnDisableConsoleMessageEnabled())
            console.sendMessage(ChatColor.RED + getDescription().getName() + " is now disabled.");

        if (playerDataStorage != null)
            playerDataStorage.save();
    }

    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    /**
     * Used for checking if a sender has admin rights (mainly to call
     * restricted commands).
     * @param sender The sender to check.
     * @return True if the sender is an admin.
     */
    public boolean isSenderAdmin(CommandSender sender) {
        return sender.hasPermission(config().getAdminRole()) || sender.isOp();
    }
}
