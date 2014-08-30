package net.minecraft.server;

import java.util.Random;

public class RandomPositionGenerator {

    private static Vec3D a = Vec3D.a(0.0D, 0.0D, 0.0D);

    public static Vec3D a(EntityCreature entitycreature, int i, int j) {
        return c(entitycreature, i, j, (Vec3D) null);
    }

    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        a.a = vec3d.a - entitycreature.locX;
        a.b = vec3d.b - entitycreature.locY;
        a.c = vec3d.c - entitycreature.locZ;
        return c(entitycreature, i, j, a);
    }

    public static Vec3D b(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        a.a = entitycreature.locX - vec3d.a;
        a.b = entitycreature.locY - vec3d.b;
        a.c = entitycreature.locZ - vec3d.c;
        return c(entitycreature, i, j, a);
    }

    private static Vec3D c(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Random random = entitycreature.aI();
        boolean flag = false;
        int k = 0;
        int l = 0;
        int i1 = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entitycreature.bY()) {
            double d0 = (double) (entitycreature.bV().e(MathHelper.floor(entitycreature.locX), MathHelper.floor(entitycreature.locY), MathHelper.floor(entitycreature.locZ)) + 4.0F);
            double d1 = (double) (entitycreature.bW() + (float) i);

            flag1 = d0 < d1 * d1;
        } else {
            flag1 = false;
        }

        for (int j1 = 0; j1 < 10; ++j1) {
            int k1 = random.nextInt(2 * i) - i;
            int l1 = random.nextInt(2 * j) - j;
            int i2 = random.nextInt(2 * i) - i;

            if (vec3d == null || (double) k1 * vec3d.a + (double) i2 * vec3d.c >= 0.0D) {
                k1 += MathHelper.floor(entitycreature.locX);
                l1 += MathHelper.floor(entitycreature.locY);
                i2 += MathHelper.floor(entitycreature.locZ);
                if (!flag1 || entitycreature.b(k1, l1, i2)) {
                    float f1 = entitycreature.a(k1, l1, i2);

                    if (f1 > f) {
                        f = f1;
                        k = k1;
                        l = l1;
                        i1 = i2;
                        flag = true;
                    }
                }
            }
        }

        if (flag) {
            return Vec3D.a((double) k, (double) l, (double) i1);
        } else {
            return null;
        }
    }
}
