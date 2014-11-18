package com.empcraft.wrg;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.bekvon.bukkit.residence.Residence;







import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class ResidenceFeature implements Listener {
	Plugin residence;
	WorldeditRegions plugin;
	public ResidenceFeature(Plugin residenceplugin,WorldeditRegions worldeditregions) {
		residence = residenceplugin;
    	plugin = worldeditregions;
    	
    }
	public boolean psfCommand(CommandSender sender, Command cmd, String label, String[] args){
		return false;
	}
	public CuboidRegion getcuboid(Player player) {
		ClaimedResidence residence = Residence.getResidenceManager().getByLoc(player.getLocation());
		if (residence!=null) {
			boolean hasPerm = false;
			if (residence.getOwner().equalsIgnoreCase(player.getName())||residence.getName().equalsIgnoreCase(player.getName())) {
				hasPerm = true;
			}
			if (plugin.checkperm(player, "wrg.residence.member")&&residence.getPlayersInResidence().contains(player)) {
				hasPerm = true;
			}
			if (hasPerm) {
				CuboidArea area = residence.getAreaArray()[0];
				Location pos1 = area.getHighLoc();
				Location pos2 = area.getLowLoc();
				Vector min = new Vector(pos2.getBlockX(),pos2.getBlockY(),pos2.getBlockZ());
				Vector max = new Vector(pos1.getBlockX(),pos1.getBlockY(),pos1.getBlockZ());
				CuboidRegion cuboid = new CuboidRegion(min, max);
				return cuboid;
			}
		}
		return null;
		
		
	}
	public String getid(Player player) {
		ClaimedResidence residence = Residence.getResidenceManager().getByLoc(player.getLocation());
		if (residence!=null) {
			return "RESIDENCE: " + residence.getName();
		}
		return null;
	}
}

