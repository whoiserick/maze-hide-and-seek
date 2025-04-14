package me.erick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.erick.GameManager;

public class LeaveCommand implements CommandExecutor {
    private final GameManager gameManager;

    public LeaveCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;

        if (!gameManager.isPlaying(player)) {
            player.sendMessage("§cVocê não está em nenhum time!");
            return true;
        }

        gameManager.removePlayer(player);
        player.sendMessage("§aVocê saiu do jogo com sucesso!");

        return true;
    }
}