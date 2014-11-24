package com.empcraft.wrg.object;

public class LocPair {
    public int xmin;
    public int ymin;
    public int xmax;
    public int ymax;

    public LocPair(final int x1, final int y1, final int x2, final int y2) {
        this.xmin = x1;
        this.xmax = x2;
        this.ymin = y1;
        this.ymax = y2;
    }

    public void add(final int x1, final int y1, final int x2, final int y2) {
        this.xmin += x1;
        this.xmax += x2;
        this.ymin += y1;
        this.ymax += y2;
    }
}
