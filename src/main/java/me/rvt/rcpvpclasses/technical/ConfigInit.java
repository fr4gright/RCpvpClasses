package me.rvt.rcpvpclasses.technical;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigInit {
    private File conf;
    private static FileConfiguration config;

    public FileConfiguration loadConfig(Plugin plugin) {

        if (conf == null) {
            conf = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(conf);

        if (!config.contains("settings")) {

            init();

            try {
                config.save(conf);
            } catch (IOException var3) {
                System.out.println("[RCpvpClasses] Unable to save config!");
            }
        }
        return config;
    }

    private void init() {
        List < String > arenas = new ArrayList < > ();
        arenas.add("pvp");

        List <String> dropDesc = new ArrayList<>();
        dropDesc.add("Received by taking damage or applying bleeding.");
        dropDesc.add("Place it on the ground to unlock it's full potential.");

        config.set("settings.arenaNames", arenas);

        config.set("settings.items.thorWeaponName", ChatColor.AQUA + "" + ChatColor.BOLD + "Stormbringer");
        config.set("settings.items.arbalistWeaponName", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Crossbow | Dragon Lore");
        config.set("settings.items.livesItem", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Lives");
        config.set("settings.items.thorGrenadeName", ChatColor.AQUA + "" + ChatColor.BOLD + "Thunderstorm");
        config.set("settings.items.livesRemainingItem", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Lives Remaining");
        config.set("settings.items.fireball", ChatColor.GOLD + "" + ChatColor.BOLD + "Get Fired");
        config.set("settings.items.snowballFreeze", ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Get Iced");
        config.set("settings.items.portalGun", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Get Portal-ed");
        config.set("settings.items.vampireGlass", ChatColor.GRAY + "" + ChatColor.BOLD + "In The Shadows");
        config.set("settings.items.vampireTorch", ChatColor.DARK_RED + "" + ChatColor.BOLD + "Bloody Stick");
        config.set("settings.items.bloodDrop", ChatColor.DARK_RED + "" + ChatColor.BOLD + "A Drop of Blood");
        config.set("settings.bloodDropLore", dropDesc);

        config.set("settings.thorStrikeCooldown", 2);
        config.set("settings.arbalistEffectsDuration", 9999);
        config.set("settings.arbalistEffectsAmp", 2);
        config.set("settings.respawnEffectsDelay", 5);
        config.set("settings.thorGrenadeThunderCount", 15);
        config.set("settings.thorGrenadeDelay", 8);
        config.set("settings.thorGrenadeRadius", 7);
        config.set("settings.thorAxeDmg", 31);
        config.set("settings.fireballRadius", 2);
        config.set("settings.iceStunDuration", 100);
        config.set("settings.portalGunDistance", 15);
        config.set("settings.mageTpDelay", 40);
        config.set("settings.vampireInvisibilityCooldown", 20);
        config.set("settings.invisibleItemAmount", 10);
        config.set("settings.howManyBats", 6);
        config.set("settings.VampWingsId", 38);
        config.set("settings.bloodTrapDelay", 20);
        config.set("settings.bloodTrapDamage", 2);
        config.set("settings.vampireTorchAmount", 10);
        config.set("settings.torchHitSlowEffect", 60);
        config.set("settings.bloodDropsAmount", 3);
        config.set("settings.bloodDropInterval", 60);
        config.set("settings.bloodSpeedDuration", 60);
    }
}