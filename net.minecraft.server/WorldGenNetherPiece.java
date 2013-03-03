package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

abstract class WorldGenNetherPiece extends StructurePiece {

    protected WorldGenNetherPiece(int i) {
        super(i);
    }

    private int a(List list) {
        boolean flag = false;
        int i = 0;

        WorldGenNetherPieceWeight worldgennetherpieceweight;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += worldgennetherpieceweight.b) {
            worldgennetherpieceweight = (WorldGenNetherPieceWeight) iterator.next();
            if (worldgennetherpieceweight.d > 0 && worldgennetherpieceweight.c < worldgennetherpieceweight.d) {
                flag = true;
            }
        }

        return flag ? i : -1;
    }

    private WorldGenNetherPiece a(WorldGenNetherPiece15 worldgennetherpiece15, List list, List list1, Random random, int i, int j, int k, int l, int i1) {
        int j1 = this.a(list);
        boolean flag = j1 > 0 && i1 <= 30;
        int k1 = 0;

        while (k1 < 5 && flag) {
            ++k1;
            int l1 = random.nextInt(j1);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                WorldGenNetherPieceWeight worldgennetherpieceweight = (WorldGenNetherPieceWeight) iterator.next();

                l1 -= worldgennetherpieceweight.b;
                if (l1 < 0) {
                    if (!worldgennetherpieceweight.a(i1) || worldgennetherpieceweight == worldgennetherpiece15.a && !worldgennetherpieceweight.e) {
                        break;
                    }

                    WorldGenNetherPiece worldgennetherpiece = WorldGenNetherPieces.a(worldgennetherpieceweight, list1, random, i, j, k, l, i1);

                    if (worldgennetherpiece != null) {
                        ++worldgennetherpieceweight.c;
                        worldgennetherpiece15.a = worldgennetherpieceweight;
                        if (!worldgennetherpieceweight.a()) {
                            list.remove(worldgennetherpieceweight);
                        }

                        return worldgennetherpiece;
                    }
                }
            }
        }

        return WorldGenNetherPiece2.a(list1, random, i, j, k, l, i1);
    }

    private StructurePiece a(WorldGenNetherPiece15 worldgennetherpiece15, List list, Random random, int i, int j, int k, int l, int i1, boolean flag) {
        if (Math.abs(i - worldgennetherpiece15.b().a) <= 112 && Math.abs(k - worldgennetherpiece15.b().c) <= 112) {
            List list1 = worldgennetherpiece15.b;

            if (flag) {
                list1 = worldgennetherpiece15.c;
            }

            WorldGenNetherPiece worldgennetherpiece = this.a(worldgennetherpiece15, list1, list, random, i, j, k, l, i1 + 1);

            if (worldgennetherpiece != null) {
                list.add(worldgennetherpiece);
                worldgennetherpiece15.d.add(worldgennetherpiece);
            }

            return worldgennetherpiece;
        } else {
            return WorldGenNetherPiece2.a(list, random, i, j, k, l, i1);
        }
    }

    protected StructurePiece a(WorldGenNetherPiece15 worldgennetherpiece15, List list, Random random, int i, int j, boolean flag) {
        switch (this.f) {
        case 0:
            return this.a(worldgennetherpiece15, list, random, this.e.a + i, this.e.b + j, this.e.f + 1, this.f, this.c(), flag);

        case 1:
            return this.a(worldgennetherpiece15, list, random, this.e.a - 1, this.e.b + j, this.e.c + i, this.f, this.c(), flag);

        case 2:
            return this.a(worldgennetherpiece15, list, random, this.e.a + i, this.e.b + j, this.e.c - 1, this.f, this.c(), flag);

        case 3:
            return this.a(worldgennetherpiece15, list, random, this.e.d + 1, this.e.b + j, this.e.c + i, this.f, this.c(), flag);

        default:
            return null;
        }
    }

    protected StructurePiece b(WorldGenNetherPiece15 worldgennetherpiece15, List list, Random random, int i, int j, boolean flag) {
        switch (this.f) {
        case 0:
            return this.a(worldgennetherpiece15, list, random, this.e.a - 1, this.e.b + i, this.e.c + j, 1, this.c(), flag);

        case 1:
            return this.a(worldgennetherpiece15, list, random, this.e.a + j, this.e.b + i, this.e.c - 1, 2, this.c(), flag);

        case 2:
            return this.a(worldgennetherpiece15, list, random, this.e.a - 1, this.e.b + i, this.e.c + j, 1, this.c(), flag);

        case 3:
            return this.a(worldgennetherpiece15, list, random, this.e.a + j, this.e.b + i, this.e.c - 1, 2, this.c(), flag);

        default:
            return null;
        }
    }

    protected StructurePiece c(WorldGenNetherPiece15 worldgennetherpiece15, List list, Random random, int i, int j, boolean flag) {
        switch (this.f) {
        case 0:
            return this.a(worldgennetherpiece15, list, random, this.e.d + 1, this.e.b + i, this.e.c + j, 3, this.c(), flag);

        case 1:
            return this.a(worldgennetherpiece15, list, random, this.e.a + j, this.e.b + i, this.e.f + 1, 0, this.c(), flag);

        case 2:
            return this.a(worldgennetherpiece15, list, random, this.e.d + 1, this.e.b + i, this.e.c + j, 3, this.c(), flag);

        case 3:
            return this.a(worldgennetherpiece15, list, random, this.e.a + j, this.e.b + i, this.e.f + 1, 0, this.c(), flag);

        default:
            return null;
        }
    }

    protected static boolean a(StructureBoundingBox structureboundingbox) {
        return structureboundingbox != null && structureboundingbox.b > 10;
    }
}
