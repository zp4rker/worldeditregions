package com.empcraft.wrg.regions;

import org.bukkit.Bukkit;
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
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;




import com.massivecraft.mcore.ps.PS;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class FactionsFeature extends AbstractRegion   {
	Plugin factions;
	WorldeditRegions plugin;
	public FactionsFeature(Plugin factionsplugin,WorldeditRegions worldeditregions) {
		factions = factionsplugin;
    	plugin = worldeditregions;
    	
    }
	
	@Override
	public CuboidRegionWrapper getcuboid(Player player) {
		Faction fac = BoardColls.get().getFactionAt(PS.valueOf(player.getLocation()));
		// check if they are the owner
		if (fac!=null) {
			if (fac.getOnlinePlayers().contains(player)) {
				if (fac.getComparisonName().equals("wilderness")==false) {
					Chunk chunk = player.getLocation().getChunk();
					Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
					Vector max = new Vector((chunk.getX() * 16) + 15, 156, (chunk.getZ() * 16)+15);
					CuboidRegion cuboid = new CuboidRegion(min, max);
					return new CuboidRegionWrapper(cuboid, "CHUNK:"+chunk.getX()+","+chunk.getZ());
				}
			}
			else {
			}
			return null;
		}
		else {
			return null;
		}
		
		
	}

    @Override
    public boolean hasPermission(Player player) {
        return MainUtil.hasPermission(player, "wrg.factions");
    }
}

