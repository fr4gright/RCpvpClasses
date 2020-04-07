package me.rvt.rcpvpclasses.tasks;

import com.bekvon.bukkit.residence.Residence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PositionCheck {
    private static Residence res = (Residence) Bukkit.getServer().getPluginManager().getPlugin("Residence");

    static public boolean isInRes(Player p, String resName) {
        if(res.getResidenceManager().getByLoc(p) != null)
            return resName.equals(res.getResidenceManager().getByLoc(p).getResidenceName());
        else return false;
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