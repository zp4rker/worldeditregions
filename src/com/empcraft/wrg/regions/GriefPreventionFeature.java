package com.empcraft.wrg.regions;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

public class GriefPreventionFeature extends AbstractRegion {
    Plugin           griefprevention;
    WorldeditRegions plugin;

    public GriefPreventionFeature(final Plugin griefpreventionplugin, final WorldeditRegions worldeditregions) {
        this.griefprevention = griefpreventionplugin;
        this.plugin = worldeditregions;

    }

    @Override
    public CuboidRegionWrapper getcuboid(final Player player) {
        final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
        if (claim != null) {
            boolean hasPerm = false;
            if (claim.getOwnerName().equalsIgnoreCase(player.getName())) {
                hasPerm = true;
            }
            else if (claim.isManager(player.getName()) && MainUtil.hasPermission(player, "wrg.griefprevention.member")) {
                hasPerm = true;
            }
            if (hasPerm) {
                final Vector max = new Vector(claim.getGreaterBoundaryCorner().getBlockX(), 256, claim.getGreaterBoundaryCorner().getBlockZ());
                final Vector min = new Vector(claim.getLesserBoundaryCorner().getBlockX(), 0, claim.getLesserBoundaryCorner().getBlockZ());
                final CuboidRegion cuboid = new CuboidRegion(min, max);

                return new CuboidRegionWrapper(cuboid, "CLAIM:" + player.getName() + ":" + claim.getID());
            }
        }
        return null;

    }

    @Override
    public boolean hasPermission(final Player player) {
        return MainUtil.hasPermission(player, "wrg.griefprevention");
    }
}
