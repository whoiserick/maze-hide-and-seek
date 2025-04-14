package me.erick.commands;

import me.erick.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {
    public CommandManager(JavaPlugin plugin, GameManager gameManager) {
        plugin.getCommand("join").setExecutor(new JoinCommand(gameManager));
        plugin.getCommand("leave").setExecutor(new LeaveCommand(gameManager));
        plugin.getCommand("start").setExecutor(new StartCommand(gameManager));
        plugin.getCommand("stop").setExecutor(new StopCommand(gameManager));
        plugin.getCommand("switch").setExecutor(new SwitchCommand(gameManager, gameManager.getSkinManager()));
        plugin.getCommand("forcestart").setExecutor(new ForceStartCommand(gameManager));
        plugin.getCommand("gamestate").setExecutor(new GameStateCommand(gameManager));
    }
}