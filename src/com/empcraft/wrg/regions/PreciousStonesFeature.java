package com.empcraft.wrg.regions;

import java.util.List;

import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;

import org.bukkit.Bukkit;
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
import com.sk89q.worldedit.regions.CuboidRegion;




public class PreciousStonesFeature extends AbstractRegion   {
	Plugin preciousstones;
	WorldeditRegions plugin;
	public PreciousStonesFeature(Plugin preciousstonesplugin,WorldeditRegions worldeditregions) {
		preciousstones = preciousstonesplugin;
    	plugin = worldeditregions;
    	
    }

	@Override
	public CuboidRegionWrapper getcuboid(Player player) {
		List<Field> fields = PreciousStones.API().getFieldsProtectingArea(FieldFlag.PLOT, player.getLocation());
		for (Field myfield:fields) {
			boolean hasPerm = false;
			if (myfield.getOwner().equalsIgnoreCase(player.getName())) {
				hasPerm = true;
			}
			else if (MainUtil.hasPermission(player, "wrg.preciousstones.member")) {
				if (myfield.isAllowed(player.getName())) {
					hasPerm = true;
				}
			}
			if (hasPerm) {
				Vector min = new Vector(myfield.getCorners().get(0).getBlockX(),myfield.getCorners().get(0).getBlockY(),myfield.getCorners().get(0).getBlockZ());
				Vector max = new Vector(myfield.getCorners().get(1).getBlockX(),myfield.getCorners().get(1).getBlockY(),myfield.getCorners().get(1).getBlockZ());
				CuboidRegion cuboid = new CuboidRegion(min, max);
				return new CuboidRegionWrapper(cuboid, "FIELD:"+myfield.toString());
			}
		}
		return null;
		
		
	}
	
	@Override
    public boolean hasPermission(Player player) {
        return MainUtil.hasPermission(player, "wrg.preciousstones");
    }
}

