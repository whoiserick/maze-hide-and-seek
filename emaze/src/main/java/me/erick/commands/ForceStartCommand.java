package me.erick.commands;

import me.erick.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceStartCommand implements CommandExecutor {
    private final GameManager gameManager;

    public ForceStartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mazehs.admin")) {
            sender.sendMessage("§cAcesso negado!");
            return true;
        }
        gameManager.forceStart();
        sender.sendMessage("§aJogo forçado para início (modo debug)");
        return true;
    }
}