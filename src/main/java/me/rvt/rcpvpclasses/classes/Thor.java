package me.rvt.rcpvpclasses.classes;

import me.rvt.rcpvpclasses.RCpvpClasses;
import me.rvt.rcpvpclasses.tasks.Cooldown;
import me.rvt.rcpvpclasses.tasks.ItemFinder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

import static me.rvt.rcpvpclasses.tasks.PositionCheck.isAIR;
import static org.bukkit.Bukkit.getServer;

public class Thor extends Default {

    private boolean isAllowedToThrow, axeReady;
    int radius = RCpvpClasses.config.getInt("settings.thor.grenadeRadius");

    public Thor(Player p)

    {
        super(p);
        spawnThor();
    }

    public boolean canThrow() {
        return isAllowedToThrow;
    }
    public void setThrowRule(boolean rule) {
        isAllowedToThrow = rule;
    }

    public void strikeWithThunder() {
        Block loc = getPlayer().getTargetBlock(null, 32);
        loc.getWorld().strikeLightning(loc.getLocation());
    }

    public void spawnThor()
    {
        axeReady = true;
        getPlayer().getWorld().strikeLightningEffect(getPlayer().getLocation());
    }

    public void stormBombInit(Entity ball) {
        Location ballLocation = ball.getLocation();
        int[] xBlocks = new int[radius * 2],
                zBlocks = new int[radius * 2];

        for (int blockIt = 0; blockIt < radius * 2; blockIt++) {
            xBlocks[blockIt] = (ballLocation.getBlockX() + radius) - blockIt;
            zBlocks[blockIt] = (ballLocation.getBlockZ() + radius) - blockIt;
        }
        doStormthunder(ball, ballLocation, xBlocks, zBlocks, RCpvpClasses.config.getInt("settings.thor.grenadeThunderCount"));
    }

    public void doStormthunder(Entity ball, Location loc, int[] xBlocks, int[] zBlocks, int count) {
        if (count > 0) {
            int randX = new Random().nextInt(radius),
                    randZ = new Random().nextInt(radius);

            final int nextCount = count - 1;

            loc.setX(xBlocks[randX]);
            loc.setZ(zBlocks[randZ]);
            loc.setY(isAIR(loc));

            ball.getWorld().strikeLightning(loc);
            getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                    doStormthunder(ball, loc, xBlocks, zBlocks, nextCount), RCpvpClasses.config.getInt("settings.thor.grenadeDelay"));
        }
    }

    public void axeReady() {
        int cooldown = RCpvpClasses.config.getInt("settings.thor.strikeCooldown");
        int axeDamage = RCpvpClasses.config.getInt("settings.thor.axeDmg");
        String axeName = RCpvpClasses.config.getString("settings.items.thor.weaponName");
        ItemStack axe = ItemFinder.inInventory(getPlayer(), axeName);
        assert axe != null;
        Damageable meta = (Damageable) axe.getItemMeta();
        assert meta != null;
        if (axeReady) {
            meta.setDamage(axeDamage);
            axe.setItemMeta((ItemMeta) meta);
            strikeWithThunder();
            axeReady = false;
            Cooldown.loadDamge(axe, meta.getDamage(), cooldown);

            getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                    axeReady = true, cooldown * axeDamage);
        }
    }
}