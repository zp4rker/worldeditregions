package com.empcraft.wrg;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;





import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;




public class GriefPreventionFeature implements Listener {
	Plugin griefprevention;
	WorldeditRegions plugin;
	public GriefPreventionFeature(Plugin griefpreventionplugin,WorldeditRegions worldeditregions) {
		griefprevention = griefpreventionplugin;
    	plugin = worldeditregions;
    	
    }
public boolean gpfCommand(CommandSender sender, Command cmd, String label, String[] args){
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
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
		if (claim!=null) {
			boolean hasPerm = false;
			if (claim.getOwnerName().equalsIgnoreCase(player.getName())) {
				hasPerm = true;
			}
			else if (claim.isManager(player.getName())&&plugin.checkperm(player, "wrg.griefprevention.member")) {
				hasPerm = true;
			}
			if (hasPerm) {
				Vector max = new Vector(claim.getGreaterBoundaryCorner().getBlockX(), 256, claim.getGreaterBoundaryCorner().getBlockZ());
				Vector min = new Vector(claim.getLesserBoundaryCorner().getBlockX(), 0, claim.getLesserBoundaryCorner().getBlockZ());
				CuboidRegion cuboid = new CuboidRegion(min, max);
				return cuboid;
			}
		}
		return null;
		
		
	}
	public String getid(Player player) {
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
		if (claim==null) {
			return null;
		}
		return "CLAIM:"+player.getName()+":"+claim.getID();
	}
}

