package net.minecraft.server;

import java.util.Random;

public class EnchantmentThorns extends Enchantment {

    public EnchantmentThorns(int i, int j) {
        super(i, j, EnchantmentSlotType.ARMOR_TORSO);
        this.b("thorns");
    }

    public int a(int i) {
        return 10 + 20 * (i - 1);
    }

    public int b(int i) {
        return super.a(i) + 50;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean canEnchant(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemArmor ? true : super.canEnchant(itemstack);
    }

    public static boolean a(int i, Random random) {
        return i <= 0 ? false : random.nextFloat() < 0.15F * (float) i;
    }

    public static int b(int i, Random random) {
        return i > 10 ? i - 10 : 1 + random.nextInt(4);
    }

    public static void a(Entity entity, EntityLiving entityliving, Random random) {
        int i = EnchantmentManager.getThornsEnchantmentLevel(entityliving);
        ItemStack itemstack = EnchantmentManager.a(Enchantment.THORNS, entityliving);

        if (a(i, random)) {
            entity.damageEntity(DamageSource.a(entityliving), b(i, random));
            entity.makeSound("damage.thorns", 0.5F, 1.0F);
            if (itemstack != null) {
                itemstack.damage(3, entityliving);
            }
        } else if (itemstack != null) {
            itemstack.damage(1, entityliving);
        }
    }
}
