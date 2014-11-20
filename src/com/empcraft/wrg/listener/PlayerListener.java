package com.empcraft.wrg.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.Selection;
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
import com.sk89q.worldedit.regions.CuboidRegion;

public class PlayerListener {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("wrg")) {
            if (sender instanceof Player==false) {
                MainUtil.sendMessage(null, MainUtil.getmsg("MSG0"));
                return false;
            }
            Player player = (Player) sender;
            
            if (WorldguardFeature.worldguard !=null) {
                return WorldguardFeature.onCommand(sender, cmd, label, args);
            }
            else {
                MainUtil.sendMessage(player, MainUtil.getmsg("MSG24"));
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority=EventPriority.LOWEST)
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return;
        }
        String name = player.getName();
        if (RegionHandler.bypass.contains(name)) {
            return;
        }
        
        RegionHandler.setMask(player, false);
        
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK||event.getAction() == Action.RIGHT_CLICK_AIR) {
            try {
                
                if (!RegionHandler.id.containsKey(name) || !RegionHandler.lastmask.containsKey(name)) {
                    return;
                }
                
                CuboidRegion region = RegionHandler.lastmask.get(player.getName());
                
                Vector loc;
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    loc = new Vector(player.getTargetBlock(null, 64).getX(), player.getTargetBlock(null, 64).getY(), player.getTargetBlock(null, 64).getZ());
                }
                else {
                    loc = new Vector(event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
                }
                
                if (region.contains(loc)) {
                    return;
                }
                boolean result = RegionHandler.maskManager.cancelBrush(player, loc, region);
                if (result) {
                    MainUtil.sendMessage(player, MainUtil.getmsg("MSG20"));
                    event.setCancelled(true);
                    return;
                }
            }
            catch (Exception e) {}
        }
    }
    @EventHandler
    public static void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if ((!RegionHandler.disabled.contains(player.getWorld().getName())) && (!RegionHandler.bypass.contains(name))) {
            RegionHandler.setMask(event.getPlayer(),false);
        }
        else {
            RegionHandler.removeMask(player);
            RegionHandler.unregisterPlayer(player);
        }
    }
    
    
    
    @EventHandler
    public static void onPlayerTeleport(PlayerTeleportEvent event) {
        
        Player player = event.getPlayer();
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return;
        }
        String name = player.getName();
        if (RegionHandler.bypass.contains(name)) {
            return;
        }
        
        Location f = event.getFrom();
        Location t = event.getTo();
        
        if (f != null && t != null) {
            if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
                RegionHandler.setMask(event.getPlayer(),false);
            }
        }
    }
    
    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RegionHandler.refreshPlayer(player);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RegionHandler.unregisterPlayer(event.getPlayer());
    }
    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent event) {
        RegionHandler.setMask(event.getPlayer(),false);
    }
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public static void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        Player player = event.getPlayer();
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return;
        }
        String name = player.getName();
        if (RegionHandler.bypass.contains(name)) {
            return;
        }
        
        String[] args = event.getMessage().split(" ");
        
        if (args.length == 0) {
            return;
        }
        
        List<String> restricted = Arrays.asList(new String[] {"/up","//up","/worldedit:/up"});
        
        List<String> masked = Arrays.asList(new String[] {"/regen","//regen","/worldedit:/regen", "/copy","//copy","/worldedit:/copy"});
        
        List<String> blocked = Arrays.asList(new String[] {"/gmask","//gmask","/worldedit:/gmask"});
        
        List<String> monitored = Arrays.asList(new String[] {"set", "replace", "overlay", "walls", "outline", "deform", "hollow", "smooth", "move", "stack", "naturalize", "paste"});
        
        String start = args[0].toLowerCase();
        
        if (restricted.contains(start)) {
            if (args.length>1) {
                if (RegionHandler.id.containsKey(name) && (RegionHandler.id.get(name) != null)) {
                    CuboidRegion mymask = RegionHandler.lastmask.get(player.getName());
                    Vector loc = new Vector(player.getLocation().getX(), player.getLocation().getY()+Integer.parseInt(args[1]), player.getLocation().getZ());
                    if (mymask == null || mymask.contains(loc)==false) {
                        MainUtil.sendMessage(player, MainUtil.getmsg("MSG6"));
                        event.setCancelled(true);
                        return;
                    }
                }
                else {
                    MainUtil.sendMessage(player, MainUtil.getmsg("MSG1"));
                    event.setCancelled(true);
                    return;
                }
            }
            else {
                MainUtil.sendMessage(player, "&cToo few arguments.\n/up <block>");
                event.setCancelled(true);
                return;
            }
        }
        else if (masked.contains(start)) {
            Selection selection = WorldeditRegions.worldedit.getSelection(event.getPlayer());
            if (selection!=null) {
                BlockVector pos1 = selection.getNativeMinimumPoint().toBlockVector();
                BlockVector pos2 = selection.getNativeMaximumPoint().toBlockVector();
                CuboidRegion myregion = RegionHandler.lastmask.get(event.getPlayer().getName());
                if (myregion==null) {
                    MainUtil.sendMessage(player, MainUtil.getmsg("MSG1"));
                }
                else {
                    if ((myregion.contains(pos1) && myregion.contains(pos2))==false) {
                        MainUtil.sendMessage(player, MainUtil.getmsg("MSG23"));
                    }
                    else {
                        return;
                    }
                }
            }
            event.setCancelled(true);
        }
        else if (blocked.contains(start)) {
            MainUtil.sendMessage(player, MainUtil.getmsg("MSG6"));
            event.setCancelled(true);
            return;
        }
        else {
            for (String cmd : monitored) {
                if (start.equals("//"+cmd) || start.equals("/"+cmd) || start.equals("/worldedit:/"+cmd)) {
                    Selection selection = WorldeditRegions.worldedit.getSelection(event.getPlayer());
                    if (selection!=null) {
                        BlockVector pos1 = selection.getNativeMinimumPoint().toBlockVector();
                        BlockVector pos2 = selection.getNativeMaximumPoint().toBlockVector();
                        CuboidRegion myregion = RegionHandler.lastmask.get(event.getPlayer().getName());
                        if (myregion==null) {
                            MainUtil.sendMessage(player, MainUtil.getmsg("MSG1"));
                        }
                        else {
                            if ((myregion.contains(pos1) && myregion.contains(pos2))==false) {
                                MainUtil.sendMessage(player, MainUtil.getmsg("MSG15"));
                            }
                        }
                    }
                    return;
                }
                
            }
        }
    }
}
