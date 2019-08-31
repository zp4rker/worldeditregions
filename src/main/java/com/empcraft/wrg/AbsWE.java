package com.empcraft.wrg;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.entity.Player;

public abstract class AbsWE {
    public abstract void setMask(Player player, CuboidRegion region);

    public abstract void removeMask(LocalSession session);

    public abstract boolean cancelBrush(Player player, BlockVector3 location, CuboidRegion region);
}
