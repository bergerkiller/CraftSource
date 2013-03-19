package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class WorldGenVillageLight extends WorldGenVillagePiece {

    private int a = -1;

    public WorldGenVillageLight(WorldGenVillageStartPiece worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(worldgenvillagestartpiece, i);
        this.f = j;
        this.e = structureboundingbox;
    }

    public static StructureBoundingBox a(WorldGenVillageStartPiece worldgenvillagestartpiece, List list, Random random, int i, int j, int k, int l) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 3, 4, 2, l);

        return StructurePiece.a(list, structureboundingbox) != null ? null : structureboundingbox;
    }

    public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.a < 0) {
            this.a = this.b(world, structureboundingbox);
            if (this.a < 0) {
                return true;
            }

            this.e.a(0, this.a - this.e.e + 4 - 1, 0);
        }

        this.a(world, structureboundingbox, 0, 0, 0, 2, 3, 1, 0, 0, false);
        this.a(world, Block.FENCE.id, 0, 1, 0, 0, structureboundingbox);
        this.a(world, Block.FENCE.id, 0, 1, 1, 0, structureboundingbox);
        this.a(world, Block.FENCE.id, 0, 1, 2, 0, structureboundingbox);
        this.a(world, Block.WOOL.id, 15, 1, 3, 0, structureboundingbox);
        this.a(world, Block.TORCH.id, 0, 0, 3, 0, structureboundingbox);
        this.a(world, Block.TORCH.id, 0, 1, 3, 1, structureboundingbox);
        this.a(world, Block.TORCH.id, 0, 2, 3, 0, structureboundingbox);
        this.a(world, Block.TORCH.id, 0, 1, 3, -1, structureboundingbox);
        return true;
    }
}
