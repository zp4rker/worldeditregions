package com.empcraft.wrg.regions;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.MainUtil;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TownyFeature extends AbstractRegion {
    Plugin towny;
    WorldeditRegions plugin;

    public TownyFeature(final Plugin townyplugin, final WorldeditRegions worldeditregions) {
        this.towny = townyplugin;
        this.plugin = worldeditregions;

    }

    @Override
    public CuboidRegionWrapper getCuboid(final Player player) {
        try {
            final PlayerCache cache = ((Towny) this.towny).getCache(player);
            final WorldCoord mycoord = cache.getLastTownBlock();
            if (mycoord == null) {
                return null;
            } else {
                final TownBlock myplot = mycoord.getTownBlock();
                if (myplot == null) {
                    return null;
                } else {
                    try {
                        if (myplot.getResident().getName().equals(player.getName())) {
                            final Chunk chunk = player.getLocation().getChunk();
                            final BlockVector3 min = BlockVector3.at(chunk.getX() * 16, 0, chunk.getZ() * 16);
                            final BlockVector3 max = BlockVector3.at((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16) + 15);
                            final CuboidRegion cuboid = new CuboidRegion(min, max);
                            return new CuboidRegionWrapper(cuboid, "PLOT:" + chunk.getX() + "," + chunk.getZ());
                        }
                    } catch (final Exception e) {

                    }
                    if (MainUtil.hasPermission(player, "wrg.towny.member")) {
                        if (myplot.getTown().hasResident(player.getName())) {
                            final Chunk chunk = player.getLocation().getChunk();
                            final BlockVector3 min = BlockVector3.at(chunk.getX() * 16, 0, chunk.getZ() * 16);
                            final BlockVector3 max = BlockVector3.at((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16) + 15);
                            final CuboidRegion cuboid = new CuboidRegion(min, max);
                            return new CuboidRegionWrapper(cuboid, "PLOT:" + chunk.getX() + "," + chunk.getZ());
                        }
                    } else if (myplot.getTown().isMayor(TownyUniverse.getDataSource().getResident(player.getName()))) {
                        final Chunk chunk = player.getLocation().getChunk();
                        final BlockVector3 min = BlockVector3.at(chunk.getX() * 16, 0, chunk.getZ() * 16);
                        final BlockVector3 max = BlockVector3.at((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16) + 15);
                        final CuboidRegion cuboid = new CuboidRegion(min, max);
                        return new CuboidRegionWrapper(cuboid, "PLOT:" + chunk.getX() + "," + chunk.getZ());
                    }
                }
            }
        } catch (final Exception e) {
        }
        return null;

    }

    @Override
    public boolean hasPermission(final Player player) {
        return MainUtil.hasPermission(player, "wrg.towny");
    }
}
