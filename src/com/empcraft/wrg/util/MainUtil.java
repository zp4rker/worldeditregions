package com.empcraft.wrg.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.empcraft.wrg.WorldeditRegions;

public class MainUtil {

    
    /**
     * Send a message where each additional argument will replace the next occurrence of %s
     */
    public static boolean sendMessage(final Player plr, String msg, final String... args) {
        if ((args != null) && (args.length > 0)) {
            if (msg.contains("%s")) {
                String prefix;
                String suffix;
                if (!MainUtil.getMessage("ITEM_COLOR").equals("")) {
                    prefix = MainUtil.getMessage("ITEM_COLOR");
                    suffix = ChatColor.getLastColors(msg.substring(0, msg.indexOf("%s")));
                }
                else {
                    prefix = "";
                    suffix = "";
                }
                for (final String s : args) {
                    if (msg.contains("%s")) {
                        msg = msg.replaceFirst("%s", prefix+s+suffix);
                    }
                }
            }
        }
        return sendMessage(plr, msg);
    }
    
    /**
     * Get message
     */
    public static String getMessage(String key) {
        return WorldeditRegions.language.getString(key);
    }

    /**
     * Send a message (use null for console)
     */
    public static boolean sendMessage(final Player plr, final String msg) {
        if ((msg.length() > 0) && !msg.equals("")) {
            if (plr == null) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            else {
                plr.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
        return true;
    }

    /**
     * Check if a user has the permissions in a range e.g.<br>
     * prisoncells.range.1, prisoncells.range.2 etc<br><br>
     * use a null player for console
     */
    public static int hasPermissionRange(final Player player, final String stub, final int range) {
        if ((player == null) || player.isOp()) {
            return Integer.MAX_VALUE;
        }
        if (player.hasPermission(stub + ".*")) {
            return Integer.MAX_VALUE;
        }
        for (int i = range; i > 0; i--) {
            if (player.hasPermission(stub + "." + i)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Check if a player has a permission<br>
     *  - supports * nodes e.g. prisoncells.blah.*<br>
     *  - supports op<br><br>
     *  use null player for console
     */
    public static boolean hasPermission(final Player player, final String perm) {
        if ((player == null) || player.isOp()) {
            return true;
        }
        if (player.hasPermission(perm)) {
            return true;
        }
        final String[] nodes = perm.split("\\.");
        final StringBuilder n = new StringBuilder();
        for (int i = 0; i < (nodes.length - 1); i++) {
            n.append(nodes[i]).append(".");
            if (player.hasPermission(n + "*")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if a player has several permissions<br>
     *  - supports * nodes e.g. prisoncells.blah.*<br>
     *  - supports op<br><br>
     *  use null player for console
     */
    public static boolean hasPermissions(final Player player, final String[] perms) {
        // Assumes null player is console.
        if ((player == null) || player.isOp()) {
            return true;
        }
        for (final String perm : perms) {
            boolean hasperm = false;
            if (player.hasPermission(perm)) {
                hasperm = true;
            }
            else {
                final String[] nodes = perm.split("\\.");
                final StringBuilder n = new StringBuilder();
                for (int i = 0; i < (nodes.length - 1); i++) {
                    n.append(nodes[i]).append(".");
                    if (player.hasPermission(n + "*")) {
                        hasperm = true;
                        break;
                    }
                }
            }
            if (!hasperm) {
                return false;
            }
        }

        return true;
    }
}
