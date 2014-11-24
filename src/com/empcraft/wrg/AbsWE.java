package com.empcraft.wrg;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

public abstract class AbsWE {
    public abstract void setMask(Player player, CuboidRegion region);

    public abstract void removeMask(LocalSession session);

    public abstract boolean cancelBrush(Player player, Vector location, CuboidRegion region);
}
