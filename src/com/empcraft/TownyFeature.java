package com.empcraft;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.db.TownyDatabaseHandler;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.tasks.TownClaim;
import com.palmergames.bukkit.util.BukkitTools;


import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;




public class TownyFeature implements Listener {
	Plugin towny;
	WorldeditRegions plugin;
	public TownyFeature(Plugin townyplugin,WorldeditRegions worldeditregions) {
		towny = townyplugin;
    	plugin = worldeditregions;
    	
    }
public boolean tfCommand(CommandSender sender, Command cmd, String label, String[] args){
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
					return cuboid;
				}
				}
				catch (Exception e) {
					
				}
				if (plugin.checkperm(player, "wrg.towny.*")) {
					if (myplot.getTown().hasResident(player.getName())) {
						Chunk chunk = player.getLocation().getChunk();
						Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
						Vector max = new Vector((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16)+15);
						CuboidRegion cuboid = new CuboidRegion(min, max);
						return cuboid;
					}
				}
				else if (myplot.getTown().isMayor(TownyUniverse.getDataSource().getResident(player.getName()))) {
					Chunk chunk = player.getLocation().getChunk();
					Vector min = new Vector(chunk.getX() * 16, 0, chunk.getZ() * 16);
					Vector max = new Vector((chunk.getX() * 16) + 15, 256, (chunk.getZ() * 16)+15);
					CuboidRegion cuboid = new CuboidRegion(min, max);
					return cuboid;
				}
			}
		}
		}
		catch (Exception e) {
		}
		return null;
		
		
	}
	public String getid(Player player) {
		return "PLOT:"+player.getLocation().getChunk().getX()+","+player.getLocation().getChunk().getZ();
	}
}

