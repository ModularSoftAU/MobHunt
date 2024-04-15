# Mob Hunt

## Installation
* Clone this repo.
* Configure your config.
* Run the dbinit.
* Download the latest version of Mob Hunt from our [releases page](https://github.com/ModularSoftAU/MobHunt/releases/)
* Drop the Mob Hunt jar file into your plugins' folder.
* Set all of your point allocations per mob in the `config.yml` under `MobHunt.Points` and define your kill cap under `MobHunt.KillCap`

## Requirements
* MySQL Database.

## Soft Dependencies
- [Optional] `DecentHolograms` if you would like to have a holographic live update leaderboard.

## Gameplay
When a player kills a mob it is logged into a database and a number incremented in their name.
Most amount and unique amount of mobs that a player kills, wins.

#### Milestones
When a player kills over an X amount of mobs, a message will broadcast to all online players that they have reached a milestone.
The milestones can be changed and modified in the `config.yml` under `Milestones.Messages` and you can change them based on Major and Minor.

### Holographic Leaderboard


## Commands
| Command               | Description                                              | Permission      |
|-----------------------|----------------------------------------------------------|-----------------|
| /mobstats             | Shows a breakdown of the mobs you killed                 |                 |
| /mobclear             | Clear all mobs from yourself or another player.          | `mobhunt.admin` |
| /mobleaderboard [mob] | Show the Top 5 Mob Hunters overall or of a specific mob. |                 |
| /mobhelp              | Helps the player know how to play Mob Hunt.              |                 |