package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EntityTippedArrow extends EntityArrow {

    private static final DataWatcherObject<Integer> f = DataWatcher.a(EntityTippedArrow.class, DataWatcherRegistry.b);
    private PotionRegistry potionRegistry;
    public final Set<MobEffect> effects;

    public EntityTippedArrow(World world) {
        super(world);
        this.potionRegistry = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public EntityTippedArrow(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        this.potionRegistry = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public EntityTippedArrow(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.potionRegistry = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public void a(ItemStack itemstack) {
        if (itemstack.getItem() == Items.TIPPED_ARROW) {
            this.potionRegistry = PotionUtil.c(itemstack.getTag());
            List list = PotionUtil.b(itemstack);

            if (!list.isEmpty()) {
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator.next();

                    this.effects.add(new MobEffect(mobeffect));
                }
            }

            this.o();
        } else if (itemstack.getItem() == Items.ARROW) {
            this.potionRegistry = Potions.EMPTY;
            this.effects.clear();
            this.datawatcher.set(EntityTippedArrow.f, Integer.valueOf(0));
        }

    }

    void o() {
        this.datawatcher.set(EntityTippedArrow.f, Integer.valueOf(PotionUtil.a((Collection) PotionUtil.a(this.potionRegistry, (Collection) this.effects))));
    }

    public void a(MobEffect mobeffect) {
        this.effects.add(mobeffect);
        this.getDataWatcher().set(EntityTippedArrow.f, Integer.valueOf(PotionUtil.a((Collection) PotionUtil.a(this.potionRegistry, (Collection) this.effects))));
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityTippedArrow.f, Integer.valueOf(0));
    }

    public void A_() {
        super.A_();
        if (this.world.isClientSide) {
            if (this.inGround) {
                if (this.b % 5 == 0) {
                    this.b(1);
                }
            } else {
                this.b(2);
            }
        } else if (this.inGround && this.b != 0 && !this.effects.isEmpty() && this.b >= 600) {
            this.world.broadcastEntityEffect(this, (byte) 0);
            this.potionRegistry = Potions.EMPTY;
            this.effects.clear();
            this.datawatcher.set(EntityTippedArrow.f, Integer.valueOf(0));
        }

    }

    private void b(int i) {
        int j = this.q();

        if (j != 0 && i > 0) {
            double d0 = (double) (j >> 16 & 255) / 255.0D;
            double d1 = (double) (j >> 8 & 255) / 255.0D;
            double d2 = (double) (j >> 0 & 255) / 255.0D;

            for (int k = 0; k < i; ++k) {
                this.world.addParticle(EnumParticle.SPELL_MOB, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2, new int[0]);
            }

        }
    }

    // CraftBukkit start accessor methods
    public void refreshEffects() {
        this.getDataWatcher().set(EntityTippedArrow.f, Integer.valueOf(PotionUtil.a((Collection) PotionUtil.a(this.potionRegistry, (Collection) this.effects))));
    }

    public String getType() {
        return ((MinecraftKey) PotionRegistry.a.b(this.potionRegistry)).toString();
    }

    public void setType(String string) {
        this.potionRegistry = PotionRegistry.a.get(new MinecraftKey(string));
        this.datawatcher.set(EntityTippedArrow.f, Integer.valueOf(PotionUtil.a((Collection) PotionUtil.a(this.potionRegistry, (Collection) this.effects))));
    }

    public boolean isTipped() {
        return !(this.effects.isEmpty() && this.potionRegistry == Potions.EMPTY);
    }
    // CraftBukkit end

    public int q() {
        return ((Integer) this.datawatcher.get(EntityTippedArrow.f)).intValue();
    }

    public static void c(DataConverterManager dataconvertermanager) {
        EntityArrow.a(dataconvertermanager, "TippedArrow");
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.potionRegistry != Potions.EMPTY && this.potionRegistry != null) {
            nbttagcompound.setString("Potion", ((MinecraftKey) PotionRegistry.a.b(this.potionRegistry)).toString());
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("CustomPotionEffects", nbttaglist);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Potion", 8)) {
            this.potionRegistry = PotionUtil.c(nbttagcompound);
        }

        Iterator iterator = PotionUtil.b(nbttagcompound).iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            this.a(mobeffect);
        }

        if (this.potionRegistry == Potions.EMPTY) {
            this.datawatcher.set(EntityTippedArrow.f, Integer.valueOf(0));
        } else {
            this.o();
        }

    }

    protected void a(EntityLiving entityliving) {
        super.a(entityliving);
        Iterator iterator = this.potionRegistry.a().iterator();

        MobEffect mobeffect;

        while (iterator.hasNext()) {
            mobeffect = (MobEffect) iterator.next();
            entityliving.addEffect(new MobEffect(mobeffect.getMobEffect(), Math.max(mobeffect.getDuration() / 8, 1), mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles()));
        }

        if (!this.effects.isEmpty()) {
            iterator = this.effects.iterator();

            while (iterator.hasNext()) {
                mobeffect = (MobEffect) iterator.next();
                entityliving.addEffect(mobeffect);
            }
        }

    }

    protected ItemStack j() {
        if (this.effects.isEmpty() && this.potionRegistry == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        } else {
            ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);

            PotionUtil.a(itemstack, this.potionRegistry);
            PotionUtil.a(itemstack, (Collection) this.effects);
            return itemstack;
        }
    }
}
