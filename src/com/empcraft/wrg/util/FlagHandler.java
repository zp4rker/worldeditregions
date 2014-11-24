package com.empcraft.wrg.util;

import org.bukkit.plugin.Plugin;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class FlagHandler {
    public static boolean              enabled          = false;
    private static WGCustomFlagsPlugin plugin           = null;

    public static StateFlag            WORLDEDIT_REGION = new StateFlag("worldedit-region", true);

    public FlagHandler(final Plugin plugin) {
        FlagHandler.enabled = true;
        FlagHandler.plugin = (WGCustomFlagsPlugin) plugin;

        FlagHandler.plugin.addCustomFlag(WORLDEDIT_REGION);
    }

    public static boolean hasFlag(final ApplicableRegionSet set) {
        return set.allows(WORLDEDIT_REGION);
    }
}
