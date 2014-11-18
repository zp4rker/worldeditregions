package com.empcraft.wrg;

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









import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class PreciousStonesFeature implements Listener {
	Plugin preciousstones;
	WorldeditRegions plugin;
	public PreciousStonesFeature(Plugin preciousstonesplugin,WorldeditRegions worldeditregions) {
		preciousstones = preciousstonesplugin;
    	plugin = worldeditregions;
    	
    }
public boolean psfCommand(CommandSender sender, Command cmd, String label, String[] args){
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
		List<Field> fields = PreciousStones.API().getFieldsProtectingArea(FieldFlag.PLOT, player.getLocation());
		for (Field myfield:fields) {
			boolean hasPerm = false;
			if (myfield.getOwner().equalsIgnoreCase(player.getName())) {
				hasPerm = true;
			}
			else if (plugin.checkperm(player, "wrg.preciousstones.member")) {
				if (myfield.isAllowed(player.getName())) {
					hasPerm = true;
				}
			}
			if (hasPerm) {
				Vector min = new Vector(myfield.getCorners().get(0).getBlockX(),myfield.getCorners().get(0).getBlockY(),myfield.getCorners().get(0).getBlockZ());
				Vector max = new Vector(myfield.getCorners().get(1).getBlockX(),myfield.getCorners().get(1).getBlockY(),myfield.getCorners().get(1).getBlockZ());
				CuboidRegion cuboid = new CuboidRegion(min, max);
				return cuboid;
			}
		}
		return null;
		
		
	}
	public String getid(Player player) {
		List<Field> fields = PreciousStones.API().getFieldsProtectingArea(FieldFlag.PLOT, player.getLocation());
		for (Field myfield:fields) {
			if (myfield.getOwner().equalsIgnoreCase(player.getName())) {
				return "FIELD:"+myfield.toString();
			}
		}
		return null;
	}
}

