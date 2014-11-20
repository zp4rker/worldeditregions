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
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class OldFactionsFeature extends AbstractRegion   {
	WorldeditRegions plugin;
	public OldFactionsFeature(Plugin factionsplugin,WorldeditRegions worldeditregions) {
    	plugin = worldeditregions;
    	
    }

	@Override
	public CuboidRegionWrapper getcuboid(Player player) {
	    FLocation loc = new FLocation(player.getLocation());
        Faction fac = Board.getFactionAt(loc);
        if(!fac.isNone()){
            if (!fac.isSafeZone()) {
                if (fac.getOnlinePlayers().contains(player)) {
                    Chunk chunk = player.getLocation().getChunk();
                    Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
                    Vector max = new Vector((chunk.getX() * 16) + 15, 156, (chunk.getZ() * 16)+15);
                    CuboidRegion cuboid = new CuboidRegion(min, max);
                    return new CuboidRegionWrapper(cuboid, "CHUNK:"+chunk.getX()+","+chunk.getZ());
                }
            }
        }
		return null;
	}
	
	@Override
    public boolean hasPermission(Player player) {
        return MainUtil.hasPermission(player, "wrg.factions");
    }
}

