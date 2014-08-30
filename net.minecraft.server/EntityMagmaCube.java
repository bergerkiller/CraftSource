package net.minecraft.server;

public class EntityMagmaCube extends EntitySlime {

    public EntityMagmaCube(World world) {
        super(world);
        this.fireProof = true;
    }

    protected void aD() {
        super.aD();
        this.getAttributeInstance(GenericAttributes.d).setValue(0.20000000298023224D);
    }

    public boolean canSpawn() {
        return this.world.difficulty != EnumDifficulty.PEACEFUL && this.world.b(this.boundingBox) && this.world.getCubes(this, this.boundingBox).isEmpty() && !this.world.containsLiquid(this.boundingBox);
    }

    public int aV() {
        return this.getSize() * 3;
    }

    public float d(float f) {
        return 1.0F;
    }

    protected String bP() {
        return "flame";
    }

    protected EntitySlime bQ() {
        return new EntityMagmaCube(this.world);
    }

    protected Item getLoot() {
        return Items.MAGMA_CREAM;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        Item item = this.getLoot();

        if (item != null && this.getSize() > 1) {
            int j = this.random.nextInt(4) - 2;

            if (i > 0) {
                j += this.random.nextInt(i + 1);
            }

            for (int k = 0; k < j; ++k) {
                this.a(item, 1);
            }
        }
    }

    public boolean isBurning() {
        return false;
    }

    protected int bR() {
        return super.bR() * 4;
    }

    protected void bS() {
        this.h *= 0.9F;
    }

    protected void bj() {
        this.motY = (double) (0.42F + (float) this.getSize() * 0.1F);
        this.al = true;
    }

    protected void b(float f) {}

    protected boolean bT() {
        return true;
    }

    protected int bU() {
        return super.bU() + 2;
    }

    protected String bV() {
        return this.getSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
    }

    public boolean P() {
        return false;
    }

    protected boolean bW() {
        return true;
    }
}
