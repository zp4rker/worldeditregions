package com.empcraft.wrg;

import com.empcraft.wrg.listener.PlayerListener;
import com.empcraft.wrg.regions.*;
import com.empcraft.wrg.util.FlagHandler;
import com.empcraft.wrg.util.MainUtil;
import com.empcraft.wrg.util.RegionHandler;
import com.empcraft.wrg.util.VaultHandler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class WorldeditRegions extends JavaPlugin implements Listener {
    public static String version = "0";
    public static WorldeditRegions plugin;
    public static WorldEditPlugin worldedit = null;
    public static YamlConfiguration language;
    public static FileConfiguration config;

    public static boolean iswhitelisted(final String arg) {
        final List<String> mylist = plugin.getConfig().getStringList("whitelist");
        for (final String current : mylist) {
            if (arg.equalsIgnoreCase(current)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        this.reloadConfig();
        this.saveConfig();
        MainUtil.sendMessage(null, "&f&oThanks for using &aWorldeditRegions&f by &dEmpire92 (updated by zp4rker)&f!");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        return PlayerListener.onCommand(sender, cmd, label, args);
    }

    @Override
    public void onEnable() {
        plugin = this;
        version = getDescription().getVersion();

        saveResource("english.yml", true);
        getConfig().options().copyDefaults(true);
        final Map<String, Object> options = new HashMap<String, Object>();
        getConfig().set("version", version);
        options.put("create.expand-vert", true);
        options.put("factions.max-chunk-traversal", 10);
        options.put("language", "english");
        options.put("create.add-owner", true);
        options.put("min-width", 16);
        options.put("worldguard.require-custom-flag", false);
        options.put("max-claim-area", 1024);
        final List<String> ignore = Arrays.asList("PlotMe", "PlotWorld");
        options.put("ignore-worlds", ignore);
        for (final Entry<String, Object> node : options.entrySet()) {
            if (!getConfig().contains(node.getKey())) {
                getConfig().set(node.getKey(), node.getValue());
            }
        }
        saveConfig();
        this.saveDefaultConfig();
        final File yamlFile = new File(getDataFolder(), getConfig().getString("language").toLowerCase() + ".yml");
        language = YamlConfiguration.loadConfiguration(yamlFile);
        config = getConfig();
        RegionHandler.disabled.addAll(config.getStringList("ignore-worlds"));

        MainUtil.sendMessage(null, "&8----&9====&7WorldeditRegions v" + version + "&9====&8----");
        MainUtil.sendMessage(null, "&dby Empire92 (updated by zp4rker)");

        final Plugin vaultPlugin = getServer().getPluginManager().getPlugin("Vault");
        if ((vaultPlugin != null) && vaultPlugin.isEnabled()) {
            new VaultHandler(this, vaultPlugin);
            MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into Vault");
        }

        final Plugin worldguardPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if ((worldguardPlugin != null) && worldguardPlugin.isEnabled()) {
            final WorldguardFeature wgf = new WorldguardFeature(worldguardPlugin, this);
            RegionHandler.regions.add(wgf);
            MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into WorldGuard");

            if (config.getBoolean("worldguard.require-custom-flag")) {
                final Plugin wgCustomFlags = getServer().getPluginManager().getPlugin("WGCustomFlags");
                if ((wgCustomFlags != null) && wgCustomFlags.isEnabled()) {
                    new FlagHandler(wgCustomFlags);
                    MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into WGCustomFlags");
                }
            }

        }
        final Plugin townyPlugin = getServer().getPluginManager().getPlugin("Towny");
        if ((townyPlugin != null) && townyPlugin.isEnabled()) {
            final TownyFeature tf = new TownyFeature(townyPlugin, this);
            RegionHandler.regions.add(tf);
            MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into Towny");
        }
        final Plugin regiosPlugin = getServer().getPluginManager().getPlugin("Regios");
        if ((regiosPlugin != null) && regiosPlugin.isEnabled()) {
            final RegiosFeature rgf = new RegiosFeature(regiosPlugin, this);
            RegionHandler.regions.add(rgf);
            MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into Regios");
        }
        final Plugin factionsPlugin = getServer().getPluginManager().getPlugin("Factions");
        Plugin mCorePlugin = getServer().getPluginManager().getPlugin("MassiveCore");
        if (mCorePlugin == null) mCorePlugin = getServer().getPluginManager().getPlugin("MassiveCore");
        if ((factionsPlugin != null) && factionsPlugin.isEnabled()) {
            if ((mCorePlugin != null) && mCorePlugin.isEnabled()) {
                final FactionsFeature ff = new FactionsFeature(factionsPlugin, this);
                RegionHandler.regions.add(ff);
                MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into Factions");
            }
        }
        final Plugin residencePlugin = getServer().getPluginManager().getPlugin("Residence");
        if ((residencePlugin != null) && residencePlugin.isEnabled()) {
            final ResidenceFeature rf = new ResidenceFeature(residencePlugin, this);
            RegionHandler.regions.add(rf);
            MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into Residence");
        }
        final Plugin griefpreventionPlugin = getServer().getPluginManager().getPlugin("GriefPrevention");
        if ((griefpreventionPlugin != null) && griefpreventionPlugin.isEnabled()) {
            final GriefPreventionFeature gpf = new GriefPreventionFeature(griefpreventionPlugin, this);
            RegionHandler.regions.add(gpf);
            MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into GriefPrevention");
        }

        final Plugin preciousstonesPlugin = getServer().getPluginManager().getPlugin("PreciousStones");
        if ((preciousstonesPlugin != null) && preciousstonesPlugin.isEnabled()) {
            final PreciousStonesFeature psf = new PreciousStonesFeature(preciousstonesPlugin, this);
            RegionHandler.regions.add(psf);
            MainUtil.sendMessage(null, "&8[&9WRG&8] &7Hooking into PreciousStones");
        }
        worldedit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        for (final Player player : Bukkit.getOnlinePlayers()) {
            RegionHandler.refreshPlayer(player);
            RegionHandler.setMask(player, false);
        }
    }

}
