package com.empcraft.wrg.util;

import com.empcraft.wrg.WorldeditRegions;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHandler {
    public static boolean enabled = false;

    static WorldeditRegions WRG;
    private static Permission perms = null;

    public VaultHandler(final WorldeditRegions plugin, final Plugin vault) {
        WRG = plugin;
        enabled = true;
        setupPermissions();
    }

    public static String[] getGroup(final Player player) {
        return perms.getPlayerGroups(player);
    }

    private static boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = WRG.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
