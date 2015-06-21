package com.empcraft.wrg.command;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Home {

    public static boolean execute(Player player, String[] args) {
        if (!MainUtil.hasPermission(player, "worldguard.region.teleport.own.*")) {
            // no perm
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.teleport.own.*");
            return false;
        }
        if (args.length < 2) {
            if (MainUtil.hasPermission(player, "worldguard.region.teleport.*")) {
                MainUtil.sendMessage(player, "&cUsage: /wrg home [home] [number]");
            }
            else {
                MainUtil.sendMessage(player, "&cUsage: /wrg home [number]");
            }
            return false;
        }
        // /wrg home Empire92 1
        // /wrg home Empire92//1
        // /wrg home 1
        
        String name;
        int index = -1;
        UUID uuid = player.getUniqueId();
        
        if (args.length == 3) {
            name = args[1];
            try {
                index = Integer.parseInt(args[2]);
            }
            catch (Exception e) {
                MainUtil.sendMessage(player, "&7Invalid home number: &c" + args[2]);
                return false;
            }
            
        }
        else if (args.length == 2) {
            String[] split = args[1].split("//");
            name = split[0];
            if (split.length == 2) {
                try {
                    index = Integer.parseInt(split[1]);
                }
                catch (Exception e) {
                    MainUtil.sendMessage(player, "&7Invalid home number: &c" + split[1]);
                    return false;
                }
            }
        }
        else {
            // usage
            if (MainUtil.hasPermission(player, "worldguard.region.teleport.*")) {
                MainUtil.sendMessage(player, "&cUsage: /wrg home [home] [number]");
            }
            else {
                MainUtil.sendMessage(player, "&cUsage: /wrg home [number]");
            }
            return false;
        }
        if (!name.equalsIgnoreCase(player.getName()) && !name.equalsIgnoreCase(uuid.toString())) {
            if (!MainUtil.hasPermission(player, "worldguard.region.teleport.*")) {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.teleport.*");
                return false;
            }
        }
        World world = player.getWorld();
        RegionManager manager = WorldguardFeature.worldguard.getRegionManager(world);
        String id;
        if (index == -1) {
            id = name;
        }
        else {
            id = name + "//" + index;
        }
        ProtectedRegion r = manager.getRegion(id);
        if (r == null) {
            MainUtil.sendMessage(player, "&cRegion does not exist: " + id);
            return false;
        }
        
        Location loc = r.getFlag(new LocationFlag("teleport"));
        org.bukkit.Location l;
        if (loc == null) {
            LocalWorld lw = BukkitUtil.getLocalWorld(world);
            BlockVector bot = r.getMinimumPoint();
            BlockVector top = r.getMaximumPoint();
            int x = bot.getBlockX() + (top.getBlockX() - bot.getBlockX()) / 2;
            int z = bot.getBlockZ() + (top.getBlockZ() - bot.getBlockZ()) / 2;
            world.loadChunk(x >> 4, z >> 4);
            int y = world.getHighestBlockYAt(x, z);
            if (y < 2) {
                y = 70;
            }
            l = new org.bukkit.Location(world, x, y, z);
        }
        else {
            Vector v = loc.getPosition();
            l = new org.bukkit.Location(world, v.getX(), v.getY(), v.getZ(), loc.getYaw(), loc.getPitch());
        }
        MainUtil.sendMessage(player, "&6Teleporting...");
        player.teleport(l);
        return true;
    }
}
