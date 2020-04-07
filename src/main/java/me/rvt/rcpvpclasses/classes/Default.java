package me.rvt.rcpvpclasses.classes;

import me.rvt.rcpvpclasses.RCpvpClasses;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Default {
    private Player p;
    private int lives = 3;

    public Default(Player p) {
        this.p = p;
        giveHeads();
    }

    public Player getPlayer() {
        return p;
    }

    public void justDied() {
        this.lives--;
        giveHeads();
    }

    private void giveHeads() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, lives);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        meta.setDisplayName(RCpvpClasses.config.getString("settings.items.livesRemaining"));
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