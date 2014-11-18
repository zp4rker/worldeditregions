package com.empcraft.wrg;

import java.util.ArrayList;
import net.jzx7.regiosapi.RegiosAPI;
import net.jzx7.regiosapi.location.RegiosPoint;
import net.jzx7.regiosapi.regions.CuboidRegion;
import net.jzx7.regiosapi.regions.Region;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;

public class RegiosFeature implements Listener {
	RegiosAPI regios;
	WorldeditRegions plugin;
	public RegiosFeature(Plugin regiosPlugin,WorldeditRegions worldeditregions) {
		regios = (RegiosAPI) regiosPlugin;
    	plugin = worldeditregions;
    	
    }
	public boolean rgsCommand(CommandSender sender, Command cmd, String label, String[] args){
		return false;
	}
	public com.sk89q.worldedit.regions.CuboidRegion getcuboid(Player player) {
		ArrayList<Region> regions = regios.getRegions(player.getLocation());
		for (Region region:regions) {
			boolean toReturn = false;
			if (region.getOwner().equals(player.getName())) {
				toReturn = true;
			}
			else if (region.getName().equals(player.getName())) {
				toReturn = true;
			}
			else if (plugin.checkperm(player, "wrg.regios.member")) {
				ArrayList<String> players = region.getPlayersInRegion();
				for (String user:players) {
					if (user.equals(player.getName())) {
						toReturn = true;
						break;
					}
				}
			}
			if (toReturn) {
				if (region instanceof CuboidRegion) {
					CuboidRegion cRegion = (CuboidRegion) region;
					RegiosPoint pos1 = cRegion.getL1();
					RegiosPoint pos2 = cRegion.getL2();
					Vector min = new Vector(pos1.getX(),pos1.getY(),pos1.getZ());
					Vector max = new Vector(pos2.getX(),pos2.getY(),pos2.getZ());
					return new com.sk89q.worldedit.regions.CuboidRegion(min, max);
				}
			}
		}
		return null;
	}
	public String getid(Player player) {
		ArrayList<Region> regions = regios.getRegions(player.getLocation());
		for (Region region:regions) {
			if (region.getOwner().equals(player.getName())) {
				return "REGIOS:"+region.getName();
			}
			else if (region.getName().equals(player.getName())) {
				return "REGIOS:"+region.getName();
			}
			else if (plugin.checkperm(player, "wrg.regios.member")) {
				ArrayList<String> players = region.getPlayersInRegion();
				for (String user:players) {
					if (user.equals(player.getName())) {
						return "REGIOS:"+region.getName();
					}
				}
			}
		}
		return null;
	}
}

