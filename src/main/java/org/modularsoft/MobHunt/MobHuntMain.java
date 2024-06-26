package org.modularsoft.MobHunt;

import org.bukkit.scheduler.BukkitScheduler;
import org.modularsoft.MobHunt.commands.mobclear;
import org.modularsoft.MobHunt.commands.mobhelp;
import org.modularsoft.MobHunt.commands.mobstats;
import org.modularsoft.MobHunt.commands.mobleaderboard;
import org.modularsoft.MobHunt.events.OnHunterJoin;
import org.modularsoft.MobHunt.events.OnMobKill;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class MobHuntMain extends JavaPlugin {
    private PluginConfig config;
    private Connection connection;
    private ConsoleCommandSender console;

    public PluginConfig config() {
        return config;
    }

    @Override
    public void onEnable() {
        // Generate configuration file
        saveDefaultConfig();
        config = new PluginConfig(this);
        console = getServer().getConsoleSender();

        // Check if DecentHolograms enabled.
        if (!getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
            getLogger().severe("DecentHolograms plugin is not enabled, hologram features will not work.");
            return;
        }

        HunterController hunterController = new HunterController(this);
        ScoreboardController scoreboardController = new ScoreboardController(this);
        HologramController hologramController = new HologramController(this, hunterController);

        // Connect to the database
        establishConnection();

        // Plugin Event Register
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new OnHunterJoin(this, hunterController, scoreboardController), this);
        pluginManager.registerEvents(new OnMobKill(this, hunterController, scoreboardController), this);

        // Command Registry
        Objects.requireNonNull(getCommand("mobstats")).setExecutor(new mobstats(this, hunterController));
        Objects.requireNonNull(getCommand("mobclear")).setExecutor(new mobclear(this, hunterController, scoreboardController));
        Objects.requireNonNull(getCommand("mobleaderboard")).setExecutor(new mobleaderboard(this, hunterController));
        Objects.requireNonNull(getCommand("mobhelp")).setExecutor(new mobhelp(hunterController));

        if (config.isFeatureOnEnableConsoleMessageEnabled()) {
            console.sendMessage(ChatColor.GREEN + getDescription().getName() + " is now enabled.");
            console.sendMessage(ChatColor.GREEN + "Running Version: " + getDescription().getVersion());
            console.sendMessage(ChatColor.GREEN + "GitHub Repository: https://github.com/ModularSoftAU/MobHunt");
            console.sendMessage(ChatColor.GREEN + "Created By: " + getDescription().getAuthors());
        }

        // Create hologram if it doesn't exist.
        hologramController.reloadHunterLeaderboard();

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, hologramController::reloadHunterLeaderboard, 0L, 20L * 10);
    }

    @Override
    public void onDisable() {
        if (config.isFeatureOnDisableConsoleMessageEnabled())
            console.sendMessage(ChatColor.RED + getDescription().getName() + " is now disabled.");
    }

    public void establishConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(config.getDatabaseHost());
            dataSource.setPort(config.getDatabasePort());
            dataSource.setDatabaseName(config.getDatabaseName());
            dataSource.setUser(config.getDatabaseUsername());
            dataSource.setPassword(config.getDatabasePassword());
            connection = dataSource.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            getLogger().info(config.getLangDatabaseConnectionError());
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                getLogger().info(config.getLangDatabaseConnectionError());
                e.printStackTrace();
            }
        }
        establishConnection();
        return connection;
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
