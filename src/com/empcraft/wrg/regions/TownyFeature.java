package com.empcraft.wrg.regions;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.MainUtil;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class TownyFeature extends AbstractRegion   {
	Plugin towny;
	WorldeditRegions plugin;
	public TownyFeature(Plugin townyplugin,WorldeditRegions worldeditregions) {
		towny = townyplugin;
    	plugin = worldeditregions;
    	
    }
	
	@Override
	public CuboidRegionWrapper getcuboid(Player player) {
		try {
		PlayerCache cache = ((Towny) towny).getCache(player);
		WorldCoord mycoord = cache.getLastTownBlock();
		if (mycoord==null) {
			return null;
		}
		else {
			TownBlock myplot = mycoord.getTownBlock();
			if (myplot==null) {
				return null;
			}
			else {
				try {
				if (myplot.getResident().getName().equals(player.getName())) {
					Chunk chunk = player.getLocation().getChunk();
					Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
					Vector max = new Vector((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16)+15);
					CuboidRegion cuboid = new CuboidRegion(min, max);
					return new CuboidRegionWrapper(cuboid, "PLOT:"+chunk.getX()+","+chunk.getZ());
				}
				}
				catch (Exception e) {
					
				}
				if (MainUtil.hasPermission(player, "wrg.towny.member")) {
					if (myplot.getTown().hasResident(player.getName())) {
						Chunk chunk = player.getLocation().getChunk();
						Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
						Vector max = new Vector((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16)+15);
						CuboidRegion cuboid = new CuboidRegion(min, max);
						return new CuboidRegionWrapper(cuboid, "PLOT:"+chunk.getX()+","+chunk.getZ());
					}
				}
				else if (myplot.getTown().isMayor(TownyUniverse.getDataSource().getResident(player.getName()))) {
					Chunk chunk = player.getLocation().getChunk();
					Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
					Vector max = new Vector((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16)+15);
					CuboidRegion cuboid = new CuboidRegion(min, max);
					return new CuboidRegionWrapper(cuboid, "PLOT:"+chunk.getX()+","+chunk.getZ());
				}
			}
		}
		}
		catch (Exception e) {
		}
		return null;
		
		
	}
	
	@Override
    public boolean hasPermission(Player player) {
        return MainUtil.hasPermission(player, "wrg.towny");
    }
}

