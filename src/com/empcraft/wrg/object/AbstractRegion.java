package com.empcraft.wrg.object;

import org.bukkit.entity.Player;

public abstract class AbstractRegion {

    public abstract boolean hasPermission(Player player);

    public abstract CuboidRegionWrapper getcuboid(Player player);
}
