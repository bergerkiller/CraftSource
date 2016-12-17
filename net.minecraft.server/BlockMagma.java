package net.minecraft.server;

import java.util.Random;

public class BlockMagma extends Block {

    public BlockMagma() {
        super(Material.STONE);
        this.a(CreativeModeTab.b);
        this.a(0.2F);
        this.a(true);
    }

    public MaterialMapColor r(IBlockData iblockdata) {
        return MaterialMapColor.K;
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof() && entity instanceof EntityLiving && !EnchantmentManager.j((EntityLiving) entity)) {
            org.bukkit.craftbukkit.event.CraftEventFactory.blockDamage = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()); // CraftBukkit
            entity.damageEntity(DamageSource.HOT_FLOOR, 1.0F);
            org.bukkit.craftbukkit.event.CraftEventFactory.blockDamage = null; // CraftBukkit
        }

        super.stepOn(world, blockposition, entity);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.getBlock() == Blocks.WATER || iblockdata1.getBlock() == Blocks.FLOWING_WATER) {
            world.setAir(blockposition1);
            world.a((EntityHuman) null, blockposition, SoundEffects.bH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
            if (world instanceof WorldServer) {
                ((WorldServer) world).a(EnumParticle.SMOKE_LARGE, (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.25D, (double) blockposition1.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D, new int[0]);
            }
        }

    }

    public boolean a(IBlockData iblockdata, Entity entity) {
        return entity.isFireProof();
    }
}
