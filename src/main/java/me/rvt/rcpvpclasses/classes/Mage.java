package me.rvt.rcpvpclasses.classes;

import me.rvt.rcpvpclasses.RCpvpClasses;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static me.rvt.rcpvpclasses.tasks.PositionCheck.isAIR;
import static org.bukkit.Bukkit.getServer;

public class Mage extends Default {

    private boolean allowToThrowFire, canThrowIce;
    private List < Location > portals = new ArrayList < > ();
    private List < Player > tped = new ArrayList < > ();

    public Mage(Player p) {
        super(p);
    }

    public boolean canThrowFire() {
        return allowToThrowFire;
    }
    public void setThrowFireRule(boolean rule) {
        allowToThrowFire = rule;
    }
    public boolean canThrowIce() {
        return canThrowIce;
    }
    public void setThrowIceRule(boolean rule) {
        canThrowIce = rule;
    }
    public List < Location > getPortals() {
        return portals;
    }

    public void turnIntoIce(Entity e) {
        List < Block > destroy = new ArrayList < > ();
        Location standAt = e.getLocation().getBlock().getLocation();

        Location[] ice = {
                standAt.getBlock().getRelative(BlockFace.EAST).getLocation(),
                standAt.getBlock().getRelative(BlockFace.WEST).getLocation(),
                standAt.getBlock().getRelative(BlockFace.SOUTH).getLocation(),
                standAt.getBlock().getRelative(BlockFace.NORTH).getLocation()
        };

        for (int height = 0;
             height < RCpvpClasses.config.getInt("settings.mage.portalHeight"); height++) {
            for (Location temp: ice) {
                if (temp.getBlock().getType() == Material.AIR) {
                    temp.getBlock().setType(Material.ICE);
                    destroy.add(temp.getBlock());
                }
            }
            for (Location update: ice)
                update.setY(update.getBlockY() + 1);
        }
        getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                deleteBlock(destroy), RCpvpClasses.config.getInt("settings.mage.iceStunDuration"));
    }

    private void deleteBlock(List < Block > toAir) {
        for (Block b: toAir) {
            b.setType(Material.AIR);
        }
    }

    public void spreadFire(Entity e) {
        int radius = RCpvpClasses.config.getInt("settings.mage.fireballRadius"),
                xBlock = e.getLocation().getBlockX(),
                zBlock = e.getLocation().getBlockZ();
        Location loc = new Location(e.getWorld(), xBlock, e.getLocation().getBlockY(), zBlock);

        for (int a = xBlock - radius; a < xBlock + radius; a++) {
            loc.setX(a);

            for (int b = zBlock - radius; b < zBlock + radius; b++) {
                loc.setZ(b);
                loc.setY(isAIR(loc) + 1);
                if (loc.getBlock().getType() == Material.AIR)
                    loc.getBlock().setType(Material.FIRE);
            }
        }
    }

    public void setPortal() {
        Location targetBlock = getPlayer().getTargetBlock(null,
                RCpvpClasses.config.getInt("settings.mage.portalGunDistance")).getLocation();
        Location blockAbove = targetBlock.clone();
        blockAbove.setY(blockAbove.getBlockY() + 1);

        Location actualPortal = blockAbove.clone();
        actualPortal.setY(actualPortal.getBlockY() + 1);

        if (!(targetBlock.getBlock().getType() == Material.AIR) &&
                blockAbove.getBlock().getType() == Material.AIR &&
                actualPortal.getBlock().getType() == Material.AIR) {
            blockAbove.getBlock().setType(Material.PURPLE_CARPET);
            actualPortal.getBlock().setType(Material.NETHER_PORTAL);
            portals.add(blockAbove);
        }

        if (portals.size() > 2) {
            Block del = portals.get(0).getBlock();
            Location netherPortalBlock = del.getLocation().clone();
            netherPortalBlock.setY(netherPortalBlock.getBlockY() + 1);

            portals.remove(0);

            if (del.getType() == Material.PURPLE_CARPET)
                del.setType(Material.AIR);
            if (netherPortalBlock.getBlock().getType() == Material.NETHER_PORTAL)
                netherPortalBlock.getBlock().setType(Material.AIR);
        }
    }

    public void teleport(Player pToTP, Block walksOn) {
        Location portalFix = walksOn.getLocation().clone();
        portalFix.setY(portalFix.getBlockY() + 1);
        if (!(portalFix.getBlock().getType() == Material.NETHER_PORTAL)) {
            walksOn.setType(Material.AIR);
            portals.remove(walksOn.getLocation());
        }
        for (Location goTo: portals) {
            if (!tped.contains(pToTP) && !goTo.equals(walksOn.getLocation())) {
                Location upBlock = goTo.getBlock().getRelative(BlockFace.UP).getLocation();
                pToTP.teleport(upBlock);
                pToTP.getWorld().playEffect(upBlock, Effect.ENDER_SIGNAL, 0);
                tped.add(pToTP);
                getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getProvidingPlugin(RCpvpClasses.class), () ->
                        tped.remove(pToTP), RCpvpClasses.config.getInt("settings.mage.tpDelay"));
                return;
            }
        }
    }

    public void launchFireBall() {
        getPlayer().launchProjectile(Fireball.class);
    }
}