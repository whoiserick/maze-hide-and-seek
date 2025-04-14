package me.erick;

import me.erick.commands.CommandManager;
import me.erick.events.EventManager;
import me.erick.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private GameManager gameManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.gameManager = new GameManager(this, configManager);

        new CommandManager(this, gameManager);
        new EventManager(this, gameManager, gameManager.getSkinManager(), gameManager.getScoreboardManager());

        getLogger().info("MazeHideAndSeek carregado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.cleanup();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}