package me.erick.events;

import me.erick.GameManager;
import me.erick.utils.ScoreboardManager;
import me.erick.utils.SkinManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class EventManager implements Listener {
    private final GameManager gameManager;
    private final SkinManager skinManager;

    public EventManager(JavaPlugin plugin, GameManager gameManager, SkinManager skinManager, ScoreboardManager scoreboardManager) {
        this.gameManager = gameManager;
        this.skinManager = skinManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Resetar skin ao entrar (caso tenha desconectado durante o jogo)
        if (gameManager.isPlaying(event.getPlayer())) {
            skinManager.resetSkin(event.getPlayer());
            gameManager.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remover jogador do jogo ao sair
        if (gameManager.isPlaying(event.getPlayer())) {
            skinManager.resetSkin(event.getPlayer());
            gameManager.removePlayer(event.getPlayer());

            // Verificar se o jogo acabou por falta de jogadores
            gameManager.checkGameEnd();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Lógica de morte no jogo
        if (gameManager.isPlaying(event.getEntity())) {
            event.setDeathMessage(null); // Remove mensagem padrão de morte

            if (gameManager.isHider(event.getEntity())) {
                gameManager.handleHiderDeath(event.getEntity());
            } else {
                gameManager.handleSeekerDeath(event.getEntity());
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Verificar se o jogador alcançou uma saída
        if (gameManager.isGameRunning() && gameManager.isHider(event.getPlayer())) {
            gameManager.checkExitLocation(event.getPlayer(), event.getTo());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Prevenir interações durante o jogo
        if (gameManager.isGameRunning() && gameManager.isPlaying(event.getPlayer())) {
            if (!gameManager.allowInteractions()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Prevenir quebra de blocos durante o jogo
        if (gameManager.isGameRunning() && gameManager.isPlaying(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Prevenir colocação de blocos durante o jogo
        if (gameManager.isGameRunning() && gameManager.isPlaying(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}