package com.empcraft;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditListener;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.InvalidToolBindException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.SessionCheck;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.tools.AreaPickaxe;
import com.sk89q.worldedit.tools.BlockDataCyler;
import com.sk89q.worldedit.tools.BlockReplacer;
import com.sk89q.worldedit.tools.BrushTool;
import com.sk89q.worldedit.tools.DistanceWand;
import com.sk89q.worldedit.tools.FloatingTreeRemover;
import com.sk89q.worldedit.tools.FloodFillTool;
import com.sk89q.worldedit.tools.Tool;
import com.sk89q.worldedit.tools.TreePlanter;
import com.sk89q.worldedit.tools.brushes.ButcherBrush;
import com.sk89q.worldedit.tools.brushes.ClipboardBrush;
import com.sk89q.worldedit.tools.brushes.CylinderBrush;
import com.sk89q.worldedit.tools.brushes.GravityBrush;
import com.sk89q.worldedit.tools.brushes.HollowCylinderBrush;
import com.sk89q.worldedit.tools.brushes.HollowSphereBrush;
import com.sk89q.worldedit.tools.brushes.SmoothBrush;
import com.sk89q.worldedit.tools.brushes.SphereBrush;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class WorldeditRegions extends JavaPlugin implements Listener {
	WorldeditRegions plugin;
	WorldEditPlugin worldedit = null;
	WorldguardFeature wgf;
	FactionsFeature ff;
	ResidenceFeature rf;
	TownyFeature tf;
	GriefPreventionFeature gpf;
	PreciousStonesFeature psf;
	
	public Map<String, Object> masks = new HashMap<String, Object>();
	public Map<String, Object> lastmask = new HashMap<String, Object>();
	public Map<String, String> id = new HashMap<String, String>();
	public Map<String, Boolean> lastregion = new HashMap<String, Boolean>();
	
	public boolean contains(String search,List<String> mylist) {
		for (String mystr:mylist) {
			if (search.equalsIgnoreCase(mystr)) {
				return true;
			}
		}
		return false;
	}
	
	public String getmsg(String key) {
		File yamlFile = new File(getDataFolder(), getConfig().getString("language").toLowerCase()+".yml"); 
		YamlConfiguration.loadConfiguration(yamlFile);
		try {
			return colorise(YamlConfiguration.loadConfiguration(yamlFile).getString(key));
		}
		catch (Exception e){
			return "";
		}
	}
    public boolean checkperm(Player player,String perm) {
    	boolean hasperm = false;
    	String[] nodes = perm.split("\\.");
    	String n2 = "";
    	if (player==null) {
    		return true;
    	}
    	else if (player.hasPermission(perm)) {
    		hasperm = true;
    	}
    	else if (player.isOp()==true) {
    		hasperm = true;
    	}
    	else {
    		for(int i = 0; i < nodes.length-1; i++) {
    			n2+=nodes[i]+".";
            	if (player.hasPermission(n2+"*")) {
            		hasperm = true;
            	}
    		}
    	}
		return hasperm;
    }
    public void msg(Player player,String mystring) {
    	if (mystring==null||mystring.equals("")) {
    		return;
    	}
    	if (player==null) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else if (player instanceof Player==false) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else {
    		player.sendMessage(colorise(mystring));
    	}

    }
    public String colorise(String mystring) {
    	String[] codes = {"&1","&2","&3","&4","&5","&6","&7","&8","&9","&0","&a","&b","&c","&d","&e","&f","&r","&l","&m","&n","&o","&k"};
    	for (String code:codes) {
    		mystring = mystring.replace(code, "§"+code.charAt(1));
    	}
    	return mystring;
    }
	
	@Override
	public void onDisable() {
    	this.reloadConfig();
    	this.saveConfig();
        msg(null,"&f&oThanks for using &aWorldeditRegions&f by &dEmpire92&f!");
	}
	public boolean iswhitelisted(String arg) {
		List<String> mylist= plugin.getConfig().getStringList("whitelist");
		for(String current:mylist){
			if (arg.equalsIgnoreCase(current)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void onEnable(){
		plugin = this;
		
		Plugin worldguardPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if((worldguardPlugin != null) && worldguardPlugin.isEnabled()) {
        	wgf = new WorldguardFeature(worldguardPlugin,this);
            getServer().getPluginManager().registerEvents(wgf,this);
            msg(null,"Plugin 'WorldGuard' found. Using it now.");
        } else {
            msg(null,"Plugin 'WorldGuard' not found. Worldguard features disabled.");
        }
		Plugin townyPlugin = getServer().getPluginManager().getPlugin("Towny");
        if((townyPlugin != null) && townyPlugin.isEnabled()) {
        	tf = new TownyFeature(townyPlugin,this);
            getServer().getPluginManager().registerEvents(tf,this);
            msg(null,"Plugin 'Towny' found. Using it now.");
        } else {
            msg(null,"Plugin 'Towny' not found. Towny features disabled.");
        }
        Plugin factionsPlugin = getServer().getPluginManager().getPlugin("Factions");
        if((factionsPlugin != null) && factionsPlugin.isEnabled()) {
        	ff = new FactionsFeature(factionsPlugin,this);
            getServer().getPluginManager().registerEvents(ff,this);
            msg(null,"Plugin 'Factions' found. Using it now.");
        } else {
            msg(null,"Plugin 'Factions' not found. Factions features disabled.");
        }
        Plugin residencePlugin = getServer().getPluginManager().getPlugin("Residence");
        if((residencePlugin != null) && residencePlugin.isEnabled()) {
        	rf = new ResidenceFeature(residencePlugin,this);
            getServer().getPluginManager().registerEvents(rf,this);
            msg(null,"Plugin 'Residence' found. Using it now.");
        } else {
            msg(null,"Plugin 'Residence' not found. Factions features disabled.");
        }
        Plugin griefpreventionPlugin = getServer().getPluginManager().getPlugin("GriefPrevention");
        if((griefpreventionPlugin != null) && griefpreventionPlugin.isEnabled()) {
        	gpf = new GriefPreventionFeature(griefpreventionPlugin,this);
            getServer().getPluginManager().registerEvents(gpf,this);
            msg(null,"Plugin 'GriefPrevention' found. Using it now.");
        } else {
            msg(null,"Plugin 'GriefPrevention' not found. GriefPrevention features disabled.");
        }
        
        Plugin preciousstonesPlugin = getServer().getPluginManager().getPlugin("PreciousStones");
        if((preciousstonesPlugin != null) && preciousstonesPlugin.isEnabled()) {
        	psf = new PreciousStonesFeature(preciousstonesPlugin,this);
            getServer().getPluginManager().registerEvents(psf,this);
            msg(null,"Plugin 'PreciousStones' found. Using it now.");
        } else {
            msg(null,"Plugin 'PreciousStones' not found. PreciousStones features disabled.");
        }
        
		worldedit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        saveResource("english.yml", true);
        getConfig().options().copyDefaults(true);
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", "0.2.2");
        options.put("create.expand-vert",true);
        options.put("language","english");
        options.put("create.add-owner",true);
        options.put("max-region-count-per-player",7);
        options.put("max-claim-area", 1024);
        List<String> ignore = Arrays.asList("PlotMe","PlotWorld");
        options.put("ignore-worlds",ignore);
        for (final Entry<String, Object> node : options.entrySet()) {
        	 if (!getConfig().contains(node.getKey())) {
        		 getConfig().set(node.getKey(), node.getValue());
        	 }
        }
    	saveConfig();
    	this.saveDefaultConfig();
    	getServer().getPluginManager().registerEvents(this, this);   
		for (Player player:Bukkit.getOnlinePlayers()) {
			masks.put(player.getName(),"~NULL");
			lastmask.put(player.getName(),"~NULL");
			setmask(player,true);
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if (cmd.getName().equalsIgnoreCase("wrg")) {
    		Player player;
    		if (sender instanceof Player==false) {
    			player = null;
    			msg(player,getmsg("MSG0"));
    			return false;
    		}
    		else {
    			player = (Player) sender;
    		}
    		if (args.length>0) {
    			//TODO HELP
    			//TODO Create
    			//TODO remove
    			String myid = id.get(player.getName());
    			boolean usewg = false;
    			if (args[0].equalsIgnoreCase("create")) {
    				usewg = true;
    			}
    			else if (myid!=null) {
    				
    			if (myid.contains("CHUNK:")) {
    				return ff.ffCommand(sender, cmd, label, args);
    			}
    			else if (myid.contains("PLOT:")) {
    				return tf.tfCommand(sender, cmd, label, args);
    			}
    			else if (myid.contains("CLAIM:")) {
    				return gpf.gpfCommand(sender, cmd, label, args);
    			}
    			else if (myid.contains("FIELD:")) {
    				return psf.psfCommand(sender, cmd, label, args);
    			}
    			else if (myid.contains("RESIDENCE:")) {
    				return rf.psfCommand(sender, cmd, label, args);
    			}
    			else {
    				if (wgf!=null) {
    					usewg = true;
    				}
    			}
    			}
    			if (usewg) {
    				return wgf.wgfcommand(sender, cmd, label, args);
    			}
    			else {
    				if (args[0].equalsIgnoreCase("help")) {
    					msg(player,getmsg("MSG19"));
    				}
    				else if (args[0].equalsIgnoreCase("remove")) {
    					msg(player,getmsg("MSG1"));
    				}
    				else if (args[0].equalsIgnoreCase("share")) {
    					msg(player,getmsg("MSG1"));
    				}
    				else if (args[0].equalsIgnoreCase("trust")) {
    					msg(player,getmsg("MSG1"));
    				}
    				else if (args[0].equalsIgnoreCase("untrust")) {
    					msg(player,getmsg("MSG1"));
    				}
    				else if (args[0].equalsIgnoreCase("info")) {
    					msg(player,getmsg("MSG1"));
    				}
    				else {
    					msg(player,getmsg("MSG19"));
    					return false;
    				}
    				return true;
    			}
    		}
    		else {
    			msg(player,getmsg("MSG19"));
				return false;
    		}
    	}
    	return false;
	}
	
	public void setmask(Player player,Boolean remove) {
		LocalSession session = worldedit.getSession(player);
		List<String> disabled = getConfig().getStringList("ignore-worlds");
		if (contains(player.getWorld().getName(),disabled)) {
			return;
		}
		if (checkperm(player,"wrg.bypass")==false) {
			if (remove) {
				if (id.containsKey(player.getName())==false) {
					if (checkperm(player,"wrg.notify")) {
						msg(player,getmsg("MSG1")+"&7.");
					}
				}
				else if ((id.get(player.getName()).equals("~NULL"))==false) {
					if (checkperm(player,"wrg.notify")) {
						msg(player,getmsg("MSG1")+"&7.");
					}
				}
				masks.put(player.getName(),"~NULL");
				lastmask.put(player.getName(),"~NULL");
				id.put(player.getName(),"~NULL");
				lastregion.put(player.getName(),false);
				Vector pos1 = new Vector(Double.MAX_VALUE, 64, Double.MAX_VALUE);
				Vector pos2 = new Vector(Double.MAX_VALUE, 64, Double.MAX_VALUE);
				CuboidRegion cr = new CuboidRegion(session.getSelectionWorld(),pos1,pos2);
				RegionMask rm = new RegionMask(cr);
				session.setMask(rm);
				
			}
			else {
				CuboidRegion mymask = null;
				String myid = "";
				if (wgf!=null) {
					mymask = wgf.getcuboid(player);
					myid = wgf.getid(player);
				}
				if (gpf!=null&&mymask==null) {
					mymask = gpf.getcuboid(player);
					myid = gpf.getid(player);
				}
				if (rf!=null&&mymask==null) {
					mymask = rf.getcuboid(player);
					myid = rf.getid(player);
				}
				if (psf!=null&&mymask==null) {
					mymask = psf.getcuboid(player);
					myid = psf.getid(player);
				}
				if (tf!=null&&mymask==null) {
					mymask = tf.getcuboid(player);
					myid = tf.getid(player);
				}
				if (ff!=null&&mymask==null) {
					mymask = ff.getcuboid(player);
					myid = ff.getid(player);
				}
				
				
				if (mymask != null) {
					if ((id.get(player.getName()).equals(myid))==false) {
						msg(player,getmsg("MSG5")+" &a"+myid+"&7.");
						lastmask.put(player.getName(),mymask);
						id.put(player.getName(),myid);
						lastregion.put(player.getName(),true);
					}
					else {
						if (checkperm(player,"wrg.notify.greeting")) {
							if (lastregion.containsKey(player.getName())) {
								if (lastregion.get(player.getName())==false) {
									msg(player,getmsg("MSG21"));
								}
							}
						}
						lastregion.put(player.getName(),true);
						//TODO entering worldedit region.
					}
					masks.put(player.getName(),player.getWorld().getName());
					Vector pos1 = mymask.getMinimumPoint().toBlockPoint();
					Vector pos2 = mymask.getMaximumPoint().toBlockPoint();
					CuboidRegion cr = new CuboidRegion(session.getSelectionWorld(),pos1,pos2);
					RegionMask rm = new RegionMask(cr);
					session.setMask(rm);
				}
				else {
					//TODO check if they are inside cuboid
					if (lastmask.containsKey(player.getName())) {
						CuboidRegion cr = (CuboidRegion) lastmask.get(player.getName());
						Vector v = new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
						if (cr.contains(v)) {
							setmask(player,true);
						}
					}
					if (checkperm(player,"wrg.notify.farewell")) {
						if (lastregion.containsKey(player.getName())) {
							if (lastregion.get(player.getName())==true) {
								msg(player,getmsg("MSG22"));
							}
						}
					}
					lastregion.put(player.getName(),false);
				}
			}
		}
		else {
			session.setMask(null);
			}
			//BYPASSING MASK
	}
	
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		List<String> disabled = getConfig().getStringList("ignore-worlds");
		if (contains(player.getWorld().getName(),disabled)) {
			return;
		}
		if (checkperm(player,"wrg.bypass")) {
			return;
		}
		setmask(event.getPlayer(),false);
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK||event.getAction() == Action.RIGHT_CLICK_AIR) {
	    	try {
	    		LocalSession session = worldedit.getSession(player);
				Tool brush = session.getTool(player.getItemInHand().getTypeId());
				if (id.get(player.getName()).equals("~NULL")) {
					return;
				}
				CuboidRegion mymask = (CuboidRegion) lastmask.get(player.getName());
				Vector loc;
				if (event.getAction() == Action.RIGHT_CLICK_AIR) {
					loc = new Vector(player.getTargetBlock(null, 64).getX(), player.getTargetBlock(null, 64).getY(), player.getTargetBlock(null, 64).getZ());
				}
				else {
					loc = new Vector(event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
				}
				if (brush instanceof BlockReplacer) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG20"));
							event.setCancelled(true);
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof BlockDataCyler) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG20"));
							event.setCancelled(true);
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof DistanceWand) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof FloatingTreeRemover) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof TreePlanter) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof AreaPickaxe) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				
				if (brush instanceof AreaPickaxe) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof SphereBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof SmoothBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof HollowSphereBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof HollowCylinderBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof GravityBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof CylinderBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof ClipboardBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
				
				
				if (brush instanceof FloodFillTool) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG20"));
							event.setCancelled(true);
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof ButcherBrush) {
					try {
						if (mymask.contains(loc)==false) {
							msg(player,"3");
							msg(event.getPlayer(),getmsg("MSG20"));
							event.setCancelled(true);
							return;
						}
					}
					catch (Exception e) {
					}
				}
				if (brush instanceof BrushTool) {
					try {
						if (mymask.contains(loc)==false) {
							msg(event.getPlayer(),getmsg("MSG15"));
							return;
						}
					}
					catch (Exception e) {
					}
				}
	    	} catch (Exception e) {
			}
	    }
	}
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (masks.get(event.getPlayer().getName()).equals(event.getPlayer().getWorld().getName())) {
			setmask(event.getPlayer(),false);
		}
		else {
			setmask(event.getPlayer(),true);
			masks.put(event.getPlayer().getName(),"~NULL");
			lastmask.put(event.getPlayer().getName(),"~NULL");
			id.put(event.getPlayer().getName(),"~NULL");
		}
	}
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (masks.get(event.getPlayer().getName()).equals(event.getPlayer().getWorld().getName())) {
			setmask(event.getPlayer(),false);
		}
		else {
			setmask(event.getPlayer(),true);
			masks.put(event.getPlayer().getName(),"~NULL");
			lastmask.put(event.getPlayer().getName(),"~NULL");
			id.put(event.getPlayer().getName(),"~NULL");
			
			final Player myplayer = event.getPlayer();
			 
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	        scheduler.scheduleSyncDelayedTask(this, new Runnable() {

				@Override
				public void run() {
					setmask(myplayer,true);
					
				}
	        }, 20L);
			
		}
	}
	
	
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		setmask(event.getPlayer(),true);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		masks.remove(event.getPlayer().getName());
		lastmask.remove(event.getPlayer().getName());
		id.remove(event.getPlayer().getName());
		lastregion.remove(event.getPlayer().getName());
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		try {setmask(event.getPlayer(),false); } catch (Exception e) { e.printStackTrace(); }
	}
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String[] args = event.getMessage().split(" ");
		Player player = event.getPlayer();
		List<String> disabled = getConfig().getStringList("ignore-worlds");
		if (contains(player.getWorld().getName(),disabled)) {
			return;
		}
		if (checkperm(player,"wrg.bypass")) {
			return;
		}
		
		if (event.getMessage().startsWith("/up")) {
			if (args.length>1) {
				try {
					if (id.get(player.getName()).equals("~NULL")) {
						msg(event.getPlayer(),getmsg("MSG1"));
						event.setCancelled(true);
						return;
					}
					CuboidRegion mymask = (CuboidRegion) lastmask.get(player.getName());
					Vector loc = new Vector(player.getLocation().getX(), player.getLocation().getY()+Integer.parseInt(args[1]), player.getLocation().getZ());
					if (mymask.contains(loc)==false) {
						msg(event.getPlayer(),getmsg("MSG6"));
						event.setCancelled(true);
						return;
					}
				}
				catch (Exception e) {
				}
			}
		}
		if (event.getMessage().substring(0, Math.min(event.getMessage().length(), 2)).equals("//")) {
			boolean operation = false;
			
			
			
			
			
			
			
			if (checkperm(event.getPlayer(),"wrg.bypass")) {
				return;
			}
			if (event.getMessage().startsWith("//gmask")) {
				msg(event.getPlayer(),getmsg("MSG6"));
				event.setCancelled(true);
				return;
			}
			else if (event.getMessage().startsWith("//regen")) {
				if (id.get(player.getName()).equals("~NULL")) {
					msg(event.getPlayer(),getmsg("MSG1"));
					event.setCancelled(true);
					return;
				}
				Selection selection = worldedit.getSelection(event.getPlayer());
				if (selection!=null) {
					BlockVector pos1 = selection.getNativeMinimumPoint().toBlockVector();
				    BlockVector pos2 = selection.getNativeMaximumPoint().toBlockVector();
				    CuboidRegion myregion = (CuboidRegion) lastmask.get(event.getPlayer().getName());
				    try {
				    if (myregion==null) {
				    	msg(event.getPlayer(),getmsg("MSG1"));
				    }
				    else {
				    	if ((myregion.contains(pos1)&&myregion.contains(pos2))==false) {
				    		msg(event.getPlayer(),getmsg("MSG20"));
				    	}
				    	else {
				    		return;
				    	}
				    }
				    }
				    catch (Exception e) {
				    	
				    }
				}
				event.setCancelled(true);
			}
			else if (event.getMessage().startsWith("//set")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//replace")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//overlay")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//walls")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//outline")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//deform")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//hollow")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//smooth")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//move")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//move")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//stack")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//naturalize")) {
				operation = true;
			}
			else if (event.getMessage().startsWith("//paste")) {
				operation = true;
			}
			if (operation) {
				if (id.get(player.getName()).equals("~NULL")) {
					msg(event.getPlayer(),getmsg("MSG1"));
					event.setCancelled(true);
					return;
				}
				Selection selection = worldedit.getSelection(event.getPlayer());
				if (selection!=null) {
					BlockVector pos1 = selection.getNativeMinimumPoint().toBlockVector();
				    BlockVector pos2 = selection.getNativeMaximumPoint().toBlockVector();
				    CuboidRegion myregion = (CuboidRegion) lastmask.get(event.getPlayer().getName());
				    try {
				    if (myregion==null) {
				    	msg(event.getPlayer(),getmsg("MSG1"));
				    }
				    else {
				    	if ((myregion.contains(pos1)&&myregion.contains(pos2))==false) {
				    		msg(event.getPlayer(),getmsg("MSG15"));
				    	}							
				    }
				}
				catch (Exception e) {
					
				}
				}
			}
		}
	}
}
