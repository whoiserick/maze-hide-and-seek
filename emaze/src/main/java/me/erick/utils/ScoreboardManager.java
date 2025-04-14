package me.erick.utils;

import me.erick.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardManager {
    private final GameManager gameManager;
    private final Scoreboard board;
    private final Objective objective;

    public ScoreboardManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = board.registerNewObjective("mazehs", "dummy");

        setupScoreboard();
    }

    private void setupScoreboard() {
        objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Maze Hide & Seek");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Linhas fixas
        updateStaticLines();
    }

    public void updateGameScoreboard() {
        board.getEntries().forEach(board::resetScores);

        // Tempo restante
        String time = formatTime(gameManager.getRemainingTime());
        Score timeScore = objective.getScore(ChatColor.YELLOW + "Tempo: " + ChatColor.WHITE + time);
        timeScore.setScore(5);

        // Hiders
        List<String> hiders = gameManager.getHiders().stream()
                .map(p -> ChatColor.GREEN + " " + p.getName())
                .collect(Collectors.toList());

        Score hidersTitle = objective.getScore(ChatColor.DARK_GREEN + "Hiders:");
        hidersTitle.setScore(4);

        int hiderPos = 3;
        for (String hider : hiders) {
            objective.getScore(hider).setScore(hiderPos--);
        }

        // Seekers
        List<String> seekers = gameManager.getSeekers().stream()
                .map(p -> ChatColor.RED + " " + p.getName())
                .collect(Collectors.toList());

        Score seekersTitle = objective.getScore(ChatColor.DARK_RED + "Seekers:");
        seekersTitle.setScore(2);

        int seekerPos = 1;
        for (String seeker : seekers) {
            objective.getScore(seeker).setScore(seekerPos--);
        }

        // Atualizar para todos os jogadores
        gameManager.getAllPlayers().forEach(player -> player.setScoreboard(board));
    }

    private void updateStaticLines() {
        Score separator = objective.getScore(ChatColor.GRAY + "----------------");
        separator.setScore(0);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    public void updatePlayerScoreboard(Player player) {
        player.setScoreboard(board);
    }

    public void clearScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}