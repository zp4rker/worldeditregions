package com.empcraft.wrg;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;



public class WorldguardFeature implements Listener {
	WorldGuardPlugin worldguard;
	WorldeditRegions plugin;
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	public WorldguardFeature(Plugin p2,WorldeditRegions p3) {
    	worldguard = getWorldGuard();
    	plugin = p3;
    	
    }
	public ProtectedRegion isowner(Player player) {
		com.sk89q.worldguard.LocalPlayer localplayer = worldguard.wrapPlayer(player);
		RegionManager manager = worldguard.getRegionManager(player.getWorld());
		ProtectedRegion myregion = manager.getRegion("__global__");
		if (myregion.isOwner(localplayer) || (myregion.isMember(localplayer) && plugin.checkperm(player, "wrg.worldguard.member"))) {
		    return myregion;
		}
		ApplicableRegionSet regions = manager.getApplicableRegions(player.getLocation());
		for (ProtectedRegion region:regions) {
			if (region.isOwner(localplayer)) {
				return region;
			}
			if (region.isMember(localplayer)) {
				if (plugin.checkperm(player, "wrg.worldguard.member"))
				return region;
			}
			else if (region.getId().toLowerCase().equals(player.getName().toLowerCase())) {
                return region;
            }
            else if (region.getId().toLowerCase().contains(player.getName().toLowerCase()+"//")) {
                return region;
            }
			else if (region.isOwner("*")) {
				return region;
			}
			if (plugin.vf!=null) {
				String[] groups = plugin.vf.getGroup(player);
				boolean hasPerm = false;
				if (plugin.checkperm(player,"wrg.worldguard.member")) {
					hasPerm = true;
				}
				for (String group:groups) {
					String regionGroups = region.getOwners().toGroupsString();
					if (regionGroups.contains("*"+group)) {
						return region;
					}
					else if (hasPerm) {
						String regionGroupMembers = region.getMembers().toGroupsString();
						if (regionGroupMembers.contains("*"+group)) {
							return region;
						}
					}
				}
			}
		}
		return null;
	}
	public ProtectedRegion getregion(Player player,BlockVector location) {
		com.sk89q.worldguard.LocalPlayer localplayer = worldguard.wrapPlayer(player);
		ApplicableRegionSet regions = worldguard.getRegionManager(player.getWorld()).getApplicableRegions(location);
		for (ProtectedRegion region:regions) {
			if (region.isOwner(localplayer)) {
				return region;
			}
			else if (region.getId().toLowerCase().equals(player.getName().toLowerCase())) {
				return region;
			}
			else if (region.getId().toLowerCase().contains(player.getName().toLowerCase()+"//")) {
				return region;
			}
			else if (region.isOwner("*")) {
				return region;
			}
		}
		return null;
	}
	public CuboidRegion getcuboid(Player player) {
		ProtectedRegion myregion = isowner(player);
		if (myregion!=null) {
			CuboidRegion cuboid = new CuboidRegion(myregion.getMinimumPoint(), myregion.getMaximumPoint());
			return cuboid;
		}
		else {
			return null;
		}
		
		
	}
	public String getid(Player player) {
		
		ProtectedRegion myregion = isowner(player);
		if (myregion!=null) {
			return myregion.getId();
		}
		else {
			return null;
		}
	}
	public boolean wgfcommand(CommandSender sender, Command cmd, String label, String[] args){
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
    			if (args[0].equalsIgnoreCase("trust")) {
    				if (plugin.checkperm(player,"worldguard.region.addmember.own.*")) {
	    				if (args.length>1) {
	    					if (plugin.lastmask.get(player.getName()).equals("~NULL")) {
	    						plugin.msg(player,plugin.getmsg("MSG1"));
	    					}
	    					else {
	    						DefaultDomain domain = worldguard.getRegionManager(player.getWorld()).getRegion(plugin.id.get(player.getName())).getMembers();
	    				        domain.addPlayer(args[1]);
	    				        worldguard.getRegionManager(player.getWorld()).getRegion(plugin.id.get(player.getName())).setMembers(domain);
	    				        plugin.msg(player,plugin.getmsg("MSG2")+" &a"+args[1]+"&7.");
	    				        try {
	    				        	worldguard.getRegionManager(player.getWorld()).save();
    				        	} catch (Exception e) {
	    				        		e.printStackTrace();
    				        	}
	    					}
	    				}
	    				else {
	    					plugin.msg(player,plugin.getmsg("MSG3"));
	    				}
    				}
    				else {
    					plugin.msg(player,plugin.getmsg("MSG4")+" &cworldguard.region.addmember.own.*");
    				}
    				return true;
    			}
    			if (args[0].equalsIgnoreCase("share")) {
    				if (plugin.checkperm(player,"worldguard.region.addowner.own.*")) {
	    				if (args.length>1) {
	    					if (plugin.lastmask.get(player.getName()).equals("~NULL")) {
	    						plugin.msg(player,plugin.getmsg("MSG1"));
	    					}
	    					else {
	    						DefaultDomain domain = worldguard.getRegionManager(player.getWorld()).getRegion(plugin.id.get(player.getName())).getOwners();
	    				        domain.addPlayer(args[1]);
	    				        worldguard.getRegionManager(player.getWorld()).getRegion(plugin.id.get(player.getName())).setOwners(domain);
	    				        plugin.msg(player,plugin.getmsg("MSG2")+" &a"+args[1]+"&7.");
	    				        try {
	    				        	worldguard.getRegionManager(player.getWorld()).save();
    				        	} catch (Exception e) {
	    				        		e.printStackTrace();
    				        	}
	    					}
	    				}
	    				else {
	    					plugin.msg(player,plugin.getmsg("MSG11"));
	    				}
    				}
    				else {
    					plugin.msg(player,plugin.getmsg("MSG4")+" &cworldguard.region.addowner.own.*");
    				}
    				return true;
    			}
    			else if (args[0].equalsIgnoreCase("untrust")) {
    				if (plugin.checkperm(player,"worldguard.region.removemember.own.*")) {
	    				if (args.length>1) {
	    					if (plugin.lastmask.get(player.getName()).equals("~NULL")) {
	    						plugin.msg(player,plugin.getmsg("MSG1"));
	    					}
	    					else {
	    						DefaultDomain domain = worldguard.getRegionManager(player.getWorld()).getRegion(plugin.id.get(player.getName())).getMembers();
	    				        domain.removePlayer(args[1]);
	    				        worldguard.getRegionManager(player.getWorld()).getRegion(plugin.id.get(player.getName())).setMembers(domain);
	    				        plugin.msg(player,plugin.getmsg("MSG12")+" &c"+args[1]+"&7.");
	    				        try {
	    				        	worldguard.getRegionManager(player.getWorld()).save();
    				        	} catch (Exception e) {
	    				        		e.printStackTrace();
    				        	}
	    					}
	    				}
	    				else {
	    					plugin.msg(player,plugin.getmsg("MSG13"));
	    				}
    				}
    				else {
    					plugin.msg(player,plugin.getmsg("MSG4")+" &cworldguard.region.removemember.own.*");
    				}
    				return true;
    			}
    			else if (args[0].equalsIgnoreCase("info")||args[0].equalsIgnoreCase("i")) {
					if (plugin.id.get(player.getName()).equals("~NULL")) {
						plugin.msg(player,plugin.getmsg("MSG1"));
					}
					else {
						Bukkit.dispatchCommand(player, "region info "+plugin.id.get(player.getName()));
					}
					return true;
    			}
    			else if (args[0].equalsIgnoreCase("create")) {
    				if (plugin.checkperm(player,"worldguard.region.define")) {
	    				if (args.length==2) {
	    				    boolean op = player.isOp();
	    				    player.setOp(true);
	    				    try {
	    					ProtectedRegion myregion = worldguard.getRegionManager(player.getWorld()).getRegion(args[1]);
	    					if (myregion==null) {
	    						if (plugin.getConfig().getBoolean("create.expand-vert")) {
	    							Bukkit.dispatchCommand(player, "/expand vert");
	    						}
	    						if (plugin.getConfig().getBoolean("create.add-owner")) {
	    							Bukkit.dispatchCommand(player, "region define "+args[1]+" "+args[1]);
	    						}
	    						else {
	    							Bukkit.dispatchCommand(player, "region define "+args[1]);
	    						}
	    						return true;
	    					}
	    					else {
	    						if (plugin.getConfig().getBoolean("create.expand-vert")) {
	    							Bukkit.dispatchCommand(player, "/expand vert");
	    						}
	    						int max = (plugin.getConfig().getInt("max-region-count-per-player"));
	    						for (int i=0;i<max-1;i++) {
	    							myregion = worldguard.getRegionManager(player.getWorld()).getRegion(args[1]+"//"+i);
	    							if (myregion==null) {
	    								if (plugin.getConfig().getBoolean("create.expand-vert")) {
	    	    							Bukkit.dispatchCommand(player, "/expand vert");
	    	    						}
	    	    						if (plugin.getConfig().getBoolean("create.add-owner")) {
	    	    							Bukkit.dispatchCommand(player, "region define "+args[1]+"//"+i+" "+args[1]);
	    	    						}
	    	    						else {
	    	    							Bukkit.dispatchCommand(player, "region define "+args[1]+"//"+i);
	    	    						}
	    	    						return true;
	    							}
	    								
	    						}
	    						plugin.msg(player,"&c"+args[1]+"&7 "+plugin.getmsg("MSG14"));
	    					}
	    				    }
	    				    catch (Exception e) {
	    				        
	    				    }
	    				    finally {
	    				        player.setOp(op);
	    				    }
	    				}
	    				else {
	    					plugin.msg(player,plugin.getmsg("MSG8"));
	    				}
    				}
    				else {
    					if (plugin.checkperm(player, "worldguard.region.define.own")) {
    						Selection selection = plugin.worldedit.getSelection(player);
    						if (selection!=null) {
    							plugin.worldedit.getSession(player);
    							worldguard.wrapPlayer(player);
    							BlockVector pos1 = selection.getNativeMinimumPoint().toBlockVector();
    						    BlockVector pos2 = selection.getNativeMaximumPoint().toBlockVector();
    						    worldguard.getRegionManager(player.getWorld()).removeRegion("//");
    						    ProtectedRegion selected = new ProtectedCuboidRegion("//", pos1, pos2);
    						    ApplicableRegionSet myregions = worldguard.getRegionManager(player.getWorld()).getApplicableRegions(selected);
    						    boolean preprotected = false;
    						    for (@SuppressWarnings("unused") ProtectedRegion current:myregions) {
    						    	preprotected = true;
    						    }
    						    if (preprotected) {
    						    	plugin.msg(player,plugin.getmsg("MSG16"));
    						    }
    						    else {
    						    	double area = (pos1.getX()-pos2.getX())*(pos1.getZ()-pos2.getZ());
    						    	if (area>plugin.getConfig().getDouble("max-claim-area")) {
    						    		plugin.msg(player,plugin.getmsg("MSG18")+"&7 - "+area + " &c>&7 "+plugin.getConfig().getDouble("max-claim-area"));
    						    	}
    						    	else {
    						    		try {
			    							player.setOp(true);
	    									ProtectedRegion myregion = worldguard.getRegionManager(player.getWorld()).getRegion(player.getName());
	    			    					if (myregion==null) {
	    			    						if (plugin.getConfig().getBoolean("create.expand-vert")) {
	    			    							Bukkit.dispatchCommand(player, "/expand vert");
	    			    						}
	    			    						if (plugin.getConfig().getBoolean("create.add-owner")) {
	    			    							Bukkit.dispatchCommand(player, "region define "+player.getName()+" "+player.getName());
	    			    						}
	    			    						else {
	    			    							Bukkit.dispatchCommand(player, "region define "+player.getName());
	    			    						}
	    			    						return true;
	    			    					}
	    			    					else {
	    			    						if (plugin.getConfig().getBoolean("create.expand-vert")) {
	    			    							Bukkit.dispatchCommand(player, "/expand vert");
	    			    						}
	    			    						int max = (plugin.getConfig().getInt("max-region-count-per-player"));
	    			    						for (int i=0;i<max-1;i++) {
	    			    							myregion = worldguard.getRegionManager(player.getWorld()).getRegion(player.getName()+"//"+i);
	    			    							if (myregion==null) {
	    			    								if (plugin.getConfig().getBoolean("create.expand-vert")) {
	    			    	    							Bukkit.dispatchCommand(player, "/expand vert");
	    			    	    						}
	    			    								Bukkit.dispatchCommand(player, "region define "+player.getName()+"//"+i);
    			    	    							Bukkit.dispatchCommand(player, "region addowner "+player.getName()+"//"+i+" "+player.getName());
	    			    	    						return true;
	    			    							}
	    			    						}
	    			    						plugin.msg(player,"&c"+player.getName()+"&7 "+plugin.getmsg("MSG14"));
    			    						}
    						    		}
			    						catch (Exception e) {
			    							e.printStackTrace();
			    						}
			    						finally {
			    							player.setOp(false);
			    						}
    						    		}
    						    		
    						    	}
    						    worldguard.getRegionManager(player.getWorld()).removeRegion("//");
	    						}
	    						else {
	    							plugin.msg(player,plugin.getmsg("MSG17"));
	    						}
    						return false;
    						}
    					plugin.msg(player,plugin.getmsg("MSG4")+" &cworldguard.region.define.own");
    				}
    				return false;
    			}
    			else if (args[0].equalsIgnoreCase("help")) {
    				plugin.msg(player,plugin.getmsg("MSG7"));
    				return true;
    			}
    			else if (args[0].equalsIgnoreCase("remove")) {
					if (plugin.lastmask.get(player.getName()).equals("~NULL")) {
						plugin.msg(player,plugin.getmsg("MSG1"));
						if (plugin.checkperm(player,"worldguard.region.remove.*")) {
							plugin.msg(player,plugin.getmsg("MSG9"));
						}
					}
					else {
						if (plugin.checkperm(player,"worldguard.region.remove.own.*")) {
							Bukkit.dispatchCommand(player, "region remove "+plugin.lastmask.get(player.getName()));
						}					
						else {
							plugin.msg(player,plugin.getmsg("MSG4")+" &cworldguard.region.remove.own.*");
						}
					}
					return true;
    			}
    			else {
    	
    				if (args.length==2) {
    					if (plugin.checkperm(player,"worldguard.region.flag.regions.own."+args[0])) {
    						if (plugin.lastmask.get(player.getName()).equals("~NULL")) {
    							plugin.msg(player,plugin.getmsg("MSG1"));
    							if (plugin.checkperm(player,"worldguard.region.flag.regions.*")) {
    								plugin.msg(player,plugin.getmsg("MSG10"));
    							}
    						}
    						else {
    							Bukkit.dispatchCommand(player,"region flag "+plugin.lastmask.get(player.getName())+" "+StringUtils.join(args," "));
    						}
    					}
    					else {
    						plugin.msg(player,plugin.getmsg("MSG4")+" &cworldguard.region.flag.regions.own."+args[0]);
    					}
    					return true;
    				}
    				else {
    					Bukkit.dispatchCommand(player,"wrg help");
    					return false;
    				}
    			}
    		}
    		Bukkit.dispatchCommand(player,"wrg help");
    	}
		return false;
	}

}

