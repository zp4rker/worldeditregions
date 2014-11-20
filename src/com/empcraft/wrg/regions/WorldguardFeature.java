package com.empcraft.wrg.regions;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.empcraft.wrg.util.VaultHandler;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;



public class WorldguardFeature extends AbstractRegion {
	public static WorldGuardPlugin worldguard = null;
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
		if (myregion != null && (myregion.isOwner(localplayer) || (myregion.isMember(localplayer) && MainUtil.hasPermission(player, "wrg.worldguard.member")))) {
		    BlockVector pt1 = new BlockVector(Integer.MIN_VALUE, 0, Integer.MIN_VALUE);
		    BlockVector pt2 = new BlockVector(Integer.MAX_VALUE, 256, Integer.MAX_VALUE);
		    return new ProtectedCuboidRegion("__global__-"+player.getWorld().getName(), pt1, pt2);
		}
		ApplicableRegionSet regions = manager.getApplicableRegions(player.getLocation());
		for (ProtectedRegion region:regions) {
			if (region.isOwner(localplayer)) {
				return region;
			}
			if (region.isMember(localplayer)) {
				if (MainUtil.hasPermission(player, "wrg.worldguard.member"))
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
			if (VaultHandler.enabled) {
				String[] groups = VaultHandler.getGroup(player);
				boolean hasPerm = false;
				if (MainUtil.hasPermission(player,"wrg.worldguard.member")) {
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
	
	@Override
	public CuboidRegionWrapper getcuboid(Player player) {
		ProtectedRegion myregion = isowner(player);
		if (myregion!=null) {
			CuboidRegion cuboid = new CuboidRegion(myregion.getMinimumPoint(), myregion.getMaximumPoint());
			return new CuboidRegionWrapper(cuboid, myregion.getId());
		}
		else {
			return null;
		}
		
		
	}

	public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if (cmd.getName().equalsIgnoreCase("wrg")) {
    		Player player;
    		if (sender instanceof Player==false) {
    			player = null;
    			MainUtil.sendMessage(player,MainUtil.getmsg("MSG0"));
    			return false;
    		}
    		else {
    			player = (Player) sender;
    		}
    		if (args.length>0) {
    			if (args[0].equalsIgnoreCase("trust")) {
    				if (MainUtil.hasPermission(player,"worldguard.region.addmember.own.*")) {
	    				if (args.length>1) {
	    					if (RegionHandler.lastmask.get(player.getName()).equals("~NULL")) {
	    						MainUtil.sendMessage(player,MainUtil.getmsg("MSG1"));
	    					}
	    					else {
	    						DefaultDomain domain = worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).getMembers();
	    				        domain.addPlayer(args[1]);
	    				        worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).setMembers(domain);
	    				        MainUtil.sendMessage(player,MainUtil.getmsg("MSG2")+" &a"+args[1]+"&7.");
	    				        try {
	    				        	worldguard.getRegionManager(player.getWorld()).save();
    				        	} catch (Exception e) {
	    				        		e.printStackTrace();
    				        	}
	    					}
	    				}
	    				else {
	    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG3"));
	    				}
    				}
    				else {
    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG4")+" &cworldguard.region.addmember.own.*");
    				}
    				return true;
    			}
    			if (args[0].equalsIgnoreCase("share")) {
    				if (MainUtil.hasPermission(player,"worldguard.region.addowner.own.*")) {
	    				if (args.length>1) {
	    					if (RegionHandler.lastmask.get(player.getName()).equals("~NULL")) {
	    						MainUtil.sendMessage(player,MainUtil.getmsg("MSG1"));
	    					}
	    					else {
	    						DefaultDomain domain = worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).getOwners();
	    				        domain.addPlayer(args[1]);
	    				        worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).setOwners(domain);
	    				        MainUtil.sendMessage(player,MainUtil.getmsg("MSG2")+" &a"+args[1]+"&7.");
	    				        try {
	    				        	worldguard.getRegionManager(player.getWorld()).save();
    				        	} catch (Exception e) {
	    				        		e.printStackTrace();
    				        	}
	    					}
	    				}
	    				else {
	    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG11"));
	    				}
    				}
    				else {
    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG4")+" &cworldguard.region.addowner.own.*");
    				}
    				return true;
    			}
    			else if (args[0].equalsIgnoreCase("untrust")) {
    				if (MainUtil.hasPermission(player,"worldguard.region.removemember.own.*")) {
	    				if (args.length>1) {
	    					if (RegionHandler.lastmask.get(player.getName()).equals("~NULL")) {
	    						MainUtil.sendMessage(player,MainUtil.getmsg("MSG1"));
	    					}
	    					else {
	    						DefaultDomain domain = worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).getMembers();
	    				        domain.removePlayer(args[1]);
	    				        worldguard.getRegionManager(player.getWorld()).getRegion(RegionHandler.id.get(player.getName())).setMembers(domain);
	    				        MainUtil.sendMessage(player,MainUtil.getmsg("MSG12")+" &c"+args[1]+"&7.");
	    				        try {
	    				        	worldguard.getRegionManager(player.getWorld()).save();
    				        	} catch (Exception e) {
	    				        		e.printStackTrace();
    				        	}
	    					}
	    				}
	    				else {
	    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG13"));
	    				}
    				}
    				else {
    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG4")+" &cworldguard.region.removemember.own.*");
    				}
    				return true;
    			}
    			else if (args[0].equalsIgnoreCase("info")||args[0].equalsIgnoreCase("i")) {
					if (RegionHandler.id.get(player.getName()).equals("~NULL")) {
						MainUtil.sendMessage(player,MainUtil.getmsg("MSG1"));
					}
					else {
						Bukkit.dispatchCommand(player, "region info "+RegionHandler.id.get(player.getName()));
					}
					return true;
    			}
    			else if (args[0].equalsIgnoreCase("create")) {
    				if (MainUtil.hasPermission(player,"worldguard.region.define")) {
	    				if (args.length==2) {
	    				    boolean op = player.isOp();
	    				    player.setOp(true);
	    				    try {
	    					ProtectedRegion myregion = worldguard.getRegionManager(player.getWorld()).getRegion(args[1]);
	    					if (myregion==null) {
	    						if (WorldeditRegions.config.getBoolean("create.expand-vert")) {
	    							Bukkit.dispatchCommand(player, "/expand vert");
	    						}
	    						if (WorldeditRegions.config.getBoolean("create.add-owner")) {
	    							Bukkit.dispatchCommand(player, "region define "+args[1]+" "+args[1]);
	    						}
	    						else {
	    							Bukkit.dispatchCommand(player, "region define "+args[1]);
	    						}
	    						return true;
	    					}
	    					else {
	    						if (WorldeditRegions.config.getBoolean("create.expand-vert")) {
	    							Bukkit.dispatchCommand(player, "/expand vert");
	    						}
	    						int max = (WorldeditRegions.config.getInt("max-region-count-per-player"));
	    						for (int i=0;i<max-1;i++) {
	    							myregion = worldguard.getRegionManager(player.getWorld()).getRegion(args[1]+"//"+i);
	    							if (myregion==null) {
	    								if (WorldeditRegions.config.getBoolean("create.expand-vert")) {
	    	    							Bukkit.dispatchCommand(player, "/expand vert");
	    	    						}
	    	    						if (WorldeditRegions.config.getBoolean("create.add-owner")) {
	    	    							Bukkit.dispatchCommand(player, "region define "+args[1]+"//"+i+" "+args[1]);
	    	    						}
	    	    						else {
	    	    							Bukkit.dispatchCommand(player, "region define "+args[1]+"//"+i);
	    	    						}
	    	    						return true;
	    							}
	    								
	    						}
	    						MainUtil.sendMessage(player,"&c"+args[1]+"&7 "+MainUtil.getmsg("MSG14"));
	    					}
	    				    }
	    				    catch (Exception e) {
	    				        
	    				    }
	    				    finally {
	    				        player.setOp(op);
	    				    }
	    				}
	    				else {
	    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG8"));
	    				}
    				}
    				else {
    					if (MainUtil.hasPermission(player, "worldguard.region.define.own")) {
    						Selection selection = WorldeditRegions.worldedit.getSelection(player);
    						if (selection!=null) {
    							WorldeditRegions.worldedit.getSession(player);
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
    						    	MainUtil.sendMessage(player,MainUtil.getmsg("MSG16"));
    						    }
    						    else {
    						    	double area = (pos1.getX()-pos2.getX())*(pos1.getZ()-pos2.getZ());
    						    	if (area>WorldeditRegions.config.getDouble("max-claim-area")) {
    						    		MainUtil.sendMessage(player,MainUtil.getmsg("MSG18")+"&7 - "+area + " &c>&7 "+WorldeditRegions.config.getDouble("max-claim-area"));
    						    	}
    						    	else {
    						    		try {
			    							player.setOp(true);
	    									ProtectedRegion myregion = worldguard.getRegionManager(player.getWorld()).getRegion(player.getName());
	    			    					if (myregion==null) {
	    			    						if (WorldeditRegions.config.getBoolean("create.expand-vert")) {
	    			    							Bukkit.dispatchCommand(player, "/expand vert");
	    			    						}
	    			    						if (WorldeditRegions.config.getBoolean("create.add-owner")) {
	    			    							Bukkit.dispatchCommand(player, "region define "+player.getName()+" "+player.getName());
	    			    						}
	    			    						else {
	    			    							Bukkit.dispatchCommand(player, "region define "+player.getName());
	    			    						}
	    			    						return true;
	    			    					}
	    			    					else {
	    			    						if (WorldeditRegions.config.getBoolean("create.expand-vert")) {
	    			    							Bukkit.dispatchCommand(player, "/expand vert");
	    			    						}
	    			    						int max = (WorldeditRegions.config.getInt("max-region-count-per-player"));
	    			    						for (int i=0;i<max-1;i++) {
	    			    							myregion = worldguard.getRegionManager(player.getWorld()).getRegion(player.getName()+"//"+i);
	    			    							if (myregion==null) {
	    			    								if (WorldeditRegions.config.getBoolean("create.expand-vert")) {
	    			    	    							Bukkit.dispatchCommand(player, "/expand vert");
	    			    	    						}
	    			    								Bukkit.dispatchCommand(player, "region define "+player.getName()+"//"+i);
    			    	    							Bukkit.dispatchCommand(player, "region addowner "+player.getName()+"//"+i+" "+player.getName());
	    			    	    						return true;
	    			    							}
	    			    						}
	    			    						MainUtil.sendMessage(player,"&c"+player.getName()+"&7 "+MainUtil.getmsg("MSG14"));
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
	    							MainUtil.sendMessage(player,MainUtil.getmsg("MSG17"));
	    						}
    						return false;
    						}
    					MainUtil.sendMessage(player,MainUtil.getmsg("MSG4")+" &cworldguard.region.define.own");
    				}
    				return false;
    			}
    			else if (args[0].equalsIgnoreCase("help")) {
    				MainUtil.sendMessage(player,MainUtil.getmsg("MSG7"));
    				return true;
    			}
    			else if (args[0].equalsIgnoreCase("remove")) {
					if (RegionHandler.lastmask.get(player.getName()).equals("~NULL")) {
						MainUtil.sendMessage(player,MainUtil.getmsg("MSG1"));
						if (MainUtil.hasPermission(player,"worldguard.region.remove.*")) {
							MainUtil.sendMessage(player,MainUtil.getmsg("MSG9"));
						}
					}
					else {
						if (MainUtil.hasPermission(player,"worldguard.region.remove.own.*")) {
							Bukkit.dispatchCommand(player, "region remove "+RegionHandler.lastmask.get(player.getName()));
						}					
						else {
							MainUtil.sendMessage(player,MainUtil.getmsg("MSG4")+" &cworldguard.region.remove.own.*");
						}
					}
					return true;
    			}
    			else {
    	
    				if (args.length==2) {
    					if (MainUtil.hasPermission(player,"worldguard.region.flag.regions.own."+args[0])) {
    						if (RegionHandler.lastmask.get(player.getName()).equals("~NULL")) {
    							MainUtil.sendMessage(player,MainUtil.getmsg("MSG1"));
    							if (MainUtil.hasPermission(player,"worldguard.region.flag.regions.*")) {
    								MainUtil.sendMessage(player,MainUtil.getmsg("MSG10"));
    							}
    						}
    						else {
    							Bukkit.dispatchCommand(player,"region flag "+RegionHandler.lastmask.get(player.getName())+" "+StringUtils.join(args," "));
    						}
    					}
    					else {
    						MainUtil.sendMessage(player,MainUtil.getmsg("MSG4")+" &cworldguard.region.flag.regions.own."+args[0]);
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
	
	@Override
    public boolean hasPermission(Player player) {
        return MainUtil.hasPermission(player, "wrg.worldguard");
    }

}

