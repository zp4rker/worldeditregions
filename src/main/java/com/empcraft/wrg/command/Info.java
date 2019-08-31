package com.empcraft.wrg.command;

import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Info {
    public static boolean execute(Player player, String[] args) {
        if (RegionHandler.id.get(player.getName()) == null) {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
        } else {
            CuboidRegion mask = RegionHandler.lastmask.get(player.getName());
            if (mask != null) {
                WorldguardFeature.temporaryHighlight(mask.getMinimumPoint().toBlockVector2(), mask.getMaximumPoint().toBlockVector2(), player, Material.GOLD_BLOCK, 60);
            }
            Bukkit.dispatchCommand(player, "region info " + RegionHandler.id.get(player.getName()));
        }
        return true;
    }
}
