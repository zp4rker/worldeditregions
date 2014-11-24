package com.empcraft.wrg;

import org.bukkit.entity.Player;

import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.command.tool.AreaPickaxe;
import com.sk89q.worldedit.command.tool.BlockDataCyler;
import com.sk89q.worldedit.command.tool.BlockReplacer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.DistanceWand;
import com.sk89q.worldedit.command.tool.FloatingTreeRemover;
import com.sk89q.worldedit.command.tool.FloodFillTool;
import com.sk89q.worldedit.command.tool.Tool;
import com.sk89q.worldedit.command.tool.TreePlanter;
import com.sk89q.worldedit.command.tool.brush.ButcherBrush;
import com.sk89q.worldedit.command.tool.brush.ClipboardBrush;
import com.sk89q.worldedit.command.tool.brush.CylinderBrush;
import com.sk89q.worldedit.command.tool.brush.GravityBrush;
import com.sk89q.worldedit.command.tool.brush.HollowCylinderBrush;
import com.sk89q.worldedit.command.tool.brush.HollowSphereBrush;
import com.sk89q.worldedit.command.tool.brush.SmoothBrush;
import com.sk89q.worldedit.command.tool.brush.SphereBrush;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;

public class WE6 extends AbsWE {

    @Override
    public void setMask(final Player player, final CuboidRegion region) {
        final RegionMask mask = new RegionMask(region);
        final LocalSession session = WorldeditRegions.worldedit.getSession(player);
        session.setMask(mask);
    }

    @Override
    public void removeMask(final LocalSession session) {
        final Mask mask = null;
        session.setMask(mask);
    }

    @Override
    public boolean cancelBrush(final Player player, final Vector location, final CuboidRegion region) {
        final LocalSession session = WorldeditRegions.worldedit.getSession(player);
        final Tool brush = session.getTool(player.getItemInHand().getTypeId());
        if (brush != null) {

            if ((brush instanceof BlockReplacer) || (brush instanceof BlockDataCyler) || (brush instanceof FloodFillTool) || (brush instanceof ButcherBrush)) {
                return true;
            }

            if ((brush instanceof DistanceWand) || (brush instanceof FloatingTreeRemover) || (brush instanceof TreePlanter) || (brush instanceof AreaPickaxe) || (brush instanceof SphereBrush) || (brush instanceof SmoothBrush) || (brush instanceof HollowSphereBrush) || (brush instanceof HollowCylinderBrush) || (brush instanceof GravityBrush) || (brush instanceof CylinderBrush) || (brush instanceof ClipboardBrush) || (brush instanceof BrushTool)) {
                MainUtil.sendMessage(player, MainUtil.getMessage("MSG15"));
            }
        }
        return false;
    }

}
