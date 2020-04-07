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
        List <String> dropDesc = new ArrayList<>();
        dropDesc.add("Received by taking damage or applying bleeding.");
        dropDesc.add("Place it on the ground to unlock it's full potential.");

        config.set("settings.arena.name", "LegendaryPvP");
        config.set("settings.arena.worldName", "pvp");

        config.set("settings.items.thor.weaponName", ChatColor.AQUA + "" + ChatColor.BOLD + "Stormbringer");
        config.set("settings.items.arbalist.weaponName", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Crossbow | Dragon Lore");
        config.set("settings.items.thor.grenadeName", ChatColor.AQUA + "" + ChatColor.BOLD + "Thunderstorm");
        config.set("settings.items.livesRemaining", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Lives Remaining");
        config.set("settings.items.mage.fireball", ChatColor.GOLD + "" + ChatColor.BOLD + "Get Fired");
        config.set("settings.items.mage.snowball", ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Get Iced");
        config.set("settings.items.mage.compass", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Get Portal-ed");
        config.set("settings.items.vampire.glass", ChatColor.GRAY + "" + ChatColor.BOLD + "In The Shadows");
        config.set("settings.items.vampire.torch", ChatColor.DARK_RED + "" + ChatColor.BOLD + "Bloody Stick");
        config.set("settings.items.vampire.bloodDrop", ChatColor.DARK_RED + "" + ChatColor.BOLD + "A Drop of Blood");
        config.set("settings.lore.bloodDrop", dropDesc);

        config.set("settings.respawnDelay", 10);
        config.set("settings.thor.strikeCooldown", 2);
        config.set("settings.arbalist.effectsDuration", 9999);
        config.set("settings.arbalist.effectsAmp", 2);
        config.set("settings.thor.grenadeThunderCount", 15);
        config.set("settings.thor.grenadeDelay", 8);
        config.set("settings.thor.grenadeRadius", 7);
        config.set("settings.thor.axeDmg", 31);
        config.set("settings.mage.fireballRadius", 2);
        config.set("settings.mage.iceStunDuration", 100);
        config.set("settings.mage.portalGunDistance", 20);
        config.set("settings.mage.tpDelay", 40);
        config.set("settings.mage.portalHeight", 4);
        config.set("settings.vampire.invisibilityCooldown", 20);
        config.set("settings.vampire.invisibleItemAmount", 10);
        config.set("settings.vampire.howManyBats", 6);
        config.set("settings.vampire.wingsId", 38);
        config.set("settings.vampire.bloodTrapDelay", 20);
        config.set("settings.vampire.bloodTrapDamage", 2);
        config.set("settings.vampire.torchAmount", 10);
        config.set("settings.vampire.torchHitSlowEffect", 60);
        config.set("settings.vampire.bloodDropsAmount", 3);
        config.set("settings.vampire.bloodDropInterval", 60);
        config.set("settings.vampire.bloodSpeedDuration", 60);
    }
}