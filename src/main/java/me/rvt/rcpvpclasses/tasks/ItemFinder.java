package me.rvt.rcpvpclasses.tasks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemFinder {
    public static ItemStack inInventory(Player p, String itemName) {
        for (ItemStack currentItem: p.getInventory().getContents()) {
            if (currentItem != null)
                if (Objects.requireNonNull(currentItem.getItemMeta()).getDisplayName().equals(itemName))
                    return currentItem;
        }
        return null;
    }

    public static ItemStack inHand(Player p, String itemName) {
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        if (itemInHand.getItemMeta() != null && itemInHand.getItemMeta().getDisplayName().equals(itemName))
            return itemInHand;
        else return null;
    }
}