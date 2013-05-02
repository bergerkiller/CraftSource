package net.minecraft.server;

public class PathfinderGoalTempt extends PathfinderGoal {

    private EntityCreature a;
    private float b;
    private double c;
    private double d;
    private double e;
    private double f;
    private double g;
    private EntityHuman h;
    private int i = 0;
    private boolean j;
    private int k;
    private boolean l;
    private boolean m;

    public PathfinderGoalTempt(EntityCreature entitycreature, float f, int i, boolean flag) {
        this.a = entitycreature;
        this.b = f;
        this.k = i;
        this.l = flag;
        this.a(3);
    }

    public boolean a() {
        if (this.i > 0) {
            --this.i;
            return false;
        } else {
            this.h = this.a.world.findNearbyPlayer(this.a, 10.0D);
            if (this.h == null) {
                return false;
            } else {
                ItemStack itemstack = this.h.cd();

                return itemstack == null ? false : itemstack.id == this.k;
            }
        }
    }

    public boolean b() {
        if (this.l) {
            if (this.a.e(this.h) < 36.0D) {
                if (this.h.e(this.c, this.d, this.e) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double) this.h.pitch - this.f) > 5.0D || Math.abs((double) this.h.yaw - this.g) > 5.0D) {
                    return false;
                }
            } else {
                this.c = this.h.locX;
                this.d = this.h.locY;
                this.e = this.h.locZ;
            }

            this.f = (double) this.h.pitch;
            this.g = (double) this.h.yaw;
        }

        return this.a();
    }

    public void c() {
        this.c = this.h.locX;
        this.d = this.h.locY;
        this.e = this.h.locZ;
        this.j = true;
        this.m = this.a.getNavigation().a();
        this.a.getNavigation().a(false);
    }

    public void d() {
        this.h = null;
        this.a.getNavigation().g();
        this.i = 100;
        this.j = false;
        this.a.getNavigation().a(this.m);
    }

    public void e() {
        this.a.getControllerLook().a(this.h, 30.0F, (float) this.a.bs());
        if (this.a.e(this.h) < 6.25D) {
            this.a.getNavigation().g();
        } else {
            this.a.getNavigation().a((EntityLiving) this.h, this.b);
        }
    }

    public boolean f() {
        return this.j;
    }
}
