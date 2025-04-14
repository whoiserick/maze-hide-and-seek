package me.erick.utils;

import me.erick.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final Main plugin;
    private FileConfiguration config;
    private final File configFile;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        loadConfig();
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Erro ao salvar config.yml: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    // Game Settings
    public int getGameDuration() {
        return config.getInt("game.duration", 300);
    }

    public int getCountdownTime() {
        return config.getInt("game.countdown", 30);
    }

    public int getSeekerDelay() {
        return config.getInt("game.seeker-delay", 10);
    }

    public boolean canJoinMidGame() {
        return config.getBoolean("game.mid-game-join", false);
    }

    public int getNeededVotes() {
        return config.getInt("game.needed-votes", 3);
    }

    // Webhook Settings
    public String getWebhookUrl() {
        return config.getString("webhook.url", "");
    }

    public boolean isWebhookEnabled() {
        return config.getBoolean("webhook.enabled", false);
    }

    // Skin Settings
    public String getHiderSkin() {
        return config.getString("skins.hider", "");
    }

    public String getSeekerSkin() {
        return config.getString("skins.seeker", "");
    }

    // Map Settings
    public String getMapName() {
        return config.getString("map.name", "Labirinto 1");
    }

    public World getWorld() {
        return Bukkit.getWorld(config.getString("map.world", "world"));
    }

    public Location getHidersSpawn() {
        ConfigurationSection spawn = config.getConfigurationSection("map.spawn.hiders");
        return new Location(
                getWorld(),
                spawn.getDouble("x"),
                spawn.getDouble("y"),
                spawn.getDouble("z"),
                (float) spawn.getDouble("yaw", 0),
                (float) spawn.getDouble("pitch", 0)
        );
    }

    public Location getSeekersSpawn() {
        ConfigurationSection spawn = config.getConfigurationSection("map.spawn.seekers");
        return new Location(
                getWorld(),
                spawn.getDouble("x"),
                spawn.getDouble("y"),
                spawn.getDouble("z"),
                (float) spawn.getDouble("yaw", 0),
                (float) spawn.getDouble("pitch", 0)
        );
    }

    public List<ExitArea> getExits() {
        List<ExitArea> exits = new ArrayList<>();
        ConfigurationSection exitsSection = config.getConfigurationSection("map.exits");
        if (exitsSection != null) {
            for (String key : exitsSection.getKeys(false)) {
                ConfigurationSection exit = exitsSection.getConfigurationSection(key);
                if (exit != null) {
                    Location loc = new Location(
                            getWorld(),
                            exit.getDouble("x"),
                            exit.getDouble("y"),
                            exit.getDouble("z")
                    );
                    exits.add(new ExitArea(loc, exit.getDouble("radius", 3.0)));
                }
            }
        }
        return exits;
    }

    // Item Settings
    public int getItemSpawnInterval() {
        return config.getInt("items.spawn-interval", 30);
    }

    public List<Location> getItemSpawnPoints() {
        List<Location> points = new ArrayList<>();
        ConfigurationSection pointsSection = config.getConfigurationSection("items.spawn-points");
        if (pointsSection != null) {
            for (String key : pointsSection.getKeys(false)) {
                ConfigurationSection point = pointsSection.getConfigurationSection(key);
                if (point != null) {
                    points.add(new Location(
                            getWorld(),
                            point.getDouble("x"),
                            point.getDouble("y"),
                            point.getDouble("z")
                    ));
                }
            }
        }
        return points;
    }

    public static class ExitArea {
        private final Location center;
        private final double radius;

        public ExitArea(Location center, double radius) {
            this.center = center;
            this.radius = radius;
        }

        public boolean isInArea(Location location) {
            if (!location.getWorld().equals(center.getWorld())) return false;
            return location.distanceSquared(center) <= (radius * radius);
        }
    }
}