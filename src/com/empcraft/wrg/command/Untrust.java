package com.empcraft.wrg.command;

import org.bukkit.entity.Player;

import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldguard.domains.DefaultDomain;

public class Untrust {
    public static boolean execute(Player player, String[] args) {
        if (MainUtil.hasPermission(player, "worldguard.region.removemember.own.*")) {
            if (args.length > 1) {
                if (RegionHandler.lastmask.get(player.getName()) == null) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                }
                else {
                    final DefaultDomain domain = WorldguardFeature.worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).getMembers();
                    domain.removePlayer(args[1]);
                    WorldguardFeature.worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).setMembers(domain);
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG12") + " &c" + args[1] + "&7.");
                    try {
                        WorldguardFeature.worldguard.getRegionManager(player.getWorld()).save();
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG13"));
            }
        }
        else {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.removemember.own.*");
        }
        return true;
    }
}
