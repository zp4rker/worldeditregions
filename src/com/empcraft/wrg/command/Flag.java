package com.empcraft.wrg.command;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;

public class Flag {

    public static boolean execute(Player player, String[] args) {
        String id = RegionHandler.id.get(player.getName());
        if (id == null) {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
            return false;
        }
        Bukkit.dispatchCommand(player, "/rg flag " + id + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " "));
        return true;
    }
    
}
