package net.minecraft.server;

import java.util.UUID;

// CraftBukkit start
import org.bukkit.event.entity.EntityUnleashEvent;
// CraftBukkit end

public abstract class EntityCreature extends EntityInsentient {

    public static final UUID bu = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    public static final AttributeModifier bv = (new AttributeModifier(EntityCreature.bu, "Fleeing speed bonus", 2.0D, 2)).a(false);
    private BlockPosition a;
    private float b;
    private float c;

    public EntityCreature(World world) {
        super(world);
        this.a = BlockPosition.ZERO;
        this.b = -1.0F;
        this.c = PathType.WATER.a();
    }

    public float a(BlockPosition blockposition) {
        return 0.0F;
    }

    public boolean cM() {
        return super.cM() && this.a(new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ)) >= 0.0F;
    }

    public boolean da() {
        return !this.navigation.n();
    }

    public boolean db() {
        return this.f(new BlockPosition(this));
    }

    public boolean f(BlockPosition blockposition) {
        return this.b == -1.0F ? true : this.a.n(blockposition) < (double) (this.b * this.b);
    }

    public void a(BlockPosition blockposition, int i) {
        this.a = blockposition;
        this.b = (float) i;
    }

    public BlockPosition dc() {
        return this.a;
    }

    public float dd() {
        return this.b;
    }

    public void de() {
        this.b = -1.0F;
    }

    public boolean df() {
        return this.b != -1.0F;
    }

    protected void cV() {
        super.cV();
        if (this.isLeashed() && this.getLeashHolder() != null && this.getLeashHolder().world == this.world) {
            Entity entity = this.getLeashHolder();

            this.a(new BlockPosition((int) entity.locX, (int) entity.locY, (int) entity.locZ), 5);
            float f = this.g(entity);

            if (this instanceof EntityTameableAnimal && ((EntityTameableAnimal) this).isSitting()) {
                if (f > 10.0F) {
                    this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
                    this.unleash(true, true);
                }

                return;
            }

            this.q(f);
            if (f > 10.0F) {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
                this.unleash(true, true);
                this.goalSelector.c(1);
            } else if (f > 6.0F) {
                double d0 = (entity.locX - this.locX) / (double) f;
                double d1 = (entity.locY - this.locY) / (double) f;
                double d2 = (entity.locZ - this.locZ) / (double) f;

                this.motX += d0 * Math.abs(d0) * 0.4D;
                this.motY += d1 * Math.abs(d1) * 0.4D;
                this.motZ += d2 * Math.abs(d2) * 0.4D;
            } else {
                this.goalSelector.d(1);
                float f1 = 2.0F;
                Vec3D vec3d = (new Vec3D(entity.locX - this.locX, entity.locY - this.locY, entity.locZ - this.locZ)).a().a((double) Math.max(f - 2.0F, 0.0F));

                this.getNavigation().a(this.locX + vec3d.x, this.locY + vec3d.y, this.locZ + vec3d.z, this.dg());
            }
        }

    }

    protected double dg() {
        return 1.0D;
    }

    protected void q(float f) {}
}
