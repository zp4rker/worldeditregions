package com.empcraft.wrg.object;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.regions.CuboidRegion;

public abstract class AbstractRegion {

    public abstract boolean hasPermission(Player player);
    public abstract CuboidRegionWrapper getcuboid(Player player);
}