# Migrating Mob Hunt Data from MySQL to YAML

Mob Hunt 1.0 and earlier stored player progress inside a MySQL database. The plugin now writes to `playerdata.yml` inside the plugin folder instead. Use this guide when you want to bring existing statistics forward to the YAML-based release.

## 1. Export the Old Data

1. Connect to your MySQL server using your preferred client (MySQL Shell, phpMyAdmin, HeidiSQL, etc.).
2. Run a query that exports every row from your Mob Hunt tables. A common schema looked like this:

   ```sql
   SELECT uuid,
          username,
          points,
          mob,
          kills
   FROM mobhunt_player_stats
   ORDER BY uuid, mob;
   ```

3. Save the results as CSV. Make sure the encoding is UTF-8 so usernames with special characters survive the export.

> **Tip:** If your historical schema used multiple tables (for example, a `players` table and a `kills` table), join them during export so each row already includes the username and mob name you expect to see in YAML.

## 2. Aggregate the Mob Columns

Each YAML player entry expects a single map of `mob -> killCount`. If your export produces one row per mob, aggregate before copying:

1. Import the CSV into a spreadsheet application.
2. Create a pivot table with `uuid`+`username` as the row key and `mob` as the column header.
3. Sum the `kills` column. The pivot output mirrors the YAML structure and can be copied directly.

Alternatively, use a short script to group the data. Example in Python:

```python
import csv
import json
from collections import defaultdict

players = {}
with open("mobhunt_export.csv", newline="", encoding="utf-8") as handle:
    reader = csv.DictReader(handle)
    for row in reader:
        mobs = players.setdefault(row["uuid"], {
            "username": row["username"],
            "points": int(row["points"]),
            "mobs": defaultdict(int),
        })
        mobs["mobs"][row["mob"]] += int(row["kills"])

print(json.dumps(players, indent=2))
```

The printed JSON acts as a checklist when transcribing to YAML.

## 3. Stop the New Server

Shut down the Paper server running the YAML version of Mob Hunt. Editing the file while the plugin is active will be overwritten immediately.

## 4. Update `playerdata.yml`

1. Open `plugins/MobHunt/playerdata.yml`.
2. For each UUID from your export, either update the existing block or create a new one using the aggregated values.
3. Keep indentation to two spaces per level—tabs will invalidate the file.

Example entry:

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

## 5. Validate and Restart

* Use `yamllint playerdata.yml` or an online YAML validator to ensure the syntax is correct.
* Start the server. Mob Hunt will read the migrated file and continue tracking progress without further setup.

## 6. Keep a Safety Backup

Archive both the exported CSV and the updated `playerdata.yml` somewhere safe. If you ever reset the hunt or spot an incorrect value, you can quickly roll back without repeating the migration.

Happy hunting! Your players retain their hard-earned milestones while enjoying the simplified setup of the YAML edition.
