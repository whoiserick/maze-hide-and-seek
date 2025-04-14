package me.erick.events;

import me.erick.GameManager;
import me.erick.utils.SkinManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {
    private final GameManager gameManager;
    private final SkinManager skinManager;

    public PlayerListener(GameManager gameManager, SkinManager skinManager) {
        this.gameManager = gameManager;
        this.skinManager = skinManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("§7[§a+§7] §f" + player.getName() + " §eentrou");

        if (gameManager.isPlaying(player)) {
            gameManager.removePlayer(player);
            skinManager.resetSkin(player);
        }

        if (gameManager.isGameRunning()) {
            player.sendMessage("§eUm jogo de Maze Hide and Seek está em andamento!");
            if (gameManager.canJoinMidGame()) {
                player.sendMessage("§aUse §f/join <hider|seeker> §apara participar");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (gameManager.isPlaying(player)) {
            gameManager.removePlayer(player);
            skinManager.resetSkin(player);
            event.setQuitMessage("§7[§c-§7] §f" + player.getName() + " §esaiu do jogo");
        } else {
            event.setQuitMessage("§7[§c-§7] §f" + player.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!gameManager.isGameRunning() || !gameManager.isPlaying(event.getPlayer())) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String blockType = event.getClickedBlock().getType().toString();
            if (!blockType.contains("DOOR") && !blockType.contains("LEVER") &&
                    !blockType.contains("BUTTON") && !blockType.contains("TRAPDOOR")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (gameManager.isGameRunning() && gameManager.isPlaying(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não pode dropar itens durante o jogo!");
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (gameManager.isGameRunning() && !gameManager.isSeeker(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (gameManager.isGameRunning() && gameManager.isPlaying(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (gameManager.isPlaying(event.getPlayer()) &&
                event.getNewGameMode() != GameMode.SPECTATOR) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não pode mudar de gamemode durante o jogo!");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (gameManager.isGameRunning() && gameManager.isPlaying(event.getPlayer())) {
            String cmd = event.getMessage().split(" ")[0].toLowerCase();
            if (!cmd.equals("/leave") && !cmd.equals("/mazehs") && !cmd.equals("/switch") && !cmd.equals("/vote")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cVocê não pode usar comandos durante o jogo!");
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (gameManager.isGameRunning() &&
                gameManager.isPlaying(event.getPlayer()) &&
                event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (gameManager.isGameRunning() && gameManager.isPlaying(player)) {
                event.setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (gameManager.isGameRunning() &&
                    gameManager.isPlaying(player) &&
                    event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (gameManager.isGameRunning()) {
            if (gameManager.isHider(event.getPlayer())) {
                event.setFormat("§2[Hider] §f%s §7» §f%s");
            } else if (gameManager.isSeeker(event.getPlayer())) {
                event.setFormat("§c[Seeker] §f%s §7» §f%s");
            }
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if (gameManager.isGameRunning() && event.getItem().getType() == Material.GOLDEN_APPLE) {
            event.getPlayer().addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    20*15, 0, false));
            event.getPlayer().sendMessage("§aVocê ficou invisível por 15 segundos!");
        }
    }

    @EventHandler
    public void onPlayerVoteCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/vote") && gameManager.isGameRunning()) {
            event.setCancelled(true);
            if (gameManager.addVote(event.getPlayer())) {
                event.getPlayer().sendMessage(String.format(
                        "§aVoto registrado! §7(%d/%d)",
                        gameManager.getVotes(),
                        gameManager.getNeededVotes()));

                if (gameManager.getVotes() >= gameManager.getNeededVotes()) {
                    Bukkit.broadcastMessage("§6Votação concluída! O jogo será interrompido.");
                    gameManager.stopGame();
                }
            } else {
                event.getPlayer().sendMessage("§cVocê já votou!");
            }
        }
    }
}