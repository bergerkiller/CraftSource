package net.minecraft.server;

public class GenLayerBiome extends GenLayer {

    private BiomeBase[] b;

    public GenLayerBiome(long i, GenLayer genlayer, WorldType worldtype) {
        super(i);
        this.b = new BiomeBase[] { BiomeBase.DESERT, BiomeBase.FOREST, BiomeBase.EXTREME_HILLS, BiomeBase.SWAMPLAND, BiomeBase.PLAINS, BiomeBase.TAIGA, BiomeBase.JUNGLE};
        this.a = genlayer;
        if (worldtype == WorldType.NORMAL_1_1) {
            this.b = new BiomeBase[] { BiomeBase.DESERT, BiomeBase.FOREST, BiomeBase.EXTREME_HILLS, BiomeBase.SWAMPLAND, BiomeBase.PLAINS, BiomeBase.TAIGA};
        }
    }

    public int[] a(int i, int j, int k, int l) {
        int[] aint = this.a.a(i, j, k, l);
        int[] aint1 = IntCache.a(k * l);

        for (int i1 = 0; i1 < l; ++i1) {
            for (int j1 = 0; j1 < k; ++j1) {
                this.a((long) (j1 + i), (long) (i1 + j));
                int k1 = aint[j1 + i1 * k];

                if (k1 == 0) {
                    aint1[j1 + i1 * k] = 0;
                } else if (k1 == BiomeBase.MUSHROOM_ISLAND.id) {
                    aint1[j1 + i1 * k] = k1;
                } else if (k1 == 1) {
                    aint1[j1 + i1 * k] = this.b[this.a(this.b.length)].id;
                } else {
                    int l1 = this.b[this.a(this.b.length)].id;

                    if (l1 == BiomeBase.TAIGA.id) {
                        aint1[j1 + i1 * k] = l1;
                    } else {
                        aint1[j1 + i1 * k] = BiomeBase.ICE_PLAINS.id;
                    }
                }
            }
        }

        return aint1;
    }
}
