package com.empcraft.wrg.command;

import com.empcraft.wrg.util.MainUtil;
import org.bukkit.entity.Player;

public class Help {
    public static boolean execute(Player player, String[] args) {
        MainUtil.sendMessage(player, MainUtil.getMessage("MSG7"));
        return true;
    }
}
