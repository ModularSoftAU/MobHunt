package com.modularenigma.MobHunt;

import com.modularenigma.MobHunt.commands.clearmobs;
import com.modularenigma.MobHunt.commands.mobs;
import com.modularenigma.MobHunt.commands.leaderboard;
import com.modularenigma.MobHunt.events.MobHunterOnJoin;
import com.modularenigma.MobHunt.events.MobKillEvent;
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

        MobHuntChatController eggChatController = new MobHuntChatController(this);
        MobHuntScoreboardController eggScoreboardController = new MobHuntScoreboardController(this);

        // Connect to the database
        establishConnection();

        // Plugin Event Register
        PluginManager pluginmanager = getServer().getPluginManager();
        pluginmanager.registerEvents(new MobHunterOnJoin(this, eggChatController, eggScoreboardController), this);
        pluginmanager.registerEvents(new MobKillEvent(this, eggChatController, eggScoreboardController), this);

        // Command Registry
        Objects.requireNonNull(getCommand("mobs")).setExecutor(new mobs(this, eggChatController));
        Objects.requireNonNull(getCommand("clearmobs")).setExecutor(new clearmobs(this, eggChatController, eggScoreboardController));
        Objects.requireNonNull(getCommand("leaderboard")).setExecutor(new leaderboard(this, eggChatController));

        // Plugin Load Message
        console.sendMessage(ChatColor.GREEN + getDescription().getName() + " is now enabled.");
        console.sendMessage(ChatColor.GREEN + "Running Version: " + getDescription().getVersion());
        console.sendMessage(ChatColor.GREEN + "GitHub Repository: https://github.com/ModularEnigma/MobHunt");
        console.sendMessage(ChatColor.GREEN + "Created By: " + getDescription().getAuthors());
    }

    @Override
    public void onDisable() {
        // Plugin Shutdown Message
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
