package me.rvt.rcpvpclasses.tasks;

import me.rvt.rcpvpclasses.RCpvpClasses;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.slipcor.pvparena.api.PVPArenaAPI.getArenaName;

public class PositionCheck {
    private static FileConfiguration config = RCpvpClasses.config;

    static public boolean isInArena(Player p) {
        List < String > arena = config.getStringList("settings.arenaNames");
        return arena.contains(getArenaName(p));
    }

    public static boolean isFighting(Player p) {
        return ItemFinder.inInventory(p, config.getString("settings.items.livesRemainingItem")) != null;
    }

    public static boolean hasLivesItem(Player p)
    {
        String itemName = RCpvpClasses.config.getString("settings.items.livesItem");

        for(ItemStack i:p.getInventory().getArmorContents())
            if (i != null && i.getItemMeta() != null && i.getItemMeta().getDisplayName().equals(itemName))
                return true;

        for(ItemStack i:p.getInventory().getContents())
            if (i != null && i.getItemMeta() != null && i.getItemMeta().getDisplayName().equals(itemName))
                return true;
        return false;
    }

    public static int isAIR(Location loc) {
        List < Material > exclude = new ArrayList < > ();
        int backup = loc.getBlockY();

        exclude.add(Material.AIR);
        exclude.add(Material.BARRIER);

        loc.setY(backup - 10);

        for (int block = 0; block < 20; block++) {
            loc.setY(loc.getBlockY() + 1);

            if (exclude.contains(loc.getBlock().getType())) {
                return loc.getBlockY() - 1;
            }
        }
        return backup;
    }
}