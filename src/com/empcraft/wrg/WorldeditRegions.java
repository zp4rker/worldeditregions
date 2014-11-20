package com.empcraft.wrg;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.plugin.java.JavaPlugin;

import com.empcraft.wrg.regions.FactionsFeature;
import com.empcraft.wrg.regions.GriefPreventionFeature;
import com.empcraft.wrg.regions.OldFactionsFeature;
import com.empcraft.wrg.regions.PreciousStonesFeature;
import com.empcraft.wrg.regions.RegiosFeature;
import com.empcraft.wrg.regions.ResidenceFeature;
import com.empcraft.wrg.regions.TownyFeature;
import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.empcraft.wrg.util.VaultHandler;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.command.tool.AreaPickaxe;
import com.sk89q.worldedit.command.tool.BlockDataCyler;
import com.sk89q.worldedit.command.tool.BlockReplacer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.DistanceWand;
import com.sk89q.worldedit.command.tool.FloatingTreeRemover;
import com.sk89q.worldedit.command.tool.FloodFillTool;
import com.sk89q.worldedit.command.tool.Tool;
import com.sk89q.worldedit.command.tool.TreePlanter;
import com.sk89q.worldedit.command.tool.brush.ButcherBrush;
import com.sk89q.worldedit.command.tool.brush.ClipboardBrush;
import com.sk89q.worldedit.command.tool.brush.CylinderBrush;
import com.sk89q.worldedit.command.tool.brush.GravityBrush;
import com.sk89q.worldedit.command.tool.brush.HollowCylinderBrush;
import com.sk89q.worldedit.command.tool.brush.HollowSphereBrush;
import com.sk89q.worldedit.command.tool.brush.SmoothBrush;
import com.sk89q.worldedit.command.tool.brush.SphereBrush;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.RegionMask;

import org.bukkit.scheduler.BukkitScheduler;

public final class WorldeditRegions extends JavaPlugin implements Listener {
    public static String version = "0";
    public static WorldeditRegions plugin;
	public static WorldEditPlugin worldedit = null;
	public static YamlConfiguration language;
	public static FileConfiguration config;
	
	@Override
	public void onDisable() {
    	this.reloadConfig();
    	this.saveConfig();
        MainUtil.sendMessage(null,"&f&oThanks for using &aWorldeditRegions&f by &dEmpire92&f!");
	}
	
	public static boolean iswhitelisted(String arg) {
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
		version = getDescription().getVersion();
		
		RegionHandler.disabled.addAll(config.getStringList("ignore-worlds"));
		
		MainUtil.sendMessage(null,"&8----&9====&7WorldeditRegions v"+version+"&9====&8----");
		MainUtil.sendMessage(null,"&dby Empire92");
		
		Plugin vaultPlugin = getServer().getPluginManager().getPlugin("Vault");
        if((vaultPlugin != null) && vaultPlugin.isEnabled()) {
            new VaultHandler(this, vaultPlugin);
            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into Vault");
        }
		
		Plugin worldguardPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if((worldguardPlugin != null) && worldguardPlugin.isEnabled()) {
        	WorldguardFeature wgf = new WorldguardFeature(worldguardPlugin,this);
        	RegionHandler.regions.add(wgf);
            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into WorldGuard");
        }
		Plugin townyPlugin = getServer().getPluginManager().getPlugin("Towny");
        if((townyPlugin != null) && townyPlugin.isEnabled()) {
        	TownyFeature tf = new TownyFeature(townyPlugin,this);
        	RegionHandler.regions.add(tf);
            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into Towny");
        }
		Plugin regiosPlugin = getServer().getPluginManager().getPlugin("Regios");
        if((regiosPlugin != null) && regiosPlugin.isEnabled()) {
        	RegiosFeature rgf = new RegiosFeature(regiosPlugin,this);
        	RegionHandler.regions.add(rgf);
            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into Regios");
        }
        Plugin factionsPlugin = getServer().getPluginManager().getPlugin("Factions");
        Plugin mCorePlugin = getServer().getPluginManager().getPlugin("mcore");
        if((factionsPlugin != null) && factionsPlugin.isEnabled()) {
        	if (mCorePlugin !=null && mCorePlugin.isEnabled()) {
	        	FactionsFeature ff = new FactionsFeature(factionsPlugin,this);
	        	RegionHandler.regions.add(ff);
	            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into Factions");
        	}
        	else {
        	    OldFactionsFeature ff2 = new OldFactionsFeature(factionsPlugin,this);
        	    RegionHandler.regions.add(ff2);
                MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into Factions (older edition)");
        	}
        }
        Plugin residencePlugin = getServer().getPluginManager().getPlugin("Residence");
        if((residencePlugin != null) && residencePlugin.isEnabled()) {
        	ResidenceFeature rf = new ResidenceFeature(residencePlugin,this);
        	RegionHandler.regions.add(rf);
            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into Residence");
        }
        Plugin griefpreventionPlugin = getServer().getPluginManager().getPlugin("GriefPrevention");
        if((griefpreventionPlugin != null) && griefpreventionPlugin.isEnabled()) {
        	GriefPreventionFeature gpf = new GriefPreventionFeature(griefpreventionPlugin,this);
        	RegionHandler.regions.add(gpf);
            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into GriefPrevention");
        }
        Plugin preciousstonesPlugin = getServer().getPluginManager().getPlugin("PreciousStones");
        if((preciousstonesPlugin != null) && preciousstonesPlugin.isEnabled()) {
        	PreciousStonesFeature psf = new PreciousStonesFeature(preciousstonesPlugin,this);
        	RegionHandler.regions.add(psf);
            MainUtil.sendMessage(null,"&8[&9WRG&8] &7Hooking into PreciousStones");
        }
		worldedit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        saveResource("english.yml", true);
        getConfig().options().copyDefaults(true);
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", version);
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
		    RegionHandler.refreshPlayer(player);
		    RegionHandler.setMask(player, false);
		}
		
		File yamlFile = new File(getDataFolder(), getConfig().getString("language").toLowerCase()+".yml");
        language = YamlConfiguration.loadConfiguration(yamlFile);
        config = getConfig();
	}
	
}
