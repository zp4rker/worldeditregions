package com.empcraft.wrg.command;

import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.entity.Player;

public class Share {
    public static boolean execute(Player player, String[] args) {
        if (MainUtil.hasPermission(player, "worldguard.region.addowner.own.*")) {
            if (args.length > 1) {
                if (RegionHandler.lastmask.get(player.getName()) == null) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                } else {
                    RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
                    final DefaultDomain domain = manager.getRegion(RegionHandler.id.get(player.getName())).getOwners();
                    domain.addPlayer(args[1]);
                    manager.getRegion(RegionHandler.id.get(player.getName())).setOwners(domain);
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG2") + " &a" + args[1] + "&7.");
                    try {
                        manager.save();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG11"));
            }
        } else {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.addowner.own.*");
        }
        return true;
    }
}
