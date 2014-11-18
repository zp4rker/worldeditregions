package com.empcraft.wrg;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;









import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;




import com.massivecraft.mcore.ps.PS;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class FactionsFeature implements Listener {
	Plugin factions;
	WorldeditRegions plugin;
	public FactionsFeature(Plugin factionsplugin,WorldeditRegions worldeditregions) {
		factions = factionsplugin;
    	plugin = worldeditregions;
    	
    }
public boolean ffCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if (cmd.getName().equalsIgnoreCase("wrg")) {
    		Player player;
    		if (sender instanceof Player==false) {
    			player = null;
    			plugin.msg(player,plugin.getmsg("MSG0"));
    			return false;
    		}
    		else {
    			player = (Player) sender;
    		}
    		if (args.length>0) {
    			if (args[0].equalsIgnoreCase("help")) {
    				plugin.msg(player,plugin.getmsg("MSG7"));
    				return true;
    			}
    		}
    		Bukkit.dispatchCommand(player,"wrg help");
    	}
		return false;
	}
	public CuboidRegion getcuboid(Player player) {
		Faction fac = BoardColls.get().getFactionAt(PS.valueOf(player.getLocation()));
		// check if they are the owner
		if (fac!=null) {
			if (fac.getOnlinePlayers().contains(player)) {
				if (fac.getComparisonName().equals("wilderness")==false) {
					Chunk chunk = player.getLocation().getChunk();
					Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
					Vector max = new Vector((chunk.getX() * 16) + 15, 156, (chunk.getZ() * 16)+15);
					CuboidRegion cuboid = new CuboidRegion(min, max);
					return cuboid;
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
	public String getid(Player player) {
		return "CHUNK:"+player.getLocation().getChunk().getX()+","+player.getLocation().getChunk().getZ();
	}
}

