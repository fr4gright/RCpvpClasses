package me.rvt.rcpvpclasses.classes;

import me.rvt.rcpvpclasses.RCpvpClasses;
import me.rvt.rcpvpclasses.tasks.Cooldown;
import me.rvt.rcpvpclasses.tasks.ItemFinder;
import me.rvt.rcpvpclasses.tasks.PositionCheck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Vampire extends Default {
    private boolean isInvisible;
    private ItemStack invisibleItem;
    private int
            glassCount = RCpvpClasses.config.getInt("settings.invisibleItemAmount"),
            cooldown = RCpvpClasses.config.getInt("settings.vampireInvisibilityCooldown"),
            wingId = RCpvpClasses.config.getInt("settings.VampWingsId"),
            batCount = RCpvpClasses.config.getInt("settings.howManyBats");
    private Entity[] bats = new Entity[batCount];
    private List < Player > bloodSucked = new ArrayList < > ();

    public Vampire(Player p) {
        super(p);
    }

    public void makeInvisible() {
        invisibleItem = ItemFinder.inInventory(getPlayer(),
                RCpvpClasses.config.getString("settings.items.vampireGlass"));

        if (invisibleItem != null && invisibleItem.getAmount() == glassCount && !isInvisible) {
            for (int i = 0; i < RCpvpClasses.config.getInt("settings.howManyBats"); i++)
                bats[i] = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), EntityType.BAT);

            isInvisible = true;
            getPlayer().getInventory().setItem(wingId, new ItemStack(Material.AIR, 1));
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 1));

            Cooldown.loadAmount(invisibleItem, 1, cooldown, false);

            for (int i = 0; i < 9; i++) {
                if (getPlayer().getInventory().getItem(i) == null) {
                    getPlayer().getInventory().setHeldItemSlot(i);
                    break;
                }
            }

            getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class),
                    this::makeVisible, cooldown * glassCount);
        }
    }

    public void makeVisible() {
        invisibleItem = ItemFinder.inInventory(getPlayer(),
                RCpvpClasses.config.getString("settings.items.vampireGlass"));

        if (isInvisible && invisibleItem != null && invisibleItem.getAmount() < glassCount) {
            for (int i = 0; i < batCount; i++)
                bats[i].remove();

            isInvisible = false;
            ItemStack wings = new ItemStack(Material.ELYTRA, 1);
            getPlayer().getInventory().setItem(wingId, wings);
            getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);

            Cooldown.loadAmount(invisibleItem, glassCount, cooldown, true);
        }
    }

    public void consumeHeart(int amount) {
        if (getPlayer().getHealth() < 20) {
            for (int i = 0; i < amount; i++)
                if (getPlayer().getHealth() <= 18)
                    getPlayer().setHealth(getPlayer().getHealth() + 2);
                else break;
        }
    }

    public void throwHeart(EntityDamageByEntityEvent e) {
        if (e.getDamage() > 2 && e.getDamager() instanceof Player) {
            throwHeart((Player) e.getDamager());
        }
    }

    public void throwHeart(Player p) {
        ItemStack drop = new ItemStack(Material.REDSTONE, 1);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.REDSTONE);
        meta.setDisplayName(RCpvpClasses.config.getString("settings.items.bloodDrop"));
        meta.setLore(RCpvpClasses.config.getStringList("settings.bloodDropLore"));
        drop.setItemMeta(meta);
        getPlayer().getWorld().dropItemNaturally(p.getLocation(), drop);
    }

    public void walkOnBlood(Player p) {
        if (!bloodSucked.contains(p)) {
            bloodSucked.add(p);
            p.damage(RCpvpClasses.config.getInt("settings.bloodTrapDamage"));
            getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                    bloodSucked.remove(p), RCpvpClasses.config.getInt("settings.bloodTrapDelay"));
        }
    }

    public void hitWithStick(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                RCpvpClasses.config.getInt("settings.torchHitSlowEffect"), 2));
        dropBlood(p, RCpvpClasses.config.getInt("settings.bloodDropsAmount"));
    }

    private void dropBlood(Player p, int amount) {
        if (amount > 0 && PositionCheck.isFighting(p)) {
            Location turnRed = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation().clone();
            turnRed.setY(turnRed.getY() + 1);

            if (turnRed.getBlock().getType() == Material.AIR)
                turnRed.getBlock().setType(Material.REDSTONE_WIRE);

            int newAmount = --amount;

            getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                    dropBlood(p, newAmount), RCpvpClasses.config.getInt("settings.bloodDropInterval"));
        }
    }

    public void stepOnBlood(Block blood) {
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                RCpvpClasses.config.getInt("settings.bloodSpeedDuration"), 2));
        blood.setType(Material.AIR);
    }
}