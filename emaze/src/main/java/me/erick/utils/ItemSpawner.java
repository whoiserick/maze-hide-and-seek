package me.erick.utils;

import me.erick.GameManager;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ItemSpawner {
    private final GameManager gameManager;
    private final List<Location> spawnPoints;
    private final Map<UUID, Item> activeItems;
    private BukkitRunnable spawnTask;

    public ItemSpawner(GameManager gameManager) {
        this.gameManager = gameManager;
        this.spawnPoints = gameManager.getConfigManager().getItemSpawnPoints();
        this.activeItems = new HashMap<>();
    }

    public void startSpawning() {
        if (spawnTask != null) return;

        spawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameManager.isGameRunning()) {
                    this.cancel();
                    return;
                }

                spawnRandomItem();
            }
        };
        spawnTask.runTaskTimer(gameManager.getPlugin(), 0L, 20L * gameManager.getConfigManager().getItemSpawnInterval());
    }

    public void stopSpawning() {
        if (spawnTask != null) {
            spawnTask.cancel();
            spawnTask = null;
        }
        clearItems();
    }

    private void spawnRandomItem() {
        if (spawnPoints.isEmpty()) return;

        Location spawnLoc = spawnPoints.get(new Random().nextInt(spawnPoints.size()));
        ItemStack item = generateRandomItem();

        Item droppedItem = spawnLoc.getWorld().dropItem(spawnLoc, item);
        droppedItem.setVelocity(new Vector(0, 0.1, 0));

        // Efeito de brilho (alternativa para 1.8.9 sem setGlowing)
        spawnLoc.getWorld().playEffect(spawnLoc, Effect.MOBSPAWNER_FLAMES, 0);

        droppedItem.setPickupDelay(10);
        activeItems.put(droppedItem.getUniqueId(), droppedItem);

        // Efeitos sonoros e visuais
        spawnLoc.getWorld().playSound(spawnLoc, Sound.ITEM_PICKUP, 1f, 1.5f);
        spawnParticles(spawnLoc);
    }

    private void spawnParticles(Location loc) {
        // Efeito de partículas alternativo para 1.8.9
        for (int i = 0; i < 15; i++) {
            loc.getWorld().playEffect(loc, Effect.HAPPY_VILLAGER, 0);
        }
    }

    private ItemStack generateRandomItem() {
        Random random = new Random();
        ItemStack item;

        switch (random.nextInt(5)) {
            case 0:
                item = new ItemStack(Material.IRON_SWORD);
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                break;
            case 1:
                item = new ItemStack(Material.POTION, 1, (short) 8194); // Poção de velocidade
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.BLUE + "Poção de Velocidade");
                item.setItemMeta(meta);
                break;
            case 2:
                item = new ItemStack(Material.FEATHER);
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 3);
                break;
            case 3:
                item = new ItemStack(Material.COMPASS);
                ItemMeta compassMeta = item.getItemMeta();
                compassMeta.setDisplayName(ChatColor.YELLOW + "Rastreador de Hiders");
                compassMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Aponte para o Hider mais próximo"));
                item.setItemMeta(compassMeta);
                break;
            default:
                item = new ItemStack(Material.GOLDEN_APPLE);
        }

        return item;
    }

    public void removeItem(Item item) {
        activeItems.remove(item.getUniqueId());
        item.remove();
    }

    public void clearItems() {
        new ArrayList<>(activeItems.values()).forEach(Item::remove);
        activeItems.clear();
    }

    public boolean isActiveItem(Item item) {
        return activeItems.containsKey(item.getUniqueId());
    }
}