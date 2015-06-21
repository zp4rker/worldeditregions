package com.empcraft.wrg.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.empcraft.wrg.AbsWE;
import com.empcraft.wrg.WE6;
import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

public class RegionHandler {

    public static AbsWE                     maskManager = new WE6();

    public static HashSet<AbstractRegion>   regions     = new HashSet<AbstractRegion>();

    public static Map<String, String>       masks       = new HashMap<String, String>();
    public static Map<String, CuboidRegion> lastmask    = new HashMap<String, CuboidRegion>();
    public static Map<String, String>       id          = new HashMap<String, String>();
    public static Map<String, Boolean>      lastregion  = new HashMap<String, Boolean>();
    public static Set<String>               bypass      = new HashSet<String>();

    public static HashSet<String>           disabled    = new HashSet<String>();

    public static boolean checkIgnored(final Player player) {
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return true;
        }
        final String name = player.getName();
        if (RegionHandler.bypass.contains(name)) {
            return true;
        }
        return false;
    }

    public static void unregisterPlayer(final Player player) {
        final String name = player.getName();
        masks.remove(name);
        lastmask.remove(name);
        id.remove(name);
        lastregion.remove(name);
        bypass.remove(name);
    }

    public static void refreshPlayer(final Player player) {
        if (MainUtil.hasPermission(player, "wrg.bypass")) {
            RegionHandler.removeMask(player);
            RegionHandler.bypass.add(player.getName());
        }
        else {
            RegionHandler.setMask(player, true);
        }
    }

    public static void removeMask(final Player player) {
        final LocalSession session = WorldeditRegions.worldedit.getSession(player);
        maskManager.removeMask(session);
    }

    public static void setMask(final Player player, final boolean remove) {
        final LocalSession session = WorldeditRegions.worldedit.getSession(player);
        if (RegionHandler.disabled.contains(player.getWorld().getName())) {
            return;
        }

        final String name = player.getName();

        if (RegionHandler.bypass.contains(name)) {
            return;
        }

        if (remove) {
            if (id.containsKey(name) && ((id.get(name)) != null)) {
                if (MainUtil.hasPermission(player, "wrg.notify")) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                }
            }
            unregisterPlayer(player);
            removeMask(player);
        }
        else {
            String myID = null;
            CuboidRegion myMask = null;
            for (final AbstractRegion region : regions) {
                if (region.hasPermission(player)) {
                    final CuboidRegionWrapper wrapper = region.getcuboid(player);
                    if (wrapper != null) {
                        myID = wrapper.id;
                        myMask = wrapper.cuboid;
                        break;
                    }
                }
            }
            if (myMask != null) {
                if (id.containsKey(name) && lastregion.containsKey(name)) {
                    if (id.get(name).equals(myID) && (id.get(name) != null)) {
                        if (!lastregion.get(name)) {
                            if (MainUtil.hasPermission(player, "wrg.notify.greeting")) {
                                MainUtil.sendMessage(player, MainUtil.getMessage("MSG21"), myID);
                            }
                        }
                    }
                }
                else if (MainUtil.hasPermission(player, "wrg.notify")) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG5"), myID);
                }
                lastmask.put(player.getName(), myMask);
                id.put(name, myID);
                lastregion.put(name, true);
                masks.put(player.getName(), player.getWorld().getName());
                final Vector pos1 = myMask.getMinimumPoint().toBlockPoint();
                final Vector pos2 = myMask.getMaximumPoint().toBlockPoint();
                final CuboidRegion cr = new CuboidRegion(session.getSelectionWorld(), pos1, pos2);
                maskManager.setMask(player, cr);
            }
            else {
                if (id.containsKey(name) && lastregion.containsKey(name)) {
                    if ((id.get(name) != null) && (!id.get(name).equals(myID))) {
                        if (lastregion.get(name)) {
                            if (MainUtil.hasPermission(player, "wrg.notify.farewell")) {
                                MainUtil.sendMessage(player, MainUtil.getMessage("MSG22"), myID);
                            }
                        }
                        lastregion.put(name, false);
                        return;
                    }
                }
                else {
                    lastregion.put(name, false);
                    final CuboidRegion cr = new CuboidRegion(session.getSelectionWorld(), new Vector(69, 69, 69), new Vector(69, 69, 69));
                    maskManager.setMask(player, cr);
                }
            }
        }
    }
}
