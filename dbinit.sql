DROP DATABASE IF EXISTS MobHunt;
CREATE DATABASE IF NOT EXISTS MobHunt;
USE MobHunt;

CREATE USER IF NOT EXISTS 'MobHunt'@'%' IDENTIFIED WITH mysql_native_password BY 'PasswordMobHunt321';
FLUSH PRIVILEGES;
GRANT SELECT ON MobHunt.* TO MobHunt@'%';
GRANT INSERT ON MobHunt.* TO MobHunt@'%';
GRANT UPDATE ON MobHunt.* TO MobHunt@'%';
GRANT DELETE ON MobHunt.* TO MobHunt@'%';

CREATE TABLE playerdata (
  id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
  uuid VARCHAR(36),
  username VARCHAR(16),
  mobsKilled int DEFAULT 0,
  killTimestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX players (uuid(8))
);
create index playerdata_username on playerdata (username);

CREATE TABLE mobs (
  id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
  playerid INT NOT NULL DEFAULT 0,
  mobType VARCHAR(100) NOT NULL,
  FOREIGN KEY (playerid) REFERENCES playerdata (id)
);