package me.erick;

import me.erick.utils.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final SkinManager skinManager;
    private final ScoreboardManager scoreboardManager;
    private final ItemSpawner itemSpawner;
    private final WebhookLogger webhookLogger;

    private GameState gameState = GameState.WAITING;
    private final Set<UUID> hiders = new HashSet<>();
    private final Set<UUID> seekers = new HashSet<>();
    private final Set<UUID> votes = new HashSet<>();
    private int remainingTime;
    private BukkitRunnable gameTask;

    public GameManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.skinManager = new SkinManager((Main) plugin);
        this.scoreboardManager = new ScoreboardManager(this);
        this.itemSpawner = new ItemSpawner(this);
        this.webhookLogger = new WebhookLogger(configManager);
    }

    public enum GameState {
        WAITING, COUNTDOWN, RUNNING, FINISHED
    }

    // Métodos principais do jogo
    public void startGame() {
        if (gameState != GameState.WAITING) return;

        gameState = GameState.RUNNING;
        remainingTime = configManager.getGameDuration();

        preparePlayers();
        teleportPlayers();
        startGameTimer();
        itemSpawner.startSpawning();
        webhookLogger.logGameStart();

        broadcastMessage("§6§lO JOGO COMEÇOU!");
        broadcastMessage("§eHiders: §fEncontrem a saída do labirinto!");
        broadcastMessage("§eSeekers: §fVocês começam em " + configManager.getSeekerDelay() + " segundos...");
    }

    public void stopGame() {
        if (gameState != GameState.RUNNING) return;

        gameState = GameState.FINISHED;
        cleanup();
        webhookLogger.logGameEnd("ADMIN_CANCEL");

        broadcastMessage("§c§lO JOGO FOI CANCELADO!");
    }

    public void forceStart() {
        if (Bukkit.getOnlinePlayers().size() < 2) return;
        startGame();
    }

    // Gerenciamento de jogadores
    public void addHider(Player player) {
        hiders.add(player.getUniqueId());
        skinManager.applyHiderSkin(player);
    }

    public void addSeeker(Player player) {
        seekers.add(player.getUniqueId());
        skinManager.applySeekerSkin(player);
    }

    public void removePlayer(Player player) {
        hiders.remove(player.getUniqueId());
        seekers.remove(player.getUniqueId());
        skinManager.resetSkin(player);
        checkGameEnd();
    }

    public boolean isPlaying(Player player) {
        return hiders.contains(player.getUniqueId()) || seekers.contains(player.getUniqueId());
    }

    public boolean isHider(Player player) {
        return hiders.contains(player.getUniqueId());
    }

    public boolean isSeeker(Player player) {
        return seekers.contains(player.getUniqueId());
    }

    public boolean hasEnoughPlayers() {
        return !hiders.isEmpty() && !seekers.isEmpty();
    }

    public void handleHiderDeath(Player player) {
        removePlayer(player);
        broadcastMessage("§a" + player.getName() + " §efoi eliminado!");
    }

    public void handleSeekerDeath(Player player) {
        player.sendMessage("§eVocê morreu! Reaparecendo em 5 segundos...");
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(getSeekerSpawn());
            }
        }.runTaskLater(plugin, 20L * 5);
    }

    public boolean checkHiderEscape(Player player, Location location) {
        return configManager.getExits().stream()
                .anyMatch(exit -> exit.isInArea(location));
    }

    public void checkExitLocation(Player player, Location location) {
        if (checkHiderEscape(player, location)) {
            removePlayer(player);
            broadcastMessage("§a" + player.getName() + " §eescapou do labirinto!");
        }
    }

    public boolean allowInteractions() {
        return false;
    }

    // Métodos auxiliares
    private void preparePlayers() {
        getAllPlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);

            if (isHider(player)) {
                player.setHealth(4); // 2 corações
            }
        });
    }

    private void teleportPlayers() {
        getAllPlayers().forEach(player -> {
            if (isHider(player)) {
                player.teleport(configManager.getHidersSpawn());
            } else {
                player.teleport(configManager.getSeekersSpawn());
                player.sendTitle("§cESPERE...", "§fVocê começa em " + configManager.getSeekerDelay() + " segundos");
            }
        });
    }

    private void startGameTimer() {
        if (gameTask != null) gameTask.cancel();

        gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.RUNNING) {
                    this.cancel();
                    return;
                }

                remainingTime--;
                scoreboardManager.updateGameScoreboard();

                if (remainingTime <= 0) {
                    endGame("HIDERS");
                    return;
                }

                if (hiders.isEmpty()) {
                    endGame("SEEKERS");
                    return;
                }

                if (remainingTime % 30 == 0) {
                    playGlobalSound(Sound.NOTE_PLING, 1f);
                }
            }
        };
        gameTask.runTaskTimer(plugin, 0L, 20L);
    }

    private void endGame(String winningTeam) {
        gameState = GameState.FINISHED;
        cleanup();

        String winnerMessage = winningTeam.equals("HIDERS") ?
                "§a§lHIDERS VENCERAM!" : "§c§lSEEKERS VENCERAM!";

        broadcastMessage("\n" + winnerMessage);
        broadcastMessage("§7Tempo restante: §f" + formatTime(remainingTime) + "\n");

        webhookLogger.logGameEnd(winningTeam);
    }

    public void cleanup() {
        if (gameTask != null) gameTask.cancel();
        itemSpawner.stopSpawning();

        getAllPlayers().forEach(player -> {
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            skinManager.resetSkin(player);
        });

        hiders.clear();
        seekers.clear();
        votes.clear();
    }

    public void checkGameEnd() {
        if (gameState == GameState.RUNNING && hiders.isEmpty()) {
            endGame("SEEKERS");
        }
    }

    public boolean addVote(Player player) {
        if (votes.contains(player.getUniqueId())) return false;
        votes.add(player.getUniqueId());
        return true;
    }

    // Getters
    public boolean isGameRunning() {
        return gameState == GameState.RUNNING;
    }

    public boolean canJoinMidGame() {
        return configManager.canJoinMidGame();
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public List<Player> getHiders() {
        return hiders.stream()
                .map(uuid -> Bukkit.getPlayer(uuid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Player> getSeekers() {
        return seekers.stream()
                .map(uuid -> Bukkit.getPlayer(uuid))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        players.addAll(getHiders());
        players.addAll(getSeekers());
        return players;
    }

    public int getVotes() {
        return votes.size();
    }

    public int getNeededVotes() {
        return configManager.getNeededVotes();
    }

    public Location getSeekerSpawn() {
        return configManager.getSeekersSpawn();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    // Métodos de utilidade
    public void broadcastMessage(String message) {
        getAllPlayers().forEach(p -> p.sendMessage(message));
    }

    public void playGlobalSound(Sound sound, float pitch) {
        getAllPlayers().forEach(p -> p.playSound(p.getLocation(), sound, 1f, pitch));
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
}