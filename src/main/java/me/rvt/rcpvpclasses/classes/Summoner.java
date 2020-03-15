package me.rvt.rcpvpclasses.classes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Summoner extends Default {
    private List < Entity > mobs = new ArrayList < > ();

    public Summoner(Player p) {
        super(p);
    }

    public void addMob(Entity e) {
        mobs.add(e);
    }

    public List < Entity > getMobs() {
        return mobs;
    }

    public void removeMob(Entity e) {
        mobs.remove(e);
    }
}