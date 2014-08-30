package net.minecraft.server;

import java.util.Random;

public class BlockDeadBush extends BlockPlant {

    protected BlockDeadBush() {
        super(Material.REPLACEABLE_PLANT);
        float f = 0.4F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
    }

    protected boolean a(Block block) {
        return block == Blocks.SAND || block == Blocks.HARDENED_CLAY || block == Blocks.STAINED_HARDENED_CLAY || block == Blocks.DIRT;
    }

    public Item getDropType(int i, Random random, int j) {
        return null;
    }

    public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
        if (!world.isStatic && entityhuman.bF() != null && entityhuman.bF().getItem() == Items.SHEARS) {
            entityhuman.a(StatisticList.MINE_BLOCK_COUNT[Block.getId(this)], 1);
            this.a(world, i, j, k, new ItemStack(Blocks.DEAD_BUSH, 1, l));
        } else {
            super.a(world, entityhuman, i, j, k, l);
        }
    }
}
