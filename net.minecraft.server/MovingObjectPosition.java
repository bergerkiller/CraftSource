package net.minecraft.server;

public class MovingObjectPosition {

    public EnumMovingObjectType type;
    public int b;
    public int c;
    public int d;
    public int face;
    public Vec3D pos;
    public Entity entity;

    public MovingObjectPosition(int i, int j, int k, int l, Vec3D vec3d) {
        this(i, j, k, l, vec3d, true);
    }

    public MovingObjectPosition(int i, int j, int k, int l, Vec3D vec3d, boolean flag) {
        this.type = flag ? EnumMovingObjectType.BLOCK : EnumMovingObjectType.MISS;
        this.b = i;
        this.c = j;
        this.d = k;
        this.face = l;
        this.pos = Vec3D.a(vec3d.a, vec3d.b, vec3d.c);
    }

    public MovingObjectPosition(Entity entity) {
        this(entity, Vec3D.a(entity.locX, entity.locY, entity.locZ));
    }

    public MovingObjectPosition(Entity entity, Vec3D vec3d) {
        this.type = EnumMovingObjectType.ENTITY;
        this.entity = entity;
        this.pos = vec3d;
    }

    public String toString() {
        return "HitResult{type=" + this.type + ", x=" + this.b + ", y=" + this.c + ", z=" + this.d + ", f=" + this.face + ", pos=" + this.pos + ", entity=" + this.entity + '}';
    }
}
