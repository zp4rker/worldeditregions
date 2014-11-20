package com.empcraft.wrg.regions;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.bekvon.bukkit.residence.Residence;







import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class ResidenceFeature extends AbstractRegion   {
	Plugin residence;
	WorldeditRegions plugin;
	public ResidenceFeature(Plugin residenceplugin,WorldeditRegions worldeditregions) {
		residence = residenceplugin;
    	plugin = worldeditregions;
    	
    }
	
	@Override
	public CuboidRegionWrapper getcuboid(Player player) {
		ClaimedResidence residence = Residence.getResidenceManager().getByLoc(player.getLocation());
		if (residence!=null) {
			boolean hasPerm = false;
			if (residence.getOwner().equalsIgnoreCase(player.getName())||residence.getName().equalsIgnoreCase(player.getName())) {
				hasPerm = true;
			}
			if (MainUtil.hasPermission(player, "wrg.residence.member")&&residence.getPlayersInResidence().contains(player)) {
				hasPerm = true;
			}
			if (hasPerm) {
				CuboidArea area = residence.getAreaArray()[0];
				Location pos1 = area.getHighLoc();
				Location pos2 = area.getLowLoc();
				Vector min = new Vector(pos2.getBlockX(),pos2.getBlockY(),pos2.getBlockZ());
				Vector max = new Vector(pos1.getBlockX(),pos1.getBlockY(),pos1.getBlockZ());
				CuboidRegion cuboid = new CuboidRegion(min, max);
				return new CuboidRegionWrapper(cuboid, "RESIDENCE: " + residence.getName());
			}
		}
		return null;
	}
	
	@Override
    public boolean hasPermission(Player player) {
        return MainUtil.hasPermission(player, "wrg.residence");
    }
}

