package me.rvt.rcpvpclasses.classes;

import me.rvt.rcpvpclasses.RCpvpClasses;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public class Default {
    private Player p;
    private int lives = 3;
    private boolean isDead;

    public Default(Player p) {
        this.p = p;
        prepareHead();
    }

    public Player getPlayer() {
        return p;
    }
    public boolean deathStatus() { return isDead; }
    public int getLives() { return lives; }

    public void justDied() {
        isDead = true;
        this.lives--;
        getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class),
                this::prepareHead, RCpvpClasses.config.getInt("settings.respawnEffectsDelay"));
    }

    private void prepareHead() {
        String headName = RCpvpClasses.config.getString("settings.items.livesItem");

        for(ItemStack i:p.getInventory().getArmorContents())
        {
            if (i != null && i.getItemMeta() != null && i.getItemMeta().getDisplayName().equals(headName))
            {
                i.setAmount(0);
                break;
            }
        }

        for(ItemStack i:p.getInventory().getContents())
        {
            if (i != null && i.getItemMeta() != null && i.getItemMeta().getDisplayName().equals(headName))
            {
                i.setAmount(0);
                break;
            }
        }
        giveHeads();
    }

    private void giveHeads() {
        isDead = false;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, lives);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        meta.setDisplayName(RCpvpClasses.config.getString("settings.items.livesRemainingItem"));
        head.setItemMeta(meta);

        Inventory inv = p.getInventory();

        for (int index = 8; index >= 0; index--){
            if (inv.getItem(index) == null)
            {
                inv.setItem(index, head);
                return;
            }
        }
    }
}