package com.empcraft.wrg.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;

public class PlayerListener implements Listener {
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("wrg")) {
            if ((sender instanceof Player) == false) {
                MainUtil.sendMessage(null, MainUtil.getMessage("MSG0"));
                return false;
            }
            final Player player = (Player) sender;

            if (WorldguardFeature.worldguard != null) {
                return WorldguardFeature.onCommand(sender, cmd, label, args);
            }
            else {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG24"));
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return;
        }
        final String name = player.getName();
        if (RegionHandler.bypass.contains(name)) {
            return;
        }

        RegionHandler.setMask(player, false);

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
            try {

                if (!RegionHandler.id.containsKey(name) || !RegionHandler.lastmask.containsKey(name)) {
                    return;
                }

                final CuboidRegion region = RegionHandler.lastmask.get(player.getName());

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
                final boolean result = RegionHandler.maskManager.cancelBrush(player, loc, region);
                if (result) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG20"));
                    event.setCancelled(true);
                    return;
                }
            }
            catch (final Exception e) {
            }
        }
    }

    @EventHandler
    public static void onWorldChange(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final String name = player.getName();
        if ((!RegionHandler.disabled.contains(player.getWorld().getName())) && (!RegionHandler.bypass.contains(name))) {
            RegionHandler.setMask(event.getPlayer(), false);
        }
        else {
            RegionHandler.removeMask(player);
            RegionHandler.unregisterPlayer(player);
        }
    }

    @EventHandler
    public static void onPlayerTeleport(final PlayerTeleportEvent event) {

        final Player player = event.getPlayer();
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return;
        }
        final String name = player.getName();
        if (RegionHandler.bypass.contains(name)) {
            return;
        }

        final Location f = event.getFrom();
        final Location t = event.getTo();

        if ((f != null) && (t != null)) {
            if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
                RegionHandler.setMask(event.getPlayer(), false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        RegionHandler.refreshPlayer(player);
        RegionHandler.setMask(player, false);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        RegionHandler.unregisterPlayer(event.getPlayer());
    }

    @EventHandler
    public static void onPlayerMove(final PlayerMoveEvent event) {
        final Location f = event.getFrom();
        final Location t = event.getTo();

        if ((f.getBlockX() != t.getBlockX()) || (f.getBlockZ() != t.getBlockZ()) || (f.getBlockY() != t.getBlockY())) {
            RegionHandler.setMask(event.getPlayer(), false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public static void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {

        final Player player = event.getPlayer();
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return;
        }
        final String name = player.getName();
        if (RegionHandler.bypass.contains(name)) {
            return;
        }

        final String[] args = event.getMessage().split(" ");

        if (args.length == 0) {
            return;
        }

        final List<String> restricted = Arrays.asList(new String[] { "/up", "//up", "/worldedit:/up" });

        final List<String> masked = Arrays.asList(new String[] { "/regen", "//regen", "/worldedit:/regen", "/copy", "//copy", "/worldedit:/copy" });

        final List<String> blocked = Arrays.asList(new String[] { "/gmask", "//gmask", "/worldedit:/gmask" });

        final List<String> monitored = Arrays.asList(new String[] { "set", "replace", "overlay", "walls", "outline", "deform", "hollow", "smooth", "move", "stack", "naturalize", "paste" });

        final String start = args[0].toLowerCase();

        if (restricted.contains(start)) {
            if (args.length > 1) {
                if (RegionHandler.id.containsKey(name) && (RegionHandler.id.get(name) != null)) {
                    final CuboidRegion mymask = RegionHandler.lastmask.get(player.getName());
                    final Vector loc = new Vector(player.getLocation().getX(), player.getLocation().getY() + Integer.parseInt(args[1]), player.getLocation().getZ());
                    if ((mymask == null) || (mymask.contains(loc) == false)) {
                        MainUtil.sendMessage(player, MainUtil.getMessage("MSG6"));
                        event.setCancelled(true);
                        return;
                    }
                }
                else {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
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
            final Selection selection = WorldeditRegions.worldedit.getSelection(event.getPlayer());
            if (selection != null) {
                final BlockVector pos1 = selection.getNativeMinimumPoint().toBlockVector();
                final BlockVector pos2 = selection.getNativeMaximumPoint().toBlockVector();
                final CuboidRegion myregion = RegionHandler.lastmask.get(event.getPlayer().getName());
                if (myregion == null) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                }
                else {
                    if ((myregion.contains(pos1) && myregion.contains(pos2)) == false) {
                        MainUtil.sendMessage(player, MainUtil.getMessage("MSG23"));
                    }
                    else {
                        return;
                    }
                }
            }
            event.setCancelled(true);
        }
        else if (blocked.contains(start)) {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG6"));
            event.setCancelled(true);
            return;
        }
        else {
            for (final String cmd : monitored) {
                if (start.equals("//" + cmd) || start.equals("/" + cmd) || start.equals("/worldedit:/" + cmd)) {
                    final Selection selection = WorldeditRegions.worldedit.getSelection(event.getPlayer());
                    if (selection != null) {
                        final BlockVector pos1 = selection.getNativeMinimumPoint().toBlockVector();
                        final BlockVector pos2 = selection.getNativeMaximumPoint().toBlockVector();
                        final CuboidRegion myregion = RegionHandler.lastmask.get(event.getPlayer().getName());
                        if (myregion == null) {
                            MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                        }
                        else {
                            if ((myregion.contains(pos1) && myregion.contains(pos2)) == false) {
                                MainUtil.sendMessage(player, MainUtil.getMessage("MSG15"));
                            }
                        }
                    }
                    return;
                }

            }
        }
    }
}
