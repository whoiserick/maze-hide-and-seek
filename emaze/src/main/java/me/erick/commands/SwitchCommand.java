package me.erick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.erick.GameManager;
import me.erick.utils.SkinManager;

public class SwitchCommand implements CommandExecutor {
    private final GameManager gameManager;
    private final SkinManager skinManager;

    public SwitchCommand(GameManager gameManager, SkinManager skinManager) {
        this.gameManager = gameManager;
        this.skinManager = skinManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar se é jogador
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem trocar de time!");
            return true;
        }

        Player player = (Player) sender;

        // Verificar estado do jogo
        if (gameManager.isGameRunning()) {
            player.sendMessage("§6Você só pode trocar de time antes do jogo começar!");
            return true;
        }

        // Verificar se está em algum time
        if (!gameManager.isPlaying(player)) {
            player.sendMessage("§cVocê não está em nenhum time! Use §f/join <time>§c primeiro.");
            return true;
        }

        // Confirmar troca se já estiver em time
        if (args.length == 0 || !args[0].equalsIgnoreCase("confirm")) {
            String currentTeam = gameManager.isHider(player) ? "§aHider" : "§bSeeker";
            player.sendMessage("§e╔══════════════════════════╗");
            player.sendMessage("§e║   §fTROCA DE TIME SOLICITADA   §e║");
            player.sendMessage("§e╠──────────────────────────╣");
            player.sendMessage("§e║ Time atual: " + currentTeam + " §e║");
            player.sendMessage("§e║ §fDigite §b/switch confirm §fpara §etrocar §e║");
            player.sendMessage("§e╚══════════════════════════╝");
            player.playSound(player.getLocation(), org.bukkit.Sound.CLICK, 1f, 1f);
            return true;
        }

        // Efetuar a troca
        try {
            if (gameManager.isHider(player)) {
                // Hider -> Seeker
                gameManager.removePlayer(player); // Em vez de removeHider/removeSeeker
                gameManager.addSeeker(player);
                skinManager.applySeekerSkin(player);
                player.sendMessage("§aVocê agora é um §bSeeker§a!");
            } else {
                // Seeker -> Hider
                gameManager.removePlayer(player); // Em vez de removeHider/removeSeeker
                gameManager.addHider(player);
                skinManager.applyHiderSkin(player);
                player.sendMessage("§aVocê agora é um §2Hider§a!");
            }

            player.playSound(player.getLocation(), org.bukkit.Sound.LEVEL_UP, 0.5f, 1f);

        } catch (Exception e) {
            player.sendMessage("§cErro ao trocar de time: " + e.getMessage());
            return false;
        }

        return true;
    }
}