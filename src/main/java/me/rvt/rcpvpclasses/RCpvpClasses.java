package me.rvt.rcpvpclasses;

import me.rvt.rcpvpclasses.classes.*;
import me.rvt.rcpvpclasses.tasks.ItemFinder;
import me.rvt.rcpvpclasses.technical.ConfigInit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static me.rvt.rcpvpclasses.tasks.PositionCheck.*;

public final class RCpvpClasses extends JavaPlugin implements Listener {

    public static FileConfiguration config;
    private List < Object > playerDB = new ArrayList < > ();
    private List < String > unplacable = new ArrayList < > ();
    private String lastSummoner = null;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        ConfigInit c = new ConfigInit();
        config = c.loadConfig(this);

        unplacable.add(config.getString("settings.items.livesItem"));
        unplacable.add(config.getString("settings.items.vampireGlass"));
        unplacable.add(config.getString("settings.items.livesRemainingItem"));
        unplacable.add(config.getString("settings.items.vampireTorch"));
    }

    @EventHandler
    private void EntitySpawnEvent(EntitySpawnEvent e) {
        if (inWorld(e.getEntity())) {
            if (lastSummoner != null) {
                for (Object s: playerDB) {
                    if (s instanceof Summoner && ((Summoner) s).getPlayer().getDisplayName().equals(lastSummoner)) {
                        ((Summoner) s).addMob(e.getEntity());
                        lastSummoner = null;
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    private void EntityDeathEvent(EntityDeathEvent e) {
        if (inWorld(e.getEntity())) {
            for (Object s: playerDB) {
                if (s instanceof Summoner && ((Summoner) s).getMobs().contains(e.getEntity())) {
                    ((Summoner) s).removeMob(e.getEntity());
                    break;
                }
            }
        }
    }

    @EventHandler
    private void EntityTargetEvent(EntityTargetEvent e) {
        if (inWorld(e.getEntity()) && e.getTarget() instanceof Player) {
            for (Object s: playerDB) {
                if (s instanceof Summoner &&
                        ((Summoner) s).getPlayer().equals(((Player) e.getTarget()).getPlayer()) &&
                        ((Summoner) s).getMobs().contains(e.getEntity())) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    private void PlayerQuitEvent(PlayerQuitEvent e) {
        removeFromDB(e.getPlayer(), false);
    }

    @EventHandler
    private void ProjectileHitEvent(ProjectileHitEvent e) {
        Entity ball = e.getEntity();

        if (inWorld(e.getEntity())) {
            if (ball instanceof Snowball) {
                for (Object o: playerDB)
                {
                    if (o instanceof Mage && ((Mage) o).canThrowIce()) {
                        if(e.getHitEntity() != null)
                            ((Mage) o).turnIntoIce(e.getHitEntity());
                        ((Mage) o).setThrowIceRule(false);
                        return;
                    }
                    if (o instanceof Thor && ((Thor) o).canThrow()) {
                        ((Thor) o).setThrowRule(false);
                        ((Thor) o).stormBombInit(ball);
                        return;
                    }
                }
            }

            if (ball instanceof Fireball) {
                for (Object m: playerDB)
                    if (m instanceof Mage && ((Mage) m).canThrowFire()) {
                        ((Mage) m).setThrowFireRule(false);
                        ((Mage) m).spreadFire(ball);
                        return;
                    }
            }
        }
    }

    @EventHandler
    private void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {

        if (inWorld(e.getEntity())) {

            for (Object o: playerDB) {
//              if (o instanceof Mage && ((Mage) o).canThrowIce()) {
//                  ((Mage) o).setThrowIceRule(false);
//                  return;
//              }
                if (o instanceof Vampire && ((Vampire) o).getPlayer().equals(e.getEntity())) {
//                  ((Vampire) o).makeVisible();
                    ((Vampire) o).throwHeart(e);
                    return;
                }
                if (e.getEntity() instanceof Player &&
                        o instanceof Vampire &&
                        e.getDamager() instanceof Player &&
                        ((Vampire) o).getPlayer().equals(e.getDamager())) {
                    Player a = (Player) e.getDamager();
                    Player v = (Player) e.getEntity();
                    ItemStack inHand = a.getInventory().getItemInMainHand();

                    if (inHand.getItemMeta() != null &&
                            inHand.getItemMeta().getDisplayName().equals(config.getString("settings.items.vampireTorch"))) {
                        ((Vampire) o).hitWithStick(v);
                        ((Vampire) o).throwHeart(v);
                    }
                }
            }
        }
    }

    @EventHandler
    private void PlayerTeleportEvent(PlayerTeleportEvent e) {
        if (isInArena(e.getPlayer()) && hasLivesItem(e.getPlayer())) {
            Player p = e.getPlayer();

            for (Object o: playerDB)
                if (o instanceof Default && ((Default) o).getPlayer().equals(p))
                    return;

            if (ItemFinder.inInventory(p, config.getString("settings.items.thorWeaponName")) != null) {
                playerDB.add(new Thor(p));
                return;
            }
            if (ItemFinder.inInventory(p, config.getString("settings.items.arbalistWeaponName")) != null) {
                playerDB.add(new Arbalist(p));
                return;
            }
            if (ItemFinder.inInventory(p, config.getString("settings.items.fireball")) != null) {
                playerDB.add(new Mage(p));
                return;
            }
            if (ItemFinder.inInventory(p, config.getString("settings.items.vampireGlass")) != null) {
                playerDB.add(new Vampire(p));
                return;
            }

            for (ItemStack i: p.getInventory().getContents()) {
                if (i != null && i.getType().toString().contains("SPAWN_EGG")) {
                    playerDB.add(new Summoner(p));
                    return;
                }
            }
            playerDB.add(new Default(p));
        } else {
            getServer().getScheduler().scheduleSyncDelayedTask(
                    this, () -> removeFromDB(e.getPlayer(), true),
                    RCpvpClasses.config.getInt("settings.respawnEffectsDelay") + 1);
        }
    }

    @EventHandler
    private void PlayerMoveEvent(PlayerMoveEvent e) {
        if (inWorld(e.getPlayer()) && isFighting(e.getPlayer())) {
            Player p = e.getPlayer();
            Block walksOn = Objects.requireNonNull(e.getTo()).getBlock();
            if (walksOn.getType().equals(Material.PURPLE_CARPET)) {
                for (Object m: playerDB) {
                    if (m instanceof Mage) {
                        if (((Mage) m).getPortals().contains(walksOn.getLocation())) {
                            ((Mage) m).teleport(p, walksOn);
                            return;
                        }
                    }
                }
            }

            if (walksOn.getType().equals(Material.REDSTONE_WIRE)) {
                for (Object v: playerDB) {
                    if (v instanceof Vampire) {
                        if (((Vampire) v).getPlayer().equals(p)) {
                            ((Vampire) v).stepOnBlood(walksOn);
                            return;
                        }
                    }
                }
                for (Object v: playerDB) {
                    if (v instanceof Vampire) {
                        ((Vampire) v).walkOnBlood(p);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    private void PlayerPortalEvent(PlayerPortalEvent e) {
        if (inWorld(e.getPlayer()) && isFighting(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    private void PlayerDeathEvent(PlayerDeathEvent e) {
        if (isInArena(e.getEntity())) {
            Player p = e.getEntity().getPlayer();

            for (Object o: playerDB) {
                if (o instanceof Default && ((Default) o).getPlayer().equals(p)) {
                    ((Default) o).justDied();
                }
                if (o instanceof Thor && ((Thor) o).getPlayer().equals(p)) {
                    getServer().getScheduler().scheduleSyncDelayedTask(
                            this, (((Thor) o)::spawnThor),
                            RCpvpClasses.config.getInt("settings.respawnEffectsDelay"));
                    return;
                }
                if (o instanceof Arbalist && ((Arbalist) o).getPlayer().equals(p)) {
                    getServer().getScheduler().scheduleSyncDelayedTask(
                            this, (((Arbalist) o)::addBoost),
                            RCpvpClasses.config.getInt("settings.respawnEffectsDelay"));
                    return;
                }
            }
        }
    }

    @EventHandler
    private void EntityPickupItemEvent(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player && isFighting((Player) e.getEntity())) {
            if (e.getItem().getItemStack().getType() == Material.REDSTONE) {
                for (Object v: playerDB) {
                    if (v instanceof Vampire && ((Vampire) v).getPlayer().equals(e.getEntity())) {
                        ((Vampire) v).consumeHeart(e.getItem().getItemStack().getAmount());
                        return;
                    }
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void PlayerInteractEvent(PlayerInteractEvent e) {
        if (isInArena(e.getPlayer())) {
            Player p = e.getPlayer();

            //ACTION CHECK
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                String axeName = config.getString("settings.items.thorWeaponName");

                if (ItemFinder.inHand(p, axeName) != null && isFighting(p)) {
                    for (Object t: playerDB) {
                        if (t instanceof Thor && ((Thor) t).getPlayer().equals(p)) {
                            ((Thor) t).axeReady();
                            return;
                        }
                    }
                }

                if (ItemFinder.inHand(p, config.getString("settings.items.thorGrenadeName")) != null) {
                    for (Object t: playerDB)
                        if (t instanceof Thor && ((Thor) t).getPlayer().equals(p)) {
                            ((Thor) t).setThrowRule(true);
                            return;
                        }
                }

                ItemStack fb = ItemFinder.inHand(p, config.getString("settings.items.fireball"));
                if (fb != null) {
                    for (Object m: playerDB) {
                        if (m instanceof Mage && ((Mage) m).getPlayer().equals(p)) {
                            ((Mage) m).setThrowFireRule(true);
                            ((Mage) m).launchFireBall();
                            fb.setAmount(fb.getAmount() - 1);
                            return;
                        }
                    }
                }

                if (ItemFinder.inHand(p, config.getString("settings.items.snowballFreeze")) != null) {
                    for (Object m: playerDB) {
                        if (m instanceof Mage && ((Mage) m).getPlayer().equals(p)) {
                            ((Mage) m).setThrowIceRule(true);
                            return;
                        }
                    }
                }
                if (ItemFinder.inHand(p, config.getString("settings.items.portalGun")) != null && isFighting(p)) {
                    for (Object m: playerDB) {
                        if (m instanceof Mage && ((Mage) m).getPlayer().equals(p)) {
                            ((Mage) m).setPortal();
                            return;
                        }
                    }
                }

                if (ItemFinder.inHand(p, config.getString("settings.items.vampireGlass")) != null) {
                    for (Object v: playerDB) {
                        if (v instanceof Vampire && ((Vampire) v).getPlayer().equals(p)) {
                            ((Vampire) v).makeInvisible();
                            return;
                        }
                    }
                }

            } else {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (p.getInventory().getItemInMainHand().getType().toString().contains("SPAWN_EGG")) {
                        lastSummoner = p.getDisplayName();
                        return;
                    }

                    ItemMeta tempMeta = p.getInventory().getItemInMainHand().getItemMeta();
                    if (tempMeta != null && unplacable.contains(tempMeta.getDisplayName())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean inWorld(Entity e) {
        List < String > arenas = config.getStringList("settings.arenaNames");
        return arenas.contains(e.getWorld().getName());
    }

    private void removeFromDB(Player p, boolean check) {
        if (check)
            if (isFighting(p)) return;

        for (Object o: playerDB) {
            if (o instanceof Default && ((Default) o).getPlayer().equals(p)) {
                playerDB.remove(o);
                break;
            }
        }
    }
}