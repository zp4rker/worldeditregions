package com.empcraft.wrg.util;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.plugin.Plugin;

public class FlagHandler {
    public static boolean enabled = false;
    public static StateFlag WORLDEDIT_REGION = new StateFlag("worldedit-region", true);
    private static WGCustomFlagsPlugin plugin = null;

    public FlagHandler(final Plugin plugin) {
        FlagHandler.enabled = true;
        FlagHandler.plugin = (WGCustomFlagsPlugin) plugin;

        FlagHandler.plugin.addCustomFlag(WORLDEDIT_REGION);
    }

    public static boolean hasFlag(final ApplicableRegionSet set) {
        return set.testState(Associables.constant(Association.NON_MEMBER), WORLDEDIT_REGION);
    }
}
