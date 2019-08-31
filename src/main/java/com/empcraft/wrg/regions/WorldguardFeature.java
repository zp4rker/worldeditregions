package com.empcraft.wrg.regions;

import com.empcraft.wrg.WorldeditRegions;
import com.empcraft.wrg.command.*;
import com.empcraft.wrg.object.AbstractRegion;
import com.empcraft.wrg.object.ChunkLoc;
import com.empcraft.wrg.object.CuboidRegionWrapper;
import com.empcraft.wrg.util.FlagHandler;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.empcraft.wrg.util.VaultHandler;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class WorldguardFeature extends AbstractRegion {
    public static WorldGuardPlugin worldguard = null;
    public static ProfileCache cache;
    WorldeditRegions plugin;

    public WorldguardFeature(final Plugin p2, final WorldeditRegions p3) {
        worldguard = getWorldGuard();
        cache = WorldGuard.getInstance().getProfileCache();
        this.plugin = p3;

    }

    public static void temporaryHighlight(final BlockVector2 bottom, final BlockVector2 top, final Player player, Material material, long time) {
        highlightRegion(bottom, top, player, material);
        Bukkit.getScheduler().runTaskLater(WorldeditRegions.plugin, new Runnable() {
            @Override
            public void run() {
                highlightRegion(bottom, top, player, null);
            }
        }, time);
    }

    public static void sendBlock(Player player, Location loc, Material material) {
        if (material != null) {
            player.sendBlockChange(loc, material.createBlockData());
        } else {
            Block block = loc.getBlock();
            BlockData data = block.getBlockData();
            player.sendBlockChange(loc, data);
        }
    }

    public static void highlightRegion(BlockVector2 bottom, BlockVector2 top, Player player, Material material) {
        int view_distance = Bukkit.getViewDistance();
        Chunk playerChunk = player.getLocation().getChunk();
        int pcx = playerChunk.getX();
        int pcz = playerChunk.getZ();
        World world = player.getWorld();
        HashMap<ChunkLoc, Chunk> chunks = new HashMap<>();
        int x, z;
        z = bottom.getBlockZ();
        for (x = bottom.getBlockX(); x <= (top.getBlockX() - 1); x++) {
            ChunkLoc loc = new ChunkLoc(x >> 4, z >> 4);
            Chunk c;
            if (chunks.containsKey(loc)) {
                c = chunks.get(loc);
                if (c == null) {
                    continue;
                }
            } else {
                if (Math.abs(pcx - loc.x) > view_distance || Math.abs(pcz - loc.z) > view_distance) {
                    chunks.put(loc, null);
                    continue;
                }
                c = world.getChunkAt(loc.x, loc.z);
                if (!c.isLoaded()) {
                    chunks.put(loc, null);
                    continue;
                } else {
                    chunks.put(loc, null);
                }
            }
            Location l = new Location(world, x, world.getHighestBlockYAt(x, z), z);
            sendBlock(player, l, material);
        }
        x = top.getBlockX();
        for (z = bottom.getBlockZ(); z <= (top.getBlockZ() - 1); z++) {
            ChunkLoc loc = new ChunkLoc(x >> 4, z >> 4);
            Chunk c;
            if (chunks.containsKey(loc)) {
                c = chunks.get(loc);
                if (c == null) {
                    continue;
                }
            } else {
                if (Math.abs(pcx - loc.x) > view_distance || Math.abs(pcz - loc.z) > view_distance) {
                    chunks.put(loc, null);
                    continue;
                }
                c = world.getChunkAt(loc.x, loc.z);
                if (!c.isLoaded()) {
                    chunks.put(loc, null);
                    continue;
                } else {
                    chunks.put(loc, null);
                }
            }
            Location l = new Location(world, x, world.getHighestBlockYAt(x, z), z);
            sendBlock(player, l, material);
        }
        z = top.getBlockZ();
        for (x = top.getBlockX(); x >= (bottom.getBlockX() + 1); x--) {
            ChunkLoc loc = new ChunkLoc(x >> 4, z >> 4);
            Chunk c;
            if (chunks.containsKey(loc)) {
                c = chunks.get(loc);
                if (c == null) {
                    continue;
                }
            } else {
                if (Math.abs(pcx - loc.x) > view_distance || Math.abs(pcz - loc.z) > view_distance) {
                    chunks.put(loc, null);
                    continue;
                }
                c = world.getChunkAt(loc.x, loc.z);
                if (!c.isLoaded()) {
                    chunks.put(loc, null);
                    continue;
                } else {
                    chunks.put(loc, null);
                }
            }
            Location l = new Location(world, x, world.getHighestBlockYAt(x, z), z);
            sendBlock(player, l, material);
        }
        x = bottom.getBlockX();
        for (z = top.getBlockZ(); z >= (bottom.getBlockZ() + 1); z--) {
            ChunkLoc loc = new ChunkLoc(x >> 4, z >> 4);
            Chunk c;
            if (chunks.containsKey(loc)) {
                c = chunks.get(loc);
                if (c == null) {
                    continue;
                }
            } else {
                if (Math.abs(pcx - loc.x) > view_distance || Math.abs(pcz - loc.z) > view_distance) {
                    chunks.put(loc, null);
                    continue;
                }
                c = world.getChunkAt(loc.x, loc.z);
                if (!c.isLoaded()) {
                    chunks.put(loc, null);
                    continue;
                } else {
                    chunks.put(loc, null);
                }
            }
            Location l = new Location(world, x, world.getHighestBlockYAt(x, z), z);
            sendBlock(player, l, material);
        }
    }

    public static boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("wrg")) {
            Player player;
            if ((sender instanceof Player) == false) {
                player = null;
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG0"));
                return false;
            } else {
                player = (Player) sender;
            }
            if (args.length == 0) {
                Bukkit.dispatchCommand(player, "wrg help");
                return false;
            }

            switch (args[0].toLowerCase()) {
                case "trust": {
                    return Trust.execute(player, args);
                }
                case "share": {
                    return Share.execute(player, args);
                }
                case "untrust": {
                    return Untrust.execute(player, args);
                }
                case "info": {
                    return Info.execute(player, args);
                }
                case "create": {
                    return Create.execute(player, args);
                }
                case "help": {
                    return Help.execute(player, args);
                }
                case "remove": {
                    return Remove.execute(player, args);
                }
                case "sethome": {
                    return Sethome.execute(player, args);
                }
                case "home": {
                    return Home.execute(player, args);
                }
                case "list": {
                    return ListCmd.execute(player, args);
                }
                case "flag": {
                    return Flag.execute(player, args);
                }
                default: {
                    if (args.length == 2) {
                        if (MainUtil.hasPermission(player, "worldguard.region.flag.regions.own." + args[0])) {
                            if (RegionHandler.lastmask.get(player.getName()) == null) {
                                MainUtil.sendMessage(player, MainUtil.getMessage("MSG1"));
                                if (MainUtil.hasPermission(player, "worldguard.region.flag.regions.*")) {
                                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG10"));
                                }
                            } else {
                                Bukkit.dispatchCommand(player, "region flag " + RegionHandler.lastmask.get(player.getName()) + " " + StringUtils.join(args, " "));
                            }
                        } else {
                            MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.flag.regions.own." + args[0]);
                        }
                        return true;
                    } else {
                        Bukkit.dispatchCommand(player, "wrg help");
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private WorldGuardPlugin getWorldGuard() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

    public ProtectedRegion isOwner(final Player player) {
        final LocalPlayer localplayer = WorldguardFeature.worldguard.wrapPlayer(player);
        final RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        final ProtectedRegion myregion = manager.getRegion("__global__");
        final ApplicableRegionSet regions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
        if ((myregion != null) && (!FlagHandler.enabled || FlagHandler.hasFlag(regions)) && (myregion.isOwner(localplayer) || (myregion.isMember(localplayer) && MainUtil.hasPermission(player, "wrg.WorldguardFeature.worldguard.member")))) {
            final BlockVector3 pt1 = BlockVector3.at(Integer.MIN_VALUE, 0, Integer.MIN_VALUE);
            final BlockVector3 pt2 = BlockVector3.at(Integer.MAX_VALUE, 256, Integer.MAX_VALUE);
            return new ProtectedCuboidRegion("__global__-" + player.getWorld().getName(), pt1, pt2);
        }
        for (final ProtectedRegion region : regions) {
            if (FlagHandler.enabled && (!FlagHandler.hasFlag(regions))) {
                continue;
            }
            if (region.isOwner(localplayer)) {
                return region;
            }
            if (region.isMember(localplayer)) {
                if (MainUtil.hasPermission(player, "wrg.WorldguardFeature.worldguard.member")) {
                    return region;
                }
            } else if (region.getId().toLowerCase().equals(player.getName().toLowerCase())) {
                return region;
            } else if (region.getId().toLowerCase().contains(player.getName().toLowerCase() + "//")) {
                return region;
            } else if (region.isOwner("*")) {
                return region;
            }
            if (VaultHandler.enabled) {
                final String[] groups = VaultHandler.getGroup(player);
                boolean hasPerm = false;
                if (MainUtil.hasPermission(player, "wrg.WorldguardFeature.worldguard.member")) {
                    hasPerm = true;
                }
                for (final String group : groups) {
                    final String regionGroups = region.getOwners().toGroupsString();
                    if (regionGroups.contains("*" + group)) {
                        return region;
                    } else if (hasPerm) {
                        final String regionGroupMembers = region.getMembers().toGroupsString();
                        if (regionGroupMembers.contains("*" + group)) {
                            return region;
                        }
                    }
                }
            }
        }
        return null;
    }

    public ProtectedRegion getRegion(final Player player, final BlockVector3 location) {
        final com.sk89q.worldguard.LocalPlayer localplayer = WorldguardFeature.worldguard.wrapPlayer(player);
        final ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).getApplicableRegions(location);
        for (final ProtectedRegion region : regions) {
            if (region.isOwner(localplayer)) {
                return region;
            } else if (region.getId().toLowerCase().equals(player.getName().toLowerCase())) {
                return region;
            } else if (region.getId().toLowerCase().contains(player.getName().toLowerCase() + "//")) {
                return region;
            } else if (region.isOwner("*")) {
                return region;
            }
        }
        return null;
    }

    @Override
    public CuboidRegionWrapper getCuboid(final Player player) {
        final ProtectedRegion myregion = isOwner(player);
        if (myregion != null) {
            final CuboidRegion cuboid = new CuboidRegion(myregion.getMinimumPoint(), myregion.getMaximumPoint());
            return new CuboidRegionWrapper(cuboid, myregion.getId());
        } else {
            return null;
        }

    }

    @Override
    public boolean hasPermission(final Player player) {
        return MainUtil.hasPermission(player, "wrg.worldguard");
    }

}
