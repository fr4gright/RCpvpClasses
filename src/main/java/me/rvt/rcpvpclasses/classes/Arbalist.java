package me.rvt.rcpvpclasses.classes;

import me.rvt.rcpvpclasses.RCpvpClasses;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Arbalist extends Default {

    public Arbalist(Player p) {
        super(p);
        addBoost();
    }

    public void addBoost() {

        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                RCpvpClasses.config.getInt("settings.arbalistEffectsDuration"),
                  RCpvpClasses.config.getInt("settings.arbalistEffectsAmp")));

        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP,
                RCpvpClasses.config.getInt("settings.arbalistEffectsDuration"),
                RCpvpClasses.config.getInt("settings.arbalistEffectsAmp")));
    }
}