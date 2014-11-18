package com.empcraft.wrg;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultFeature {
	WorldeditRegions WRG;
	private static Permission perms = null;
    public VaultFeature(WorldeditRegions plugin,Plugin vault) {
		WRG = plugin;
        setupPermissions();
	}
	public String[] getGroup(Player player) {
		return perms.getPlayerGroups(player);
	}
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = WRG.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
