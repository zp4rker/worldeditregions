package com.empcraft.wrg.regions;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GriefPreventionFeature extends AbstractRegion {
    Plugin griefprevention;
    WorldeditRegions plugin;

    public GriefPreventionFeature(final Plugin griefpreventionplugin, final WorldeditRegions worldeditregions) {
        this.griefprevention = griefpreventionplugin;
        this.plugin = worldeditregions;

    }

    @Override
    public CuboidRegionWrapper getCuboid(final Player player) {
        final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
        if (claim != null) {
            boolean hasPerm = false;
            String uuid = player.getUniqueId().toString();
            if (claim.getOwnerName().equalsIgnoreCase(player.getName()) || claim.getOwnerName().equals(uuid)) {
                hasPerm = true;
            } else if ((claim.managers.contains(player.getName()) || claim.managers.contains(uuid)) && MainUtil.hasPermission(player, "wrg.griefprevention.member")) {
                hasPerm = true;
            }
            if (hasPerm) {
                final BlockVector3 max = BlockVector3.at(claim.getGreaterBoundaryCorner().getBlockX(), 256, claim.getGreaterBoundaryCorner().getBlockZ());
                final BlockVector3 min = BlockVector3.at(claim.getLesserBoundaryCorner().getBlockX(), 0, claim.getLesserBoundaryCorner().getBlockZ());
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
