package com.empcraft.wrg.object;

import com.sk89q.worldedit.regions.CuboidRegion;

public class CuboidRegionWrapper {
    public final CuboidRegion cuboid;
    public final String       id;

    public CuboidRegionWrapper(final CuboidRegion cuboid, final String id) {
        this.cuboid = cuboid;
        this.id = id;
    }
}
