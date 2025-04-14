package me.erick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import me.erick.GameManager;

public class StopCommand implements CommandExecutor {
    private final GameManager gameManager;

    public StopCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificação de permissão
        if (!sender.hasPermission("mazehs.admin")) {
            sender.sendMessage("§cVocê não tem permissão para parar o jogo!");
            return true;
        }

        // Verificação de estado do jogo
        if (!gameManager.isGameRunning()) {
            sender.sendMessage("§6Não há nenhum jogo em andamento!");
            return true;
        }

        // Sistema de confirmação
        if (args.length == 0 || !args[0].equalsIgnoreCase("confirm")) {
            sender.sendMessage("§e╔════════════════════════════╗");
            sender.sendMessage("§e║ §c§lATENÇÃO: §eVocê está prestes a §cPARAR §eo jogo!");
            sender.sendMessage("§e║ §6Digite §f/stop confirm §6para confirmar");
            sender.sendMessage("§e╚════════════════════════════╝");

            // Adiciona um efeito sonoro opcional (se estiver disponível)
            if (sender instanceof org.bukkit.entity.Player) {
                org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
                p.playSound(p.getLocation(), org.bukkit.Sound.NOTE_PLING, 1f, 0.5f);
            }
            return true;
        }

        // Execução da parada do jogo
        try {
            gameManager.stopGame();
            sender.sendMessage("§a✔ Jogo interrompido com sucesso!");
            gameManager.broadcastMessage("\n§c§l» §4§lJOGO CANCELADO §c§l«");
            gameManager.broadcastMessage("§7O jogo foi interrompido por um administrador\n");

        } catch (Exception e) {
            sender.sendMessage("§c✖ Erro ao parar o jogo: " + e.getMessage());
            return false;
        }

        return true;
    }
}