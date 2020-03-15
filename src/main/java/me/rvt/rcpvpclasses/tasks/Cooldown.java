package me.rvt.rcpvpclasses.tasks;

import me.rvt.rcpvpclasses.RCpvpClasses;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public class Cooldown {
    public static void loadDamge(ItemStack item, int damage, int cooldown) {
        Damageable meta = (Damageable) item.getItemMeta();
        if (meta != null && damage > 0) {
            damage--;
            meta.setDamage(damage);
            item.setItemMeta((ItemMeta) meta);
            int newDamage = damage;
            getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                    loadDamge(item, newDamage, cooldown), cooldown);
        }
    }
    public static void loadAmount(ItemStack item, int amount, int cooldown, boolean countUp) {
        if (countUp) {
            if (item.getAmount() < amount) {
                item.setAmount(item.getAmount() + 1);
            } else return;
        } else {
            if (item.getAmount() > amount) {
                item.setAmount(item.getAmount() - 1);
            } else return;
        }
        getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                loadAmount(item, amount, cooldown, countUp), cooldown);
    }
}