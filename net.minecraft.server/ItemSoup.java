package net.minecraft.server;

public class ItemSoup extends ItemFood {

    public ItemSoup(int i, int j) {
        super(i, j, false);
        this.d(1);
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        super.b(itemstack, world, entityhuman);
        return new ItemStack(Item.BOWL);
    }
}
