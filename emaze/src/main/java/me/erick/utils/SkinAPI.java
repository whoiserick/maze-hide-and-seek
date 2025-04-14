package me.erick.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SkinAPI {
    private final ConfigManager configManager;
    private final Map<String, SkinCache> skinCache = new HashMap<>();

    public SkinAPI(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public SkinData getSkinData(String textureUrl) throws Exception {
        if (skinCache.containsKey(textureUrl)) {
            SkinCache cache = skinCache.get(textureUrl);
            if (!cache.isExpired()) {
                return cache.getData();
            }
        }

        // Implementação simplificada para 1.8.9
        String value = textureUrl.contains("texture/") ?
                textureUrl.split("texture/")[1] : textureUrl;

        SkinData data = new SkinData(value, "");
        skinCache.put(textureUrl, new SkinCache(data, System.currentTimeMillis() + 3600000));
        return data;
    }

    public SkinData getFallbackSkin(String skinType) {
        // Método agora público
        String texture = skinType.equals("hider") ?
                "ewogICJ0aW1lc3RhbXAiIDogMTY1MDY3OTU5OTM0MiwKICAicHJvZmlsZUlkIiA6ICI1ZmQ1Y2I0OT" :
                "ewogICJ0aW1lc3RhbXAiIDogMTY1MDY4MDE5MTQ0MSwKICAicHJvZmlsZUlkIiA6ICJjY2Y1ZGM1";
        return new SkinData(texture, "");
    }

    public void clearCache() {
        skinCache.clear();
    }

    public static class SkinCache {
        private final SkinData data;
        private final long expiresAt;

        public SkinCache(SkinData data, long expiresAt) {
            this.data = data;
            this.expiresAt = expiresAt;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }

        public SkinData getData() {
            return data;
        }
    }

    public static class SkinData {
        private final String value;
        private final String signature;

        public SkinData(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }

        public String getValue() {
            return value;
        }

        public String getSignature() {
            return signature;
        }
    }
}