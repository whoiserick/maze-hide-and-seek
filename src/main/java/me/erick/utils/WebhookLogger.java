package me.erick.utils;

import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookLogger {
    private final ConfigManager configManager;

    public WebhookLogger(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void logGameStart() {
        if (!configManager.isWebhookEnabled()) return;
        sendToDiscord("Game Started: Maze Hide and Seek");
    }

    public void logGameEnd(String winner) {
        if (!configManager.isWebhookEnabled()) return;
        sendToDiscord("Game Ended: " + winner + " won!");
    }

    private void sendToDiscord(String message) {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("MazeHideAndSeek"), () -> {
            try {
                String jsonPayload = "{\"content\":\"" + message + "\"}";
                URL url = new URL(configManager.getWebhookUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getInputStream().close();
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to send webhook: " + e.getMessage());
            }
        });
    }
}