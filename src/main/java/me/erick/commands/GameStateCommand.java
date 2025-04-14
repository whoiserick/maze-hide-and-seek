package me.erick.commands;

import me.erick.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameStateCommand implements CommandExecutor {
    private final GameManager gameManager;

    public GameStateCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§eEstado: " + (gameManager.isGameRunning() ? "§aRodando" : "§cParado"));
        sender.sendMessage("§aHiders: " + gameManager.getHiders().size());
        sender.sendMessage("§bSeekers: " + gameManager.getSeekers().size());
        return true;
    }
}