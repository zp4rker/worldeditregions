package com.empcraft.wrg.regions;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.object.LocPair;
import com.empcraft.wrg.util.MainUtil;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

public class OldFactionsFeature extends AbstractRegion {
    WorldeditRegions plugin;

    public OldFactionsFeature(final Plugin factionsplugin, final WorldeditRegions worldeditregions) {
        this.plugin = worldeditregions;

    }

    @Override
    public CuboidRegionWrapper getcuboid(final Player player) {
        final Chunk chunk = player.getLocation().getChunk();
        final boolean perm = MainUtil.hasPermission(player, "wrg.factions.wilderness");
        final LocPair locs = new LocPair(chunk.getX(), chunk.getZ(), chunk.getX(), chunk.getZ());
        final World world = player.getWorld();

        int count = WorldeditRegions.config.getInt("factions.max-chunk-traversal");

        if (isAdded(locs, world, player, perm)) {
            boolean hasPerm = true;

            LocPair chunkSelection;
            while (hasPerm && (count > 0)) {
                count--;

                hasPerm = false;

                chunkSelection = new LocPair(locs.xmax + 1, locs.ymin, locs.xmax + 1, locs.ymax);

                if (isAdded(chunkSelection, world, player, perm)) {
                    locs.add(0, 0, 1, 0);
                    hasPerm = true;
                }

                chunkSelection = new LocPair(locs.xmin - 1, locs.ymin, locs.xmin - 1, locs.ymax);

                if (isAdded(chunkSelection, world, player, perm)) {
                    locs.add(-1, 0, 0, 0);
                    hasPerm = true;
                }

                chunkSelection = new LocPair(locs.xmin, locs.ymax + 1, locs.xmax, locs.ymax + 1);

                if (isAdded(chunkSelection, world, player, perm)) {
                    locs.add(0, 0, 0, 1);
                    hasPerm = true;
                }

                chunkSelection = new LocPair(locs.xmin, locs.ymin - 1, locs.xmax, locs.ymin - 1);

                if (isAdded(chunkSelection, world, player, perm)) {
                    locs.add(0, -1, 0, 0);
                    hasPerm = true;
                }
            }
            final Vector min = new Vector(locs.xmin * 16, 0, locs.ymin * 16);
            final Vector max = new Vector((locs.xmax * 16) + 15, 156, (locs.ymax * 16) + 15);
            final CuboidRegion cuboid = new CuboidRegion(min, max);
            return new CuboidRegionWrapper(cuboid, "CHUNK:" + chunk.getX() + "," + chunk.getZ());
        }
        return null;
    }

    public boolean isAdded(final LocPair locs, final World world, final Player player, final boolean perm) {
        for (int x = locs.xmin; x <= locs.xmax; x++) {
            for (int y = locs.ymin; y <= locs.ymax; y++) {

                final FLocation loc = new FLocation(new Location(world, x << 4, 0, y << 4));
                final Faction fac = Board.getFactionAt(loc);

                if (((fac == null) || fac.isNone() || fac.isSafeZone()) && (!fac.getId().equals("0"))) {
                    return false;
                }
                if (!fac.getOnlinePlayers().contains(player)) {
                    return false;
                }
                if (fac.getId().equals("0") && !perm) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean hasPermission(final Player player) {
        return MainUtil.hasPermission(player, "wrg.factions");
    }
}
