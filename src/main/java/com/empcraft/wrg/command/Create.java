package com.empcraft.wrg.command;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Create {
    public static boolean execute(Player player, String[] args) {

        Region selection;
        try {
            selection = WorldeditRegions.worldedit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
        } catch (IncompleteRegionException e) {
            MainUtil.sendMessage(player, "&cPlease make a selection with WorldEdit first!");
            return false;
        }

        Player owner;

        if (args.length == 2) {
            if (!MainUtil.hasPermission(player, "worldguard.region.define")) {
                MainUtil.sendMessage(player, "&cUsage: /wrg create");
                return false;
            }
            owner = Bukkit.getPlayer(args[1]);
            if (owner == null) {
                MainUtil.sendMessage(player, "&cPlayer not found: " + args[1]);
                return false;
            }
        } else if (args.length != 1) {
            if (!MainUtil.hasPermission(player, "worldguard.region.define")) {
                MainUtil.sendMessage(player, "&cUsage: /wrg create");
                return false;
            }
            MainUtil.sendMessage(player, "&cUsage: /wrg create");
            return false;
        } else {
            owner = player;
        }

        UUID uuid = owner.getUniqueId();
        String uuidStr = uuid.toString();

        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));

        String regionName = uuidStr;
        boolean found = false;

        int max = MainUtil.hasPermissionRange(player, "wrg.limit", 512);

        if (manager.getRegion(uuidStr) != null) {
            for (int i = 0; i < max; i++) {
                if (manager.getRegion(uuidStr + "//" + i) == null) {
                    found = true;
                    regionName = uuidStr + "//" + i;
                    break;
                }
            }
        } else {
            found = true;
        }

        if (found == false) {
            MainUtil.sendMessage(player, "&c" + player.getName() + " " + MainUtil.getMessage("MSG14"));
            return false;
        }


        // get allowed regions

        // todo use UUIDS
        // get next free region for player

        BlockVector3 pos1 = selection.getMinimumPoint();
        BlockVector3 pos2 = selection.getMaximumPoint();

        int min = WorldeditRegions.config.getInt("min-width");
        if (Math.abs(pos2.getBlockX() - pos1.getBlockX()) < min || Math.abs(pos2.getBlockZ() - pos1.getBlockZ()) < min) {
            MainUtil.sendMessage(player, "&cSelection is too small: < " + min + "x" + min);
            return false;
        }

        if (WorldeditRegions.config.getBoolean("create.expand-vert")) {
            pos1 = pos1.withY(0);
            pos2 = pos2.withY(255);
        }

        int area = WorldeditRegions.config.getInt("max-claim-area");
        if (Math.abs(pos2.getBlockX() - pos1.getBlockX()) * Math.abs(pos2.getBlockZ() - pos1.getBlockZ()) > area) {
            MainUtil.sendMessage(player, "&cSelection is too big: length x width > " + area);
            return false;
        }

        ProtectedCuboidRegion pr = new ProtectedCuboidRegion(regionName, pos1, pos2);

        ApplicableRegionSet regions = manager.getApplicableRegions(pr);
        if (regions.size() > 0) {
            MainUtil.sendMessage(player, "&cYour region overlaps with existing regions");
            for (ProtectedRegion region : regions.getRegions()) {
                WorldguardFeature.temporaryHighlight(region.getMinimumPoint().toBlockVector2(), region.getMaximumPoint().toBlockVector2(), player, Material.OBSIDIAN, 60);
                String[] split = region.getId().split("//");
                String name = split[0];
                try {
                    name = WorldguardFeature.cache.getIfPresent(UUID.fromString(name)).getName();
                    if (split.length == 2) {
                        name += split[1];
                    }
                } catch (Exception e) {
                }
                MainUtil.sendMessage(player, "&c - " + name);
            }
            return false;
        }

        pr.getOwners().addPlayer(uuid);

        manager.addRegion(pr);
        try {
            manager.save();
        } catch (StorageException e) {
            e.printStackTrace();
        }

        MainUtil.sendMessage(player, "&aSuccessfully created region!");
        return true;
    }
}