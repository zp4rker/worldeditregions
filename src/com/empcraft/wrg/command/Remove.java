package com.empcraft.wrg.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;

public class Remove {
    public static boolean execute(Player player, String[] args) {
        if (RegionHandler.lastmask.get(player.getName()) == null) {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
            if (MainUtil.hasPermission(player, "worldguard.region.remove.*")) {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG9"));
            }
        }
        else {
            if (MainUtil.hasPermission(player, "worldguard.region.remove.own.*")) {
                Bukkit.dispatchCommand(player, "region remove " + RegionHandler.lastmask.get(player.getName()));
            }
            else {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.remove.own.*");
            }
        }
        return true;
    }
}
