package com.empcraft.wrg;

import org.bukkit.entity.Player;
import com.empcraft.wrg.util.MainUtil;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.masks.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.tools.AreaPickaxe;
import com.sk89q.worldedit.tools.BlockDataCyler;
import com.sk89q.worldedit.tools.BlockReplacer;
import com.sk89q.worldedit.tools.BrushTool;
import com.sk89q.worldedit.tools.DistanceWand;
import com.sk89q.worldedit.tools.FloatingTreeRemover;
import com.sk89q.worldedit.tools.FloodFillTool;
import com.sk89q.worldedit.tools.Tool;
import com.sk89q.worldedit.tools.TreePlanter;
import com.sk89q.worldedit.tools.brushes.ButcherBrush;
import com.sk89q.worldedit.tools.brushes.ClipboardBrush;
import com.sk89q.worldedit.tools.brushes.CylinderBrush;
import com.sk89q.worldedit.tools.brushes.GravityBrush;
import com.sk89q.worldedit.tools.brushes.HollowCylinderBrush;
import com.sk89q.worldedit.tools.brushes.HollowSphereBrush;
import com.sk89q.worldedit.tools.brushes.SmoothBrush;
import com.sk89q.worldedit.tools.brushes.SphereBrush;

/*
 *  Export with the version of worldedit that you prefer.
 *   - You will also need to set the AbsWE class to be used in the RegionHandler class
 *  
 *  
 */

public class WE5 extends AbsWE {

    @Override
    public void setMask(Player player, CuboidRegion region) {
        RegionMask mask = new RegionMask(region);
        LocalSession session = WorldeditRegions.worldedit.getSession(player);
        session.setMask(mask);
    }

    @Override
    public void removeMask(LocalSession session) {
        RegionMask mask = null;
        session.setMask(mask);
    }

    @Override
    public boolean cancelBrush(Player player, Vector location, CuboidRegion region) {
        LocalSession session = WorldeditRegions.worldedit.getSession(player);
        Tool brush = session.getTool(player.getItemInHand().getTypeId());
        if (brush != null) {
            
            if (brush instanceof BlockReplacer 
             || brush instanceof BlockDataCyler 
             || brush instanceof FloodFillTool 
             || brush instanceof ButcherBrush) {
                return true;
            }
            
            if (brush instanceof DistanceWand
             || brush instanceof FloatingTreeRemover
             || brush instanceof TreePlanter
             || brush instanceof AreaPickaxe
             || brush instanceof SphereBrush
             || brush instanceof SmoothBrush
             || brush instanceof HollowSphereBrush
             || brush instanceof HollowCylinderBrush
             || brush instanceof GravityBrush
             || brush instanceof CylinderBrush
             || brush instanceof ClipboardBrush
             || brush instanceof BrushTool) {
                MainUtil.sendMessage(player, MainUtil.getmsg("MSG15"));
            }
        }
        return false;
    }
    
}
