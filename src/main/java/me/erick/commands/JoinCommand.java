package me.erick.commands;

import me.erick.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor {
    private final GameManager gameManager;

    public JoinCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Uso: /join <hider|seeker>");
            return true;
        }

        String team = args[0].toLowerCase();
        if (team.equals("hider")) {
            gameManager.addHider(player);
        } else if (team.equals("seeker")) {
            gameManager.addSeeker(player);
        } else {
            player.sendMessage("Time inválido. Use 'hider' ou 'seeker'");
        }

        return true;
    }
}