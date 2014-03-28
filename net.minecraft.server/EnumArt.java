package net.minecraft.server;

public enum EnumArt {

    KEBAB("Kebab", 0, "Kebab", 16, 16, 0, 0), AZTEC("Aztec", 1, "Aztec", 16, 16, 16, 0), ALBAN("Alban", 2, "Alban", 16, 16, 32, 0), AZTEC2("Aztec2", 3, "Aztec2", 16, 16, 48, 0), BOMB("Bomb", 4, "Bomb", 16, 16, 64, 0), PLANT("Plant", 5, "Plant", 16, 16, 80, 0), WASTELAND("Wasteland", 6, "Wasteland", 16, 16, 96, 0), POOL("Pool", 7, "Pool", 32, 16, 0, 32), COURBET("Courbet", 8, "Courbet", 32, 16, 32, 32), SEA("Sea", 9, "Sea", 32, 16, 64, 32), SUNSET("Sunset", 10, "Sunset", 32, 16, 96, 32), CREEBET("Creebet", 11, "Creebet", 32, 16, 128, 32), WANDERER("Wanderer", 12, "Wanderer", 16, 32, 0, 64), GRAHAM("Graham", 13, "Graham", 16, 32, 16, 64), MATCH("Match", 14, "Match", 32, 32, 0, 128), BUST("Bust", 15, "Bust", 32, 32, 32, 128), STAGE("Stage", 16, "Stage", 32, 32, 64, 128), VOID("Void", 17, "Void", 32, 32, 96, 128), SKULL_AND_ROSES("SkullAndRoses", 18, "SkullAndRoses", 32, 32, 128, 128), WITHER("Wither", 19, "Wither", 32, 32, 160, 128), FIGHTERS("Fighters", 20, "Fighters", 64, 32, 0, 96), POINTER("Pointer", 21, "Pointer", 64, 64, 0, 192), PIGSCENE("Pigscene", 22, "Pigscene", 64, 64, 64, 192), BURNINGSKULL("BurningSkull", 23, "BurningSkull", 64, 64, 128, 192), SKELETON("Skeleton", 24, "Skeleton", 64, 48, 192, 64), DONKEYKONG("DonkeyKong", 25, "DonkeyKong", 64, 48, 192, 112);
    public static final int A = "SkullAndRoses".length();
    public final String B;
    public final int C;
    public final int D;
    public final int E;
    public final int F;
    private static final EnumArt[] G = new EnumArt[] { KEBAB, AZTEC, ALBAN, AZTEC2, BOMB, PLANT, WASTELAND, POOL, COURBET, SEA, SUNSET, CREEBET, WANDERER, GRAHAM, MATCH, BUST, STAGE, VOID, SKULL_AND_ROSES, WITHER, FIGHTERS, POINTER, PIGSCENE, BURNINGSKULL, SKELETON, DONKEYKONG};

    private EnumArt(String s, int i, String s1, int j, int k, int l, int i1) {
        this.B = s1;
        this.C = j;
        this.D = k;
        this.E = l;
        this.F = i1;
    }
}
