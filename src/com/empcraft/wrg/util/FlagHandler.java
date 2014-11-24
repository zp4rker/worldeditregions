package com.empcraft.wrg.util;

import org.bukkit.plugin.Plugin;

import com.mewin.WGCustomFlags.FlagManager;
import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class FlagHandler {
    public static boolean              enabled          = false;
    private static WGCustomFlagsPlugin plugin           = null;

    public static StateFlag            WORLDEDIT_REGION = new StateFlag("worldedit-region", true);

    public FlagHandler(final Plugin plugin) {
        FlagHandler.enabled = true;
        FlagHandler.plugin = (WGCustomFlagsPlugin) plugin;

        FlagHandler.plugin.addCustomFlag(WORLDEDIT_REGION);
    }

    public static boolean hasFlag(final WorldGuardPlugin plugin, final ProtectedRegion region, final LocalPlayer player) {
        final Flag flag = FlagManager.getCustomFlag("worldedit-region");
        final Object state = region.getFlag(flag);
        if (state == null) {
            return false;
        }
        return (boolean) state;
    }
}
