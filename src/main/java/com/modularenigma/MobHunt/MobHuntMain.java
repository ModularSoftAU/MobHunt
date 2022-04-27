package com.modularenigma.MobHunt;

import com.modularenigma.MobHunt.commands.mobclear;
import com.modularenigma.MobHunt.commands.mobcount;
import com.modularenigma.MobHunt.commands.mobleaderboard;
import com.modularenigma.MobHunt.events.OnHunterJoin;
import com.modularenigma.MobHunt.events.OnMobKill;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.ChatColor;
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

        HunterController hunterController = new HunterController(this);
        ScoreboardController scoreboardController = new ScoreboardController(this);

        // Connect to the database
        establishConnection();

        // Plugin Event Register
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new OnHunterJoin(this, hunterController, scoreboardController), this);
        pluginManager.registerEvents(new OnMobKill(this, hunterController, scoreboardController), this);

        // Command Registry
        Objects.requireNonNull(getCommand("mobcount")).setExecutor(new mobcount(this, hunterController));
        Objects.requireNonNull(getCommand("mobclear")).setExecutor(new mobclear(this, hunterController, scoreboardController));
        Objects.requireNonNull(getCommand("mobleaderboard")).setExecutor(new mobleaderboard(this, hunterController));

        if (config.isFeatureOnEnableConsoleMessageEnabled()) {
            console.sendMessage(ChatColor.GREEN + getDescription().getName() + " is now enabled.");
            console.sendMessage(ChatColor.GREEN + "Running Version: " + getDescription().getVersion());
            console.sendMessage(ChatColor.GREEN + "GitHub Repository: https://github.com/ModularEnigma/MobHunt");
            console.sendMessage(ChatColor.GREEN + "Created By: " + getDescription().getAuthors());
        }
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
}
