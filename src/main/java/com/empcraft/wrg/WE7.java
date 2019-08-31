package com.empcraft.wrg;

import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.*;
import com.sk89q.worldedit.command.tool.brush.*;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.entity.Player;

public class WE7 extends AbsWE {

    @Override
    public void setMask(Player player, CuboidRegion region) {
        final RegionMask mask = new RegionMask(region);
        final LocalSession session = WorldeditRegions.worldedit.getSession(player);
        session.setMask(mask);
    }

    @Override
    public void removeMask(LocalSession session) {
        session.setMask(null);
    }

    @Override
    public boolean cancelBrush(Player player, BlockVector3 location, CuboidRegion region) {
        final LocalSession session = WorldeditRegions.worldedit.getSession(player);
        final Tool brush = session.getTool(BukkitAdapter.asItemType(player.getInventory().getItemInMainHand().getType()));
        if (brush != null) {

            if ((brush instanceof BlockReplacer) || (brush instanceof BlockDataCyler) || (brush instanceof FloodFillTool) || (brush instanceof ButcherBrush)) {
                return true;
            }

            if ((brush instanceof FloatingTreeRemover) || (brush instanceof TreePlanter) || (brush instanceof AreaPickaxe) || (brush instanceof SphereBrush) || (brush instanceof SmoothBrush) || (brush instanceof HollowSphereBrush) || (brush instanceof HollowCylinderBrush) || (brush instanceof GravityBrush) || (brush instanceof CylinderBrush) || (brush instanceof ClipboardBrush) || (brush instanceof BrushTool)) {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG15"));
            }

        }
        return false;
    }

}
