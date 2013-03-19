package net.minecraft.server;

import java.util.Random;

public class BlockBookshelf extends Block {

    public BlockBookshelf(int i) {
        super(i, Material.WOOD);
        this.a(CreativeModeTab.b);
    }

    public int a(Random random) {
        return 3;
    }

    public int getDropType(int i, Random random, int j) {
        return Item.BOOK.id;
    }
}
