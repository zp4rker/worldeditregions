package com.empcraft.wrg.listener;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.Arrays;
import java.util.List;

public class PlayerListener implements Listener {

    public static boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("wrg")) {
            if ((sender instanceof Player) == false) {
                MainUtil.sendMessage(null, MainUtil.getMessage("MSG0"));
                return false;
            }
            final Player player = (Player) sender;

            if (WorldguardFeature.worldguard != null) {
                return WorldguardFeature.onCommand(sender, cmd, label, args);
            } else {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG24"));
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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

                BlockVector3 loc;
                if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    loc = BlockVector3.at(player.getTargetBlock(null, 64).getX(), player.getTargetBlock(null, 64).getY(), player.getTargetBlock(null, 64).getZ());
                } else {
                    loc = BlockVector3.at(event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
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
            } catch (final Exception e) {
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onWorldChange(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final String name = player.getName();
        if ((!RegionHandler.disabled.contains(player.getWorld().getName())) && (!RegionHandler.bypass.contains(name))) {
            RegionHandler.refreshPlayer(player);
            RegionHandler.setMask(event.getPlayer(), false);
        } else {
            RegionHandler.removeMask(player);
            RegionHandler.unregisterPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        RegionHandler.refreshPlayer(player);
        RegionHandler.setMask(player, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

        final List<String> restricted = Arrays.asList("/up", "//up", "/worldedit:/up");

        final List<String> masked = Arrays.asList("/regen", "//regen", "/worldedit:/regen", "/copy", "//copy", "/worldedit:/copy");

        final List<String> blocked = Arrays.asList("/gmask", "//gmask", "/worldedit:/gmask");

        final List<String> monitored = Arrays.asList("set", "replace", "overlay", "walls", "outline", "deform", "hollow", "smooth", "move", "stack", "naturalize", "paste");

        final String start = args[0].toLowerCase();

        if (restricted.contains(start)) {
            if (args.length > 1) {
                if (RegionHandler.id.containsKey(name) && (RegionHandler.id.get(name) != null)) {
                    final CuboidRegion mymask = RegionHandler.lastmask.get(player.getName());
                    final BlockVector3 loc = BlockVector3.at(player.getLocation().getX(), player.getLocation().getY() + Integer.parseInt(args[1]), player.getLocation().getZ());
                    if ((mymask == null) || (mymask.contains(loc) == false)) {
                        MainUtil.sendMessage(player, MainUtil.getMessage("MSG6"));
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                    event.setCancelled(true);
                    return;
                }
            } else {
                MainUtil.sendMessage(player, "&cToo few arguments.\n/up <block>");
                event.setCancelled(true);
                return;
            }
        } else if (masked.contains(start)) {
            try {
                final Region selection = WorldeditRegions.worldedit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
                if (selection != null) {
                    final BlockVector3 pos1 = selection.getMinimumPoint();
                    final BlockVector3 pos2 = selection.getMaximumPoint();
                    final CuboidRegion myregion = RegionHandler.lastmask.get(event.getPlayer().getName());
                    if (myregion == null) {
                        MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                    } else {
                        if ((myregion.contains(pos1) && !myregion.contains(pos2))) {
                            MainUtil.sendMessage(player, MainUtil.getMessage("MSG23"));
                        } else {
                            return;
                        }
                    }
                }
            } catch (IncompleteRegionException e) {
            }
            event.setCancelled(true);
        } else if (blocked.contains(start)) {
            MainUtil.sendMessage(player, MainUtil.getMessage("MSG6"));
            event.setCancelled(true);
            return;
        } else {
            for (final String cmd : monitored) {
                if (start.equals("//" + cmd) || start.equals("/" + cmd) || start.equals("/worldedit:/" + cmd)) {
                    try {
                        final Region selection = WorldeditRegions.worldedit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
                        if (selection != null) {
                            final BlockVector3 pos1 = selection.getMinimumPoint();
                            final BlockVector3 pos2 = selection.getMaximumPoint();
                            final CuboidRegion myregion = RegionHandler.lastmask.get(event.getPlayer().getName());
                            if (myregion == null) {
                                MainUtil.sendMessage(player, MainUtil.getMessage("MSG1")); // here
                            } else {
                                if ((myregion.contains(pos1) && !myregion.contains(pos2))) {
                                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG15"));
                                }
                            }
                        }
                    } catch (IncompleteRegionException e) {
                    }
                    return;
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        RegionHandler.unregisterPlayer(event.getPlayer());
    }
}
