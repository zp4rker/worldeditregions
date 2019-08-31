package com.empcraft.wrg.command;

import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public class Sethome {

    public static boolean execute(Player player, String[] args) {
        if (MainUtil.hasPermission(player, "worldguard.region.flag.regions.own.*")) {
            if (args.length > 1) {
                if (RegionHandler.lastmask.get(player.getName()) == null) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                } else {
                    ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).getRegion(RegionHandler.id.get(player.getName()));
                    region.setFlag(Flags.SPAWN_LOC, BukkitAdapter.adapt(player.getLocation()));
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG25"));
                    try {
                        WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).save();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG3"));
            }
        } else {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &worldguard.region.addmember.own.*");
        }
        return true;
    }

}
