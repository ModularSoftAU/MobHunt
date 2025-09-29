package org.modularsoft.MobHunt.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.modularsoft.MobHunt.MobHuntMain;
import org.modularsoft.MobHunt.MobHuntQuery;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class PlayerDataStorage {
    private static final String PLAYERS_KEY = "players";

    private final MobHuntMain plugin;
    private final File dataFile;
    private YamlConfiguration data;

    public PlayerDataStorage(MobHuntMain plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        reload();
    }

    public void reload() {
        if (!dataFile.getParentFile().exists() && !dataFile.getParentFile().mkdirs()) {
            plugin.getLogger().log(Level.SEVERE, "Unable to create MobHunt data directory.");
        }

        if (!dataFile.exists()) {
            try {
                if (!dataFile.createNewFile()) {
                    plugin.getLogger().log(Level.SEVERE, "Unable to create MobHunt player data file.");
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create MobHunt player data file.", e);
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void save() {
        if (data == null)
            return;

        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save MobHunt player data.", e);
        }
    }

    private ConfigurationSection getPlayersSection(boolean create) {
        ConfigurationSection section = data.getConfigurationSection(PLAYERS_KEY);
        if (section == null && create) {
            section = data.createSection(PLAYERS_KEY);
        }
        return section;
    }

    private ConfigurationSection getPlayerSection(UUID uuid, boolean create) {
        ConfigurationSection playersSection = getPlayersSection(create);
        if (playersSection == null)
            return null;

        ConfigurationSection section = playersSection.getConfigurationSection(uuid.toString());
        if (section == null && create) {
            section = playersSection.createSection(uuid.toString());
        }
        return section;
    }

    public boolean ensurePlayerProfile(UUID uuid, String username) {
        ConfigurationSection section = getPlayerSection(uuid, true);
        if (section == null)
            return false;

        boolean created = false;
        boolean dirty = false;
        if (!section.contains("points")) {
            section.set("points", 0);
            created = true;
            dirty = true;
        }

        if (!section.isConfigurationSection("mobs")) {
            section.createSection("mobs");
            created = true;
            dirty = true;
        }

        if (!Objects.equals(section.getString("username"), username)) {
            section.set("username", username);
            dirty = true;
        }

        if (dirty)
            save();

        return created;
    }

    public int getPoints(UUID uuid) {
        ConfigurationSection section = getPlayerSection(uuid, false);
        if (section == null)
            return 0;
        return section.getInt("points", 0);
    }

    public void setPoints(UUID uuid, int points) {
        ConfigurationSection section = getPlayerSection(uuid, true);
        if (section == null)
            return;
        section.set("points", Math.max(points, 0));
        save();
    }

    public void addPoints(UUID uuid, int delta) {
        int current = getPoints(uuid);
        setPoints(uuid, current + delta);
    }

    public int getMobKillCount(UUID uuid, String mobType) {
        ConfigurationSection section = getPlayerSection(uuid, false);
        if (section == null)
            return 0;
        ConfigurationSection mobs = section.getConfigurationSection("mobs");
        if (mobs == null)
            return 0;
        return mobs.getInt(mobType, 0);
    }

    public void incrementMobKillCount(UUID uuid, String mobType) {
        ConfigurationSection section = getPlayerSection(uuid, true);
        if (section == null)
            return;

        ConfigurationSection mobs = section.getConfigurationSection("mobs");
        if (mobs == null)
            mobs = section.createSection("mobs");

        mobs.set(mobType, mobs.getInt(mobType, 0) + 1);
        save();
    }

    public boolean clearPlayerData(UUID uuid) {
        ConfigurationSection section = getPlayerSection(uuid, true);
        if (section == null)
            return false;

        section.set("points", 0);
        section.set("mobs", null);
        section.createSection("mobs");
        save();
        return true;
    }

    public List<MobHuntQuery.MobStat> getMobStats(UUID uuid) {
        ConfigurationSection section = getPlayerSection(uuid, false);
        if (section == null)
            return Collections.emptyList();

        ConfigurationSection mobs = section.getConfigurationSection("mobs");
        if (mobs == null)
            return Collections.emptyList();

        List<MobHuntQuery.MobStat> stats = new ArrayList<>();
        for (String mob : mobs.getKeys(false)) {
            stats.add(new MobHuntQuery.MobStat(mob, mobs.getInt(mob)));
        }

        stats.sort(Comparator.comparingInt(MobHuntQuery.MobStat::mobsKilled).reversed());
        return stats;
    }

    public List<MobHuntQuery.MobHunter> getTopHunters(int limit) {
        ConfigurationSection playersSection = getPlayersSection(false);
        if (playersSection == null)
            return Collections.emptyList();

        List<MobHuntQuery.MobHunter> hunters = new ArrayList<>();
        for (String key : playersSection.getKeys(false)) {
            ConfigurationSection playerSection = playersSection.getConfigurationSection(key);
            if (playerSection == null)
                continue;

            String username = playerSection.getString("username", key);
            int points = playerSection.getInt("points", 0);
            hunters.add(new MobHuntQuery.MobHunter(username, points));
        }

        hunters.sort(Comparator.comparingInt(MobHuntQuery.MobHunter::points).reversed());
        if (limit > 0 && hunters.size() > limit)
            return new ArrayList<>(hunters.subList(0, limit));

        return new ArrayList<>(hunters);
    }

    public List<MobHuntQuery.MobHunter> getTopHuntersForMob(String mobType, int limit) {
        ConfigurationSection playersSection = getPlayersSection(false);
        if (playersSection == null)
            return Collections.emptyList();

        List<MobHuntQuery.MobHunter> hunters = new ArrayList<>();
        for (String key : playersSection.getKeys(false)) {
            ConfigurationSection playerSection = playersSection.getConfigurationSection(key);
            if (playerSection == null)
                continue;

            ConfigurationSection mobs = playerSection.getConfigurationSection("mobs");
            if (mobs == null)
                continue;

            int kills = mobs.getInt(mobType, 0);
            if (kills <= 0)
                continue;

            String username = playerSection.getString("username", key);
            hunters.add(new MobHuntQuery.MobHunter(username, kills));
        }

        hunters.sort(Comparator.comparingInt(MobHuntQuery.MobHunter::points).reversed());
        if (limit > 0 && hunters.size() > limit)
            return new ArrayList<>(hunters.subList(0, limit));

        return new ArrayList<>(hunters);
    }
}
