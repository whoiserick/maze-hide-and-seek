package me.erick.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.erick.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinManager {
    private final Main plugin;
    private final SkinAPI skinAPI;
    private final Map<UUID, String> originalSkins = new HashMap<>();

    public SkinManager(Main plugin) {
        this.plugin = plugin;
        this.skinAPI = new SkinAPI(plugin.getConfigManager());
    }

    public void applyHiderSkin(Player player) {
        applySkin(player, plugin.getConfigManager().getHiderSkin(), "hider");
    }

    public void applySeekerSkin(Player player) {
        applySkin(player, plugin.getConfigManager().getSeekerSkin(), "seeker");
    }

    private void applySkin(Player player, String textureUrl, String skinType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!originalSkins.containsKey(player.getUniqueId())) {
                        saveOriginalSkin(player);
                    }

                    SkinAPI.SkinData skinData = skinAPI.getSkinData(textureUrl);
                    setSkin(player, skinData.getValue(), skinData.getSignature());

                    player.sendMessage("§aSkin de " + skinType + " aplicada!");
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Falha ao aplicar skin: " + e.getMessage());
                    applyFallbackSkin(player, skinType);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void saveOriginalSkin(Player player) throws Exception {
        GameProfile profile = getGameProfile(player);
        Property texture = profile.getProperties().get("textures").iterator().next();
        originalSkins.put(player.getUniqueId(), texture.getValue());
    }

    private void setSkin(Player player, String value, String signature) throws Exception {
        GameProfile profile = getGameProfile(player);
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", new Property("textures", value, signature));
        updatePlayerSkin(player);
    }

    public void resetSkin(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (originalSkins.containsKey(player.getUniqueId())) {
                        setSkin(player, originalSkins.get(player.getUniqueId()), "");
                        originalSkins.remove(player.getUniqueId());
                    } else {
                        clearSkin(player);
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Erro ao resetar skin: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void applyFallbackSkin(Player player, String skinType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    SkinAPI.SkinData fallback = skinAPI.getFallbackSkin(skinType);
                    setSkin(player, fallback.getValue(), fallback.getSignature());
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Falha crítica ao aplicar fallback: " + e.getMessage());
                    clearSkin(player);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void clearSkin(Player player) {
        try {
            GameProfile profile = getGameProfile(player);
            profile.getProperties().removeAll("textures");
            updatePlayerSkin(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Erro ao limpar skin: " + e.getMessage());
        }
    }

    private GameProfile getGameProfile(Player player) throws Exception {
        Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
        return (GameProfile) profile;
    }

    private void updatePlayerSkin(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.hidePlayer(player);
                    online.showPlayer(player);
                }
            }
        }.runTask(plugin);
    }

    public void reload() {
        skinAPI.clearCache();
    }
}