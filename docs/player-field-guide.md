# Mob Hunt Player Field Guide

The Mob Hunt plugin turns mob-slaying into an ongoing competition. This guide helps new hunters understand the rules, scoring, and strategies so they can climb the leaderboard.

## Quick Start Checklist

1. **Read the tutorial** – Type `/mobhelp` in-game to see the overview written by your server admins.
2. **Check your stats** – Use `/mobstats` to view your total points, kill counts per mob, and your personal rank.
3. **Learn the point values** – Open `plugins/MobHunt/config.yml` or ask staff which mobs are worth the most points.
4. **Grab supplies** – Stock up on weapons, armor, and food before heading into the world.
5. **Head to a hunting ground** – Explore different biomes and dimensions to find high-value mobs.

## Understanding Points & Kill Caps

Mob Hunt awards points for every mob kill. Each mob has two key settings:

* **Point value** – Higher values mean the mob is more rewarding to defeat.
* **Kill cap** – After a certain number of kills, you earn reduced points for that mob to prevent farming.

### Example Scenario

| Mob | Base Points | Kill Cap | Notes |
|-----|-------------|----------|-------|
| Zombie | 5 | 50 | Common mobs—great early-game income but drops off quickly. |
| Blaze | 12 | 30 | More dangerous; hunting in the Nether is worth it. |
| Enderman | 15 | 25 | High value, but the cap means you’ll want to mix in other mobs. |

If you have already slain 45 zombies, they will award very few points. Switch to skeletons, spiders, or rare mobs to keep your score climbing.

## Milestones and Rewards

Milestones celebrate your progress:

* **Minor milestones** – Smaller point thresholds that can show a chat or title message. Perfect for personal achievements.
* **Major milestones** – Larger thresholds that may broadcast globally so everyone knows you hit a new record.
* **Custom rewards** – Server owners can pair milestones with items, economy payouts, or permissions using other plugins.

Whenever you hit a milestone, the plugin plays optional sounds and titles to keep the adrenaline going.

## Competing on Leaderboards

You can review the top hunters anytime:

* `/mobleaderboard` – View the overall leaders.
* `/mobleaderboard <mob>` – See the best hunter for a specific mob.
* **Scoreboards** – Some servers enable a sidebar scoreboard so you can track your rank in real time.
* **Holograms** – If DecentHolograms is installed, a live hologram can display the top 10 hunters at spawn or in a hall of fame.

### Running Events

Some communities like to run recurring Mob Hunt events to keep the competition fresh. When your staff team launches a new event:

1. Decide whether to clear or archive `playerdata.yml`.
2. Announce the rules, prizes, and any special modifiers.
3. Encourage players to plan new strategies together.

## Hunting Strategies

* **Diversify targets** – Rotate between mob types to avoid hitting the kill caps too quickly.
* **Explore dimensions** – The Nether and The End often host higher-value mobs.
* **Team up** – Split mob roles with friends: one player tanks, another deals damage, a third collects loot.
* **Gear upgrades** – Enchant swords with Sharpness/Smite and bows with Power to maximize efficiency.
* **Time your hunts** – Nighttime on the surface spawns more hostile mobs; daytime is perfect for hunting passive mobs if they reward points.

## Preparing for Danger

Hunting high-value mobs can be risky:

* **Carry backups** – Extra armor and weapons prevent downtime after a death.
* **Bring potions** – Fire resistance and regeneration potions can keep you alive in the Nether.
* **Use shields** – Shields block many melee and projectile attacks, giving you more time to react.
* **Keep inventory tidy** – Empty inventory space ensures you collect drops and don’t lose track of valuables.

## Frequently Asked Questions

**Q: Do I lose points when I die?**  
A: No. Points only ever increase as you slay mobs.

**Q: Can I farm mobs in spawners?**  
A: It depends on your server’s rules. The kill cap prevents infinite farming, but always follow local guidelines.

**Q: How do I know the kill cap for a mob?**  
A: Ask staff or view the server’s `config.yml`. Some servers include the values in `/mobhelp`.

**Q: My stats seem stuck—what do I do?**  
A: Contact staff to make sure the plugin can write to `playerdata.yml`. Re-logging usually refreshes your scoreboard.

## Shareable Quick Tips

Print or copy these lines into your server’s announcement channels:

* “Use `/mobstats` after every hunt to see where you’re close to a new milestone.”
* “Rotate between overworld, Nether, and End mobs to maximize points.”
* “Hit `/mobleaderboard blaze` to see who rules the Nether this week.”
* “Event night is coming—archive your best hunts and get ready for a fresh climb!”

Happy hunting!
