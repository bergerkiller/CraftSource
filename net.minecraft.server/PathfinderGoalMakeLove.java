package net.minecraft.server;

public class PathfinderGoalMakeLove extends PathfinderGoal {

    private final EntityVillager b;
    private EntityVillager c;
    private final World d;
    private int e;
    Village a;

    public PathfinderGoalMakeLove(EntityVillager entityvillager) {
        this.b = entityvillager;
        this.d = entityvillager.world;
        this.a(3);
    }

    public boolean a() {
        if (this.b.getAge() != 0) {
            return false;
        } else if (this.b.getRandom().nextInt(500) != 0) {
            return false;
        } else {
            this.a = this.d.ai().getClosestVillage(new BlockPosition(this.b), 0);
            if (this.a == null) {
                return false;
            } else if (this.f() && this.b.r(true)) {
                Entity entity = this.d.a(EntityVillager.class, this.b.getBoundingBox().grow(8.0D, 3.0D, 8.0D), (Entity) this.b);

                if (entity == null) {
                    return false;
                } else {
                    this.c = (EntityVillager) entity;
                    return this.c.getAge() == 0 && this.c.r(true);
                }
            } else {
                return false;
            }
        }
    }

    public void c() {
        this.e = 300;
        this.b.p(true);
    }

    public void d() {
        this.a = null;
        this.c = null;
        this.b.p(false);
    }

    public boolean b() {
        return this.e >= 0 && this.f() && this.b.getAge() == 0 && this.b.r(false);
    }

    public void e() {
        --this.e;
        this.b.getControllerLook().a(this.c, 10.0F, 30.0F);
        if (this.b.h(this.c) > 2.25D) {
            this.b.getNavigation().a((Entity) this.c, 0.25D);
        } else if (this.e == 0 && this.c.di()) {
            this.i();
        }

        if (this.b.getRandom().nextInt(35) == 0) {
            this.d.broadcastEntityEffect(this.b, (byte) 12);
        }

    }

    private boolean f() {
        if (!this.a.i()) {
            return false;
        } else {
            int i = (int) ((double) ((float) this.a.c()) * 0.35D);

            return this.a.e() < i;
        }
    }

    private void i() {
        EntityVillager entityvillager = this.b.b((EntityAgeable) this.c);
        // CraftBukkit start - call EntityBreedEvent
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityBreedEvent(entityvillager, this.b, this.c, null, null, 0).isCancelled()) {
            return;
        }
        // CraftBukkit end

        this.c.setAgeRaw(6000);
        this.b.setAgeRaw(6000);
        this.c.s(false);
        this.b.s(false);
        entityvillager.setAgeRaw(-24000);
        entityvillager.setPositionRotation(this.b.locX, this.b.locY, this.b.locZ, 0.0F, 0.0F);
        this.d.addEntity(entityvillager, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.BREEDING); // CraftBukkit - added SpawnReason
        this.d.broadcastEntityEffect(entityvillager, (byte) 12);
    }
}
