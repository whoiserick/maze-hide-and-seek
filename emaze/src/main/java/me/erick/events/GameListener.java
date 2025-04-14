package me.erick.events;

import me.erick.GameManager;
import me.erick.utils.ScoreboardManager;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameListener implements Listener {
    private final GameManager gameManager;
    private final ScoreboardManager scoreboardManager;
    private final Map<UUID, Long> attackCooldowns = new HashMap<>();
    private final int ATTACK_COOLDOWN = 2; // segundos

    public GameListener(GameManager gameManager, ScoreboardManager scoreboardManager) {
        this.gameManager = gameManager;
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        if (gameManager.isGameRunning()) {
            // Cooldown entre ataques
            if (isOnCooldown(attacker)) {
                event.setCancelled(true);
                attacker.sendMessage(String.format("§eAguarde §c%d§e segundos para atacar novamente",
                        getRemainingCooldown(attacker)));
                return;
            }

            // Seekers não podem se atacar
            if (gameManager.isSeeker(victim) && gameManager.isSeeker(attacker)) {
                event.setCancelled(true);
                attacker.sendMessage("§cVocê não pode atacar outros Seekers!");
                return;
            }

            // Hiders têm vida reduzida
            if (gameManager.isHider(victim)) {
                event.setDamage(20); // Mata com um hit
                victim.getWorld().playEffect(victim.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_BREAK, 1f, 0.5f);
            }

            // Ativar cooldown
            setCooldown(attacker);
            scoreboardManager.updateGameScoreboard();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (gameManager.isGameRunning() && gameManager.isPlaying(event.getPlayer())) {
            if (gameManager.isSeeker(event.getPlayer())) {
                event.setRespawnLocation(gameManager.getSeekerSpawn());
                event.getPlayer().addPotionEffect(new PotionEffect(
                        PotionEffectType.SPEED,
                        20 * 30, // 30 segundos
                        1 // Nível II
                ));
            } else {
                event.setRespawnLocation(event.getPlayer().getLocation());
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                event.getPlayer().sendMessage("§eVocê foi eliminado! Aguarde o fim do jogo.");
            }
            scoreboardManager.updateGameScoreboard();
        }
    }

    // Sistema de Cooldown
    private boolean isOnCooldown(Player player) {
        return attackCooldowns.containsKey(player.getUniqueId()) &&
                attackCooldowns.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    private int getRemainingCooldown(Player player) {
        return (int) ((attackCooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000) + 1;
    }

    private void setCooldown(Player player) {
        attackCooldowns.put(
                player.getUniqueId(),
                System.currentTimeMillis() + (ATTACK_COOLDOWN * 1000)
        );
    }
}