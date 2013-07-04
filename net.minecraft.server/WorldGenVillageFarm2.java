package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class WorldGenVillageFarm2 extends WorldGenVillagePiece {

    private int a = -1;
    private int b;
    private int c;
    private int d;
    private int e;

    public WorldGenVillageFarm2(WorldGenVillageStartPiece worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(worldgenvillagestartpiece, i);
        this.g = j;
        this.f = structureboundingbox;
        this.b = this.a(random);
        this.c = this.a(random);
        this.d = this.a(random);
        this.e = this.a(random);
    }

    private int a(Random random) {
        switch (random.nextInt(5)) {
        case 0:
            return Block.CARROTS.id;

        case 1:
            return Block.POTATOES.id;

        default:
            return Block.CROPS.id;
        }
    }

    public static WorldGenVillageFarm2 a(WorldGenVillageStartPiece worldgenvillagestartpiece, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 13, 4, 9, l);

        return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillageFarm2(worldgenvillagestartpiece, i1, random, structureboundingbox, l) : null;
    }

    public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.a < 0) {
            this.a = this.b(world, structureboundingbox);
            if (this.a < 0) {
                return true;
            }

            this.f.a(0, this.a - this.f.e + 4 - 1, 0);
        }

        this.a(world, structureboundingbox, 0, 1, 0, 12, 4, 8, 0, 0, false);
        this.a(world, structureboundingbox, 1, 0, 1, 2, 0, 7, Block.SOIL.id, Block.SOIL.id, false);
        this.a(world, structureboundingbox, 4, 0, 1, 5, 0, 7, Block.SOIL.id, Block.SOIL.id, false);
        this.a(world, structureboundingbox, 7, 0, 1, 8, 0, 7, Block.SOIL.id, Block.SOIL.id, false);
        this.a(world, structureboundingbox, 10, 0, 1, 11, 0, 7, Block.SOIL.id, Block.SOIL.id, false);
        this.a(world, structureboundingbox, 0, 0, 0, 0, 0, 8, Block.LOG.id, Block.LOG.id, false);
        this.a(world, structureboundingbox, 6, 0, 0, 6, 0, 8, Block.LOG.id, Block.LOG.id, false);
        this.a(world, structureboundingbox, 12, 0, 0, 12, 0, 8, Block.LOG.id, Block.LOG.id, false);
        this.a(world, structureboundingbox, 1, 0, 0, 11, 0, 0, Block.LOG.id, Block.LOG.id, false);
        this.a(world, structureboundingbox, 1, 0, 8, 11, 0, 8, Block.LOG.id, Block.LOG.id, false);
        this.a(world, structureboundingbox, 3, 0, 1, 3, 0, 7, Block.WATER.id, Block.WATER.id, false);
        this.a(world, structureboundingbox, 9, 0, 1, 9, 0, 7, Block.WATER.id, Block.WATER.id, false);

        int i;

        for (i = 1; i <= 7; ++i) {
            this.a(world, this.b, MathHelper.nextInt(random, 2, 7), 1, 1, i, structureboundingbox);
            this.a(world, this.b, MathHelper.nextInt(random, 2, 7), 2, 1, i, structureboundingbox);
            this.a(world, this.c, MathHelper.nextInt(random, 2, 7), 4, 1, i, structureboundingbox);
            this.a(world, this.c, MathHelper.nextInt(random, 2, 7), 5, 1, i, structureboundingbox);
            this.a(world, this.d, MathHelper.nextInt(random, 2, 7), 7, 1, i, structureboundingbox);
            this.a(world, this.d, MathHelper.nextInt(random, 2, 7), 8, 1, i, structureboundingbox);
            this.a(world, this.e, MathHelper.nextInt(random, 2, 7), 10, 1, i, structureboundingbox);
            this.a(world, this.e, MathHelper.nextInt(random, 2, 7), 11, 1, i, structureboundingbox);
        }

        for (i = 0; i < 9; ++i) {
            for (int j = 0; j < 13; ++j) {
                this.b(world, j, 4, i, structureboundingbox);
                this.b(world, Block.DIRT.id, 0, j, -1, i, structureboundingbox);
            }
        }

        return true;
    }
}
