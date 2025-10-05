# Mob Hunt

Mob Hunt is a Paper plugin that rewards players for hunting across the overworld and beyond. Track unique mob kills, earn points, celebrate milestones, and showcase the best hunters on holograms or scoreboards – now without the need for an external database.

## Table of Contents

1. [Key Features](#key-features)
2. [Requirements](#requirements)
3. [Installation](#installation)
4. [Player Data Storage](#player-data-storage)
5. [Getting Started In-Game](#getting-started-in-game)
6. [How the Scoring System Works](#how-the-scoring-system-works)
7. [Milestones, Rewards, and Announcements](#milestones-rewards-and-announcements)
8. [Leaderboards & Competitive Play](#leaderboards--competitive-play)
9. [Commands](#commands)
10. [Tips & Configuration Highlights](#tips--configuration-highlights)
11. [Migrating from Legacy MySQL Builds](#migrating-from-legacy-mysql-builds)
12. [Player Field Guide](#player-field-guide)
13. [Building From Source](#building-from-source)
14. [Contributing](#contributing)

## Key Features

* **YAML-backed player data** – Player statistics and points are saved inside `plugins/MobHunt/playerdata.yml`, making the plugin portable to any server without requiring MySQL.
* **Configurable scoring** – Assign custom point values for each mob type and define the kill cap that controls diminishing returns.
* **Milestones and announcements** – Reward hunters with customizable milestone notifications and sounds.
* **Leaderboards everywhere** – Display top hunters through chat commands, scoreboards, or optional DecentHolograms integration.
* **Player-friendly tutorials** – `/mobhelp` and the new [Player Field Guide](#player-field-guide) walk newcomers through the rules and best practices.

## Requirements

* Paper/Paper forks 1.20.4+ (matching the API version declared in `pom.xml`).
* Java 17 or later.
* *(Optional)* [DecentHolograms](https://www.spigotmc.org/resources/decent-holograms-1-8-1-20-4.96927/) for holographic leaderboards.

## Installation

1. Download the latest Mob Hunt jar from the [releases page](https://github.com/ModularSoftAU/MobHunt/releases/).
2. Drop the jar into your server’s `plugins` folder.
3. Start the server once to generate the default configuration and player data file.
4. Configure `plugins/MobHunt/config.yml`:
   * Adjust `MobHunt.Points` for the mobs you care about.
   * Set `MobHunt.KillCap` to control when hunters stop earning points for repeated kills.
   * Tweak milestone thresholds, sounds, scoreboard lines, and hologram location if desired.
5. Reload/restart the server. Mob Hunt is ready to track your players.

## Player Data Storage

Mob Hunt now uses a portable YAML document located at `plugins/MobHunt/playerdata.yml` for all persistent player statistics. No database server is required.

### File Layout

```yaml
players:
  6aa4c0e5-6f4f-4c34-8c6d-1d2b7890b2e4:
    username: ExampleHunter
    points: 1275
    mobs:
      Zombie: 34
      Creeper: 18
      Ender Dragon: 1
```

* `players` – root map keyed by player UUID.
* `username` – last known username, updated whenever they join.
* `points` – total Mob Hunt score (integer).
* `mobs` – nested map of mob display names to kill counts used by scoreboards and leaderboards.

### Backup & Version Control

* Treat `playerdata.yml` like any other world save file: include it in regular server backups.
* The file is human-readable, so it can also be tracked with Git or other version control systems if you maintain a configuration repository.
* Because updates are synchronous, a server crash cannot leave a half-written entry, but it is still good practice to make off-site backups before large events or season resets.

### Editing Safely

* Always stop the server before manually editing `playerdata.yml` to avoid overwriting in-memory changes.
* Keep mob names consistent with those defined in `config.yml`; typos will appear as distinct categories on leaderboards.
* When reducing points or kills, double-check milestone thresholds—players rejoining after a manual rollback will retrigger milestone messages if they now fall below a threshold.

## Getting Started In-Game

1. **Join the hunt** – Use `/mobhelp` after you log in to read the tutorial text configured by your server admins.
2. **Check your stats** – Run `/mobstats` to see which mobs you have hunted and how many points you have.
3. **Pick your targets** – Focus on the mobs worth the most in your server’s `config.yml`, but be mindful of diminishing returns near the kill cap.
4. **Watch the scoreboard** – If enabled, the sidebar updates live as you earn points.
5. **Celebrate milestones** – Keep an ear out for sounds or broadcasts letting everyone know you reached a minor or major milestone.

## How the Scoring System Works

* **Per-mob points** – Every mob type has a configurable point value. Higher values encourage players to chase rarer mobs.
* **Kill caps** – After a configurable number of kills, the reward for that mob dwindles to prevent farming the same creature.
* **Example** – If `Zombie` is worth 5 points with a kill cap of 50, the first few zombies grant the full 5 points. As you approach 50 kills you receive fewer points, nudging you toward other mobs.
* **Persistent progress** – All points and kill totals are saved immediately to `playerdata.yml`, so relogs and restarts keep your stats intact.

## Milestones, Rewards, and Announcements

* **Minor vs major milestones** – Configure two tiers of celebratory messages. For example, a minor milestone at 1,000 points and a major one at 5,000.
* **Custom sounds and titles** – Use `Lang.Milestone` keys to tailor the broadcast, sound effect, and title/subtitle players see.
* **Server-wide recognition** – Major milestones can broadcast to all players, while minor milestones can be private—mix and match to create your preferred atmosphere.

## Leaderboards & Competitive Play

* **Chat leaderboards** – `/mobleaderboard` lists the top hunters overall. Add a mob name to focus on the best creeper hunter, blaze slayer, and more.
* **Scoreboards** – When enabled, the sidebar updates every refresh tick to highlight the leaders and your personal rank.
* **Holograms** – Pair Mob Hunt with [DecentHolograms](https://www.spigotmc.org/resources/decent-holograms-1-8-1-20-4.96927/) to drop a 3D leaderboard into your spawn area.
* **Season resets** – Admins can archive `playerdata.yml` at the end of a season and start fresh by deleting or renaming the file.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/mobstats [player]` | View your own mob kill breakdown or specify a player (admin only) to inspect their stats. | `mobhunt.admin` for viewing others |
| `/mobclear [player]` | Reset your own Mob Hunt progress, or specify another player to reset them (admin only). | `mobhunt.admin` |
| `/mobleaderboard [mob]` | Display the overall leaderboard or the top hunters for a specific mob type. | *None* |
| `/mobhelp` | Show the Mob Hunt tutorial text to explain the rules and scoring. | *None* |

## Tips & Configuration Highlights

* **Scoreboards** – Customize the sidebar in `Lang.Scoreboard` to match your server branding.
* **Holograms** – Configure `Hologram.LocationWorld/X/Y/Z` if you use DecentHolograms to show the live leaderboard. If the plugin is missing, Mob Hunt will continue running and simply skip hologram updates.
* **Storage error message** – `Lang.Storage.Error` is sent if the plugin can’t write to `playerdata.yml`; make sure the plugin folder is writable.
* **Onboarding tip** – Pre-fill `Lang.Help` with a quick explanation of the point values on your server so `/mobhelp` answers the most common questions.
* **Season archives** – Keep a copy of `playerdata.yml` whenever you want to preserve a season’s results before resetting the hunt.

## Migrating from Legacy MySQL Builds

Upgrading from an older Mob Hunt release that stored data in MySQL? Follow these steps to keep your players’ progress:

1. **Export the legacy data** – On your old server, run:

   ```sql
   SELECT uuid, username, points, mob, kills
   FROM mobhunt_player_stats
   ORDER BY uuid;
   ```

   Save the result as CSV. The exact table names may differ depending on your historical schema; adjust the query accordingly.
2. **Stop the new server** – Make sure the Mob Hunt plugin is not running so it will not overwrite manual edits to `playerdata.yml`.
3. **Open `playerdata.yml`** – For each exported row, create or locate a matching UUID entry and update the `username`, `points`, and `mobs` map.
4. **Validate YAML formatting** – Use an online YAML validator or `yamllint` to ensure indentation is correct. A single misplaced space can break the file.
5. **Restart the server** – Mob Hunt will load the migrated data automatically and continue writing to `playerdata.yml`.

Tip: if you have many rows, import the CSV into a spreadsheet, pivot on the mob column, and then copy/paste the aggregated values into YAML to reduce manual editing time.

Need a printable walkthrough? See the [Storage Migration Guide](docs/storage-migration.md).

## Player Field Guide

Looking for an extended walkthrough or something you can share with your community? Read the [Mob Hunt Player Field Guide](docs/player-field-guide.md) for examples, strategies, and printable quick tips.

## Building From Source

```bash
git clone https://github.com/ModularSoftAU/MobHunt.git
cd MobHunt
mvn package
```

The compiled jar will be located in `target/`.

## Contributing

Issues and pull requests are welcome! Whether you want to tweak balance, add new integrations, or improve translations, feel free to collaborate. Please include testing notes when submitting changes.