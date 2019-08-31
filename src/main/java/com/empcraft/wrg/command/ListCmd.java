package com.empcraft.wrg.command;

import com.empcraft.wrg.regions.WorldguardFeature;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.profile.Profile;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class ListCmd {
    public static boolean execute(Player player, String[] args) {
        World bukkitWorld = player.getWorld();
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(bukkitWorld));
        Map<String, ProtectedRegion> regions = manager.getRegions();
        String mode;
        int page = 0;
        if (args.length == 1) {
            mode = "mine";
        } else {
            mode = args[1].toLowerCase();
            if (args.length == 3) {
                try {
                    page = Integer.parseInt(args[2]) - 1;
                } catch (Exception e) {
                    MainUtil.sendMessage(player, "&7Invalid page number: &c" + args[2]);
                }
            } else if (args.length != 2) {
                MainUtil.sendMessage(player, "&7Usage: /wrg list [mine|shared|all|<player>] [page]");
                return false;
            }
        }
        List<ProtectedRegion> allowed = new ArrayList<>();

        LocalPlayer lp = WorldguardFeature.worldguard.wrapPlayer(player);
        UUID uuid = lp.getUniqueId();
        String uuidStr = uuid.toString();

        switch (mode) {
            case "mine": {
                if (!MainUtil.hasPermission(player, "worldguard.region.list.own")) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.list.own");
                    return false;
                }
                String name = player.getName();
                for (Entry<String, ProtectedRegion> entry : regions.entrySet()) {
                    String[] split = entry.getKey().split("//");
                    if (split[0].equalsIgnoreCase(uuidStr) || split[0].equalsIgnoreCase(name)) {
                        allowed.add(entry.getValue());
                    }
                }
                break;
            }
            case "shared": {
                if (!MainUtil.hasPermission(player, "worldguard.region.list.shared")) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.list.shared");
                    return false;
                }
                String name = player.getName();
                for (Entry<String, ProtectedRegion> entry : regions.entrySet()) {
                    ProtectedRegion r = entry.getValue();
                    if (r.isOwner(lp) || r.isMember(lp) || r.isMember(name) || r.isOwner(name)) {
                        allowed.add(entry.getValue());
                    }
                }
                break;
            }
            case "all": {
                if (!MainUtil.hasPermission(player, "worldguard.region.list")) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.list");
                    return false;
                }
                for (Entry<String, ProtectedRegion> entry : regions.entrySet()) {
                    allowed.add(entry.getValue());
                }
                break;
            }
            // player
            default: {
                if (!MainUtil.hasPermission(player, "worldguard.region.list.other")) {
                    MainUtil.sendMessage(player, MainUtil.getMessage("MSG4") + " &cworldguard.region.list.other");
                    return false;
                }
                for (Entry<String, ProtectedRegion> entry : regions.entrySet()) {
                    String[] split = entry.getKey().split("//");
                    try {
                        UUID owner = UUID.fromString(split[0]);
                        Profile profile = WorldguardFeature.cache.getIfPresent(owner);
                        if (profile.getName().toLowerCase().startsWith(args[1])) {
                            allowed.add(entry.getValue());
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            }
        }
        displayRegions(player, mode, allowed, page);
        return true;
    }

    public static void displayRegions(Player player, String mode, List<ProtectedRegion> regions, int page) {
        Collections.sort(regions, new Comparator<ProtectedRegion>() {
            @Override
            public int compare(ProtectedRegion a, ProtectedRegion b) {
                String str1 = a.getId();
                String str2 = b.getId();
                int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                if (res == 0) {
                    res = str1.compareTo(str2);
                }
                return res;
            }
        });

        if (page < 0) {
            page = 0;
        }

        int pageSize = 8;

        // Get the total pages
        final int totalPages = (int) Math.ceil(regions.size() / pageSize);
        if (page > totalPages) {
            page = totalPages;
        }
        // Only display pageSize!
        int max = (page * pageSize) + pageSize;
        if (max > regions.size()) {
            max = regions.size();
        }
        ProtectedRegion r;

        // header

        MainUtil.sendMessage(player, "&6Regions for: &7`" + mode + "` &8(&7" + (page + 1) + "&8/&7" + (totalPages + 1) + "&8)");

        LocalPlayer lp = WorldguardFeature.worldguard.wrapPlayer(player);
        UUID uuid = lp.getUniqueId();
        String uuidStr = uuid.toString();

        for (int i = (page * pageSize); i < max; i++) {
            r = regions.get(i);

            String[] split = r.getId().split("//");
            ChatColor color;

            int index = 1;
            if (split.length == 2) {
                try {
                    index = Integer.parseInt(split[1]) + 2;
                } catch (Exception e) {
                }
            }

            List<String> trustedList = new ArrayList<>();
            DefaultDomain rt = r.getOwners();
            trustedList.addAll(rt.getGroups());
            trustedList.addAll(rt.getPlayers());
            long start = System.currentTimeMillis();
            for (UUID member : rt.getUniqueIds()) {
                Profile profile = WorldguardFeature.cache.getIfPresent(member);
                if (profile != null) {
                    trustedList.add(profile.getName());
                }
            }
            String trusted = StringUtils.join(trustedList, ", ");

            List<String> membersList = new ArrayList<>();
            DefaultDomain rm = r.getMembers();
            membersList.addAll(rm.getGroups());
            membersList.addAll(rm.getPlayers());
            for (UUID member : rm.getUniqueIds()) {
                Profile profile = WorldguardFeature.cache.getIfPresent(member);
                if (profile != null) {
                    membersList.add(profile.getName());
                }
            }
            String members = StringUtils.join(membersList, ", ");

            if (split[0].equalsIgnoreCase(uuidStr) || split[0].equalsIgnoreCase(player.getName())) {
                color = ChatColor.BLUE;
            } else if (r.isOwner(lp) || trustedList.contains(player.getName())) {
                color = ChatColor.DARK_AQUA;
            } else if (r.isMember(lp) || membersList.contains(player.getName())) {
                color = ChatColor.DARK_GREEN;
            } else {
                color = ChatColor.GRAY;
            }

            String flags = "";
            String prefix = "";
            for (Entry<Flag<?>, Object> flag : r.getFlags().entrySet()) {
                flags += prefix + flag.getKey().getName();
                prefix = ", ";
            }

            String id;
            if (membersList.contains(split[0])) {
                id = index + "";
            } else {
                try {
                    UUID owner = UUID.fromString(split[0]);
                    Profile profile = WorldguardFeature.cache.getIfPresent(owner);
                    if (profile == null) {
                        continue;
                    }
                    id = profile.getName();
                } catch (Exception e) {
                    continue;
                }
            }
            new FancyMessage("")
                    .then(" - ")
                    .color(ChatColor.DARK_GRAY)
                    .then("#")
                    .color(ChatColor.RED)
                    .command("/wrg home " + r.getId())
                    .then("" + index)
                    .tooltip("Click to teleport!")
                    .color(ChatColor.GRAY)
                    .command("/wrg home " + r.getId())
                    .then(" - ")
                    .color(ChatColor.DARK_GRAY)
                    .then(id)
                    .color(color)
                    .then(" [")
                    .color(ChatColor.DARK_GRAY)
                    .then("trusted")
                    .color(ChatColor.GRAY)
                    // tooltip
                    .tooltip(trusted)
                    .then("|")
                    .color(ChatColor.DARK_GRAY)
                    .then("members")
                    .color(ChatColor.GRAY)
                    // tooltip
                    .tooltip(members)
                    .then("|")
                    .color(ChatColor.DARK_GRAY)
                    .then("flags")
                    .color(ChatColor.GRAY)
                    // tooltip
                    .tooltip(flags)
                    .then("]")
                    .color(ChatColor.DARK_GRAY)
                    .send(player);
        }

        if (page == 0 && totalPages == 0) {
            return;
        }
        if (page < totalPages && page > 0) {
            // back | next 
            new FancyMessage("")
                    .color(ChatColor.GOLD)
                    .then("back")
                    .command("/wrg list " + mode + " " + (page))
                    .then(" | ")
                    .color(ChatColor.DARK_GRAY)
                    .then("next")
                    .color(ChatColor.GOLD)
                    .command("/wrg list " + mode + " " + (page + 2))
                    .send(player);
            return;
        }
        if (page == 0) {
            // next
            new FancyMessage("")
                    .then("back")
                    .color(ChatColor.DARK_GRAY)
                    .then(" | ")
                    .color(ChatColor.DARK_GRAY)
                    .then("next")
                    .color(ChatColor.GOLD)
                    .command("/wrg list " + mode + " " + (page + 2))
                    .send(player);
            return;
        }
        if (page == totalPages) {
            // back
            new FancyMessage("")
                    .then("back")
                    .color(ChatColor.GOLD)
                    .command("/wrg list " + mode + " " + (page))
                    .then(" | ")
                    .color(ChatColor.DARK_GRAY)
                    .then("next")
                    .color(ChatColor.DARK_GRAY)
                    .send(player);
            return;
        }
    }
}
