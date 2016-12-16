package net.minecraft.server;

public class RecipeMapClone extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class
    public RecipeMapClone() {
        super(new ItemStack(Items.MAP, 0, -1), java.util.Arrays.asList(new ItemStack(Items.MAP, 0, 0)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemstack.isEmpty() && i > 0;
    }

    public ItemStack craftItem(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.a;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return ItemStack.a;
                    }

                    ++i;
                }
            }
        }

        if (!itemstack.isEmpty() && i >= 1) {
            ItemStack itemstack2 = new ItemStack(Items.FILLED_MAP, i + 1, itemstack.getData());

            if (itemstack.hasName()) {
                itemstack2.g(itemstack.getName());
            }

            if (itemstack.hasTag()) {
                itemstack2.setTag(itemstack.getTag());
            }

            return itemstack2;
        } else {
            return ItemStack.a;
        }
    }

    public int a() {
        return 9;
    }

    public ItemStack b() {
        return ItemStack.a;
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (itemstack.getItem().s()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().r()));
            }
        }

        return nonnulllist;
    }
}
