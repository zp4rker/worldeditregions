package com.empcraft.wrg.regions;

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

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.Vector;

public class RegiosFeature extends AbstractRegion   {
	RegiosAPI regios;
	WorldeditRegions plugin;
	public RegiosFeature(Plugin regiosPlugin,WorldeditRegions worldeditregions) {
		regios = (RegiosAPI) regiosPlugin;
    	plugin = worldeditregions;
    	
    }

	public CuboidRegionWrapper getcuboid(Player player) {
		ArrayList<Region> regions = regios.getRegions(player.getLocation());
		for (Region region:regions) {
			boolean toReturn = false;
			if (region.getOwner().equals(player.getName())) {
				toReturn = true;
			}
			else if (region.getName().equals(player.getName())) {
				toReturn = true;
			}
			else if (MainUtil.hasPermission(player, "wrg.regios.member")) {
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
					com.sk89q.worldedit.regions.CuboidRegion cuboid = new com.sk89q.worldedit.regions.CuboidRegion(min, max);
					return new CuboidRegionWrapper(cuboid, "REGIOS:"+region.getName());
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
			else if (MainUtil.hasPermission(player, "wrg.regios.member")) {
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
	
	@Override
    public boolean hasPermission(Player player) {
        return MainUtil.hasPermission(player, "wrg.regios");
    }
}

