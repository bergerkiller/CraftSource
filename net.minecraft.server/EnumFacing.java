package net.minecraft.server;

public enum EnumFacing {

    a("DOWN", 0, 0, 1, 0, -1, 0), b("UP", 1, 1, 0, 0, 1, 0), c("NORTH", 2, 2, 3, 0, 0, -1), d("SOUTH", 3, 3, 2, 0, 0, 1), e("EAST", 4, 4, 5, -1, 0, 0), f("WEST", 5, 5, 4, 1, 0, 0);
    private final int g;
    private final int h;
    private final int i;
    private final int j;
    private final int k;
    private static final EnumFacing[] l = new EnumFacing[6];

    private static final EnumFacing[] m = new EnumFacing[] { a, b, c, d, e, f};

    private EnumFacing(String s, int i, int j, int k, int l, int i1, int j1) {
        this.g = j;
        this.h = k;
        this.i = l;
        this.j = i1;
        this.k = j1;
    }

    public int c() {
        return this.i;
    }

    public int e() {
        return this.k;
    }

    public static EnumFacing a(int i) {
        return l[i % l.length];
    }

    static {
        EnumFacing[] aenumfacing = values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing enumfacing = aenumfacing[j];

            l[enumfacing.g] = enumfacing;
        }
    }
}
