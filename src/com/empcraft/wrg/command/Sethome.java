package com.empcraft.wrg.command;

import org.bukkit.entity.Player;

import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Sethome {

    public static boolean execute(Player player, String[] args) {
        if (MainUtil.hasPermission(player, "worldguard.region.flag.regions.own.*")) {
            if (args.length > 1) {
                if (RegionHandler.lastmask.get(player.getName()) == null) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                }
                else {
                    ProtectedRegion region = WorldguardFeature.worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName()));
                    LocationFlag flag = new LocationFlag("location");
                    org.bukkit.Location loc = player.getLocation();
                    Vector vector = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                    Location home = new Location(BukkitUtil.getLocalWorld(player.getWorld()), vector, loc.getYaw(), loc.getPitch());
                    region.setFlag(flag, home);
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG25"));
                    try {
                        WorldguardFeature.worldguard.getRegionManager(player.getWorld()).save();
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG3"));
            }
        }
        else {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &worldguard.region.addmember.own.*");
        }
        return true;
    }
    
}
