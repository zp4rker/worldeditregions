package com.empcraft.wrg.command;

import org.bukkit.entity.Player;

import com.empcraft.wrg.util.MainUtil;

public class Help {
    public static boolean execute(Player player, String[] args) {
        MainUtil.sendMessage(player, MainUtil.getMessage("MSG7"));
        return true;
    }
}
