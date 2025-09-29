# Mob Hunt

Mob Hunt is a Paper plugin that rewards players for hunting across the overworld and beyond. Track unique mob kills, earn points, celebrate milestones, and showcase the best hunters on holograms or scoreboards – now without the need for an external database.

## Key Features

* **YAML-backed player data** – Player statistics and points are saved inside `plugins/MobHunt/playerdata.yml`, making the plugin portable to any server without requiring MySQL.
* **Configurable scoring** – Assign custom point values for each mob type and define the kill cap that controls diminishing returns.
* **Milestones and announcements** – Reward hunters with customizable milestone notifications and sounds.
* **Leaderboards everywhere** – Display top hunters through chat commands, scoreboards, or optional DecentHolograms integration.

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

* Statistics are saved in `plugins/MobHunt/playerdata.yml`.
* Each player entry contains their UUID, last known username, total points, and per-mob kill counts.
* Data is written synchronously after each change to keep the file consistent. Regular server backups will include Mob Hunt progress automatically.

## Gameplay Overview

* Every mob kill grants points based on `MobHunt.Points`. The reward decreases as players approach the configured kill cap for a mob, encouraging variety.
* Reaching milestone thresholds (minor or major) triggers configurable sounds and broadcasts.
* `/mobleaderboard` and optional holograms showcase the top hunters so competition stays fierce.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/mobstats [player]` | View your own mob kill breakdown or specify a player (admin only) to inspect their stats. | `mobhunt.admin` for viewing others |
| `/mobclear [player]` | Reset your own Mob Hunt progress, or specify another player to reset them (admin only). | `mobhunt.admin` |
| `/mobleaderboard [mob]` | Display the overall leaderboard or the top hunters for a specific mob type. | *None* |
| `/mobhelp` | Show the Mob Hunt tutorial text to explain the rules. | *None* |

## Tips & Configuration Highlights

* **Scoreboards** – Customize the sidebar in `Lang.Scoreboard` to match your server branding.
* **Holograms** – Configure `Hologram.LocationWorld/X/Y/Z` if you use DecentHolograms to show the live leaderboard.
* **Storage error message** – `Lang.Storage.Error` is sent if the plugin can’t write to `playerdata.yml`; make sure the plugin folder is writable.

## Building From Source

```bash
git clone https://github.com/ModularSoftAU/MobHunt.git
cd MobHunt
mvn package
```

The compiled jar will be located in `target/`.

## Contributing

Issues and pull requests are welcome! Whether you want to tweak balance, add new integrations, or improve translations, feel free to collaborate. Please include testing notes when submitting changes.