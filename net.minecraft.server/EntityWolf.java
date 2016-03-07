package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.UUID;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
// CraftBukkit end

public class EntityWolf extends EntityTameableAnimal {

    private static final DataWatcherObject<Float> DATA_HEALTH = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Boolean> bA = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> bB = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.b);
    private float bC;
    private float bD;
    private boolean bE;
    private boolean bF;
    private float bG;
    private float bH;

    public EntityWolf(World world) {
        super(world);
        this.setSize(0.6F, 0.85F);
        this.setTamed(false);
    }

    protected void r() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.goalSit = new PathfinderGoalSit(this));
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.a(6, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalBeg(this, 8.0F));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(4, new PathfinderGoalRandomTargetNonTamed(this, EntityAnimal.class, false, new Predicate() {
            public boolean a(Entity entity) {
                return entity instanceof EntitySheep || entity instanceof EntityRabbit;
            }

            public boolean apply(Object object) {
                return this.a((Entity) object);
            }
        }));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget(this, EntitySkeleton.class, false));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        if (this.isTamed()) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        }

        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(2.0D);
    }

    // CraftBukkit - add overriden version
    @Override
    public void setGoalTarget(EntityLiving entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason reason, boolean fire) {
        super.setGoalTarget(entityliving, reason, fire);
        if (entityliving == null) {
            this.setAngry(false);
        } else if (!this.isTamed()) {
            this.setAngry(true);
        }
    }
    // CraftBukkit end

    public void setGoalTarget(EntityLiving entityliving) {
        super.setGoalTarget(entityliving);
        if (entityliving == null) {
            this.setAngry(false);
        } else if (!this.isTamed()) {
            this.setAngry(true);
        }

    }

    protected void M() {
        this.datawatcher.set(EntityWolf.DATA_HEALTH, Float.valueOf(this.getHealth()));
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityWolf.DATA_HEALTH, Float.valueOf(this.getHealth()));
        this.datawatcher.register(EntityWolf.bA, Boolean.valueOf(false));
        this.datawatcher.register(EntityWolf.bB, Integer.valueOf(EnumColor.RED.getInvColorIndex()));
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.gR, 0.15F, 1.0F);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Angry", this.isAngry());
        nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getInvColorIndex());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAngry(nbttagcompound.getBoolean("Angry"));
        if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
            this.setCollarColor(EnumColor.fromInvColorIndex(nbttagcompound.getByte("CollarColor")));
        }

    }

    protected SoundEffect G() {
        return this.isAngry() ? SoundEffects.gM : (this.random.nextInt(3) == 0 ? (this.isTamed() && ((Float) this.datawatcher.get(EntityWolf.DATA_HEALTH)).floatValue() < 10.0F ? SoundEffects.gS : SoundEffects.gP) : SoundEffects.gK);
    }

    protected SoundEffect bR() {
        return SoundEffects.gO;
    }

    protected SoundEffect bS() {
        return SoundEffects.gL;
    }

    protected float cd() {
        return 0.4F;
    }

    protected MinecraftKey J() {
        return LootTables.I;
    }

    public void n() {
        super.n();
        if (!this.world.isClientSide && this.bE && !this.bF && !this.cT() && this.onGround) {
            this.bF = true;
            this.bG = 0.0F;
            this.bH = 0.0F;
            this.world.broadcastEntityEffect(this, (byte) 8);
        }

        if (!this.world.isClientSide && this.getGoalTarget() == null && this.isAngry()) {
            this.setAngry(false);
        }

    }

    public void m() {
        super.m();
        this.bD = this.bC;
        if (this.dl()) {
            this.bC += (1.0F - this.bC) * 0.4F;
        } else {
            this.bC += (0.0F - this.bC) * 0.4F;
        }

        if (this.ah()) {
            this.bE = true;
            this.bF = false;
            this.bG = 0.0F;
            this.bH = 0.0F;
        } else if ((this.bE || this.bF) && this.bF) {
            if (this.bG == 0.0F) {
                this.a(SoundEffects.gQ, this.cd(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.bH = this.bG;
            this.bG += 0.05F;
            if (this.bH >= 2.0F) {
                this.bE = false;
                this.bF = false;
                this.bH = 0.0F;
                this.bG = 0.0F;
            }

            if (this.bG > 0.4F) {
                float f = (float) this.getBoundingBox().b;
                int i = (int) (MathHelper.sin((this.bG - 0.4F) * 3.1415927F) * 7.0F);

                for (int j = 0; j < i; ++j) {
                    float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;

                    this.world.addParticle(EnumParticle.WATER_SPLASH, this.locX + (double) f1, (double) (f + 0.8F), this.locZ + (double) f2, this.motX, this.motY, this.motZ, new int[0]);
                }
            }
        }

    }

    public float getHeadHeight() {
        return this.length * 0.8F;
    }

    public int N() {
        return this.isSitting() ? 20 : super.N();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            if (this.goalSit != null) {
                // CraftBukkit - moved into EntityLiving.d(DamageSource, float)
                // PAIL : checkme
                // this.goalSit.setSitting(false);
            }
            if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
                f = (f + 1.0F) / 2.0F;
            }

            return super.damageEntity(damagesource, f);
        }
    }

    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public void setTamed(boolean flag) {
        super.setTamed(flag);
        if (flag) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        }

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack) {
        if (this.isTamed()) {
            if (itemstack != null) {
                if (itemstack.getItem() instanceof ItemFood) {
                    ItemFood itemfood = (ItemFood) itemstack.getItem();

                    if (itemfood.g() && ((Float) this.datawatcher.get(EntityWolf.DATA_HEALTH)).floatValue() < 20.0F) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            --itemstack.count;
                        }

                        this.heal((float) itemfood.getNutrition(itemstack), org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.EATING); // CraftBukkit
                        return true;
                    }
                } else if (itemstack.getItem() == Items.DYE) {
                    EnumColor enumcolor = EnumColor.fromInvColorIndex(itemstack.getData());

                    if (enumcolor != this.getCollarColor()) {
                        this.setCollarColor(enumcolor);
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            --itemstack.count;
                        }

                        return true;
                    }
                }
            }

            if (this.d((EntityLiving) entityhuman) && !this.world.isClientSide && !this.e(itemstack)) {
                this.goalSit.setSitting(!this.isSitting());
                this.bc = false;
                this.navigation.o();
                this.setGoalTarget((EntityLiving) null, TargetReason.FORGOT_TARGET, true); // CraftBukkit - reason
            }
        } else if (itemstack != null && itemstack.getItem() == Items.BONE && !this.isAngry()) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                --itemstack.count;
            }

            if (!this.world.isClientSide) {
                // CraftBukkit - added event call and isCancelled check.
                if (this.random.nextInt(3) == 0 && !CraftEventFactory.callEntityTameEvent(this, entityhuman).isCancelled()) {
                    this.setTamed(true);
                    this.navigation.o();
                    this.setGoalTarget((EntityLiving) null);
                    this.goalSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.setHealth(this.getMaxHealth()); // CraftBukkit - 20.0 -> getMaxHealth()
                    this.setOwnerUUID(entityhuman.getUniqueID());
                    this.o(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.o(false);
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }
            }

            return true;
        }

        return super.a(entityhuman, enumhand, itemstack);
    }

    public boolean e(ItemStack itemstack) {
        return itemstack == null ? false : (!(itemstack.getItem() instanceof ItemFood) ? false : ((ItemFood) itemstack.getItem()).g());
    }

    public int cJ() {
        return 8;
    }

    public boolean isAngry() {
        return (((Byte) this.datawatcher.get(EntityWolf.bv)).byteValue() & 2) != 0;
    }

    public void setAngry(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityWolf.bv)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityWolf.bv, Byte.valueOf((byte) (b0 | 2)));
        } else {
            this.datawatcher.set(EntityWolf.bv, Byte.valueOf((byte) (b0 & -3)));
        }

    }

    public EnumColor getCollarColor() {
        return EnumColor.fromInvColorIndex(((Integer) this.datawatcher.get(EntityWolf.bB)).intValue() & 15);
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.datawatcher.set(EntityWolf.bB, Integer.valueOf(enumcolor.getInvColorIndex()));
    }

    public EntityWolf b(EntityAgeable entityageable) {
        EntityWolf entitywolf = new EntityWolf(this.world);
        UUID uuid = this.getOwnerUUID();

        if (uuid != null) {
            entitywolf.setOwnerUUID(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    public void s(boolean flag) {
        this.datawatcher.set(EntityWolf.bA, Boolean.valueOf(flag));
    }

    public boolean mate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityWolf)) {
            return false;
        } else {
            EntityWolf entitywolf = (EntityWolf) entityanimal;

            return !entitywolf.isTamed() ? false : (entitywolf.isSitting() ? false : this.isInLove() && entitywolf.isInLove());
        }
    }

    public boolean dl() {
        return ((Boolean) this.datawatcher.get(EntityWolf.bA)).booleanValue();
    }

    protected boolean isTypeNotPersistent() {
        return !this.isTamed() /*&& this.ticksLived > 2400*/; // CraftBukkit
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
            if (entityliving instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entityliving;

                if (entitywolf.isTamed() && entitywolf.getOwner() == entityliving1) {
                    return false;
                }
            }

            return entityliving instanceof EntityHuman && entityliving1 instanceof EntityHuman && !((EntityHuman) entityliving1).a((EntityHuman) entityliving) ? false : !(entityliving instanceof EntityHorse) || !((EntityHorse) entityliving).isTamed();
        } else {
            return false;
        }
    }

    public boolean a(EntityHuman entityhuman) {
        return !this.isAngry() && super.a(entityhuman);
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
