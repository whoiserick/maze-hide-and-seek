package me.erick.commands;

import me.erick.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
    private final GameManager gameManager;

    public StartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mazehs.admin")) {
            player.sendMessage("§cVocê não tem permissão para isso!");
            return true;
        }

        if (gameManager.isGameRunning()) {
            player.sendMessage("§6O jogo já está em andamento!");
            return true;
        }

        if (gameManager.getHiders().isEmpty() || gameManager.getSeekers().isEmpty()) {
            player.sendMessage("§cPrecisa de pelo menos 1 hider e 1 seeker para começar!");
            return true;
        }

        gameManager.startGame();
        player.sendMessage("§aJogo iniciado com sucesso!");
        return true;
    }
}