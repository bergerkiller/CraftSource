package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
// CraftBukkit start
import java.util.List;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
// CraftBukkit end

public abstract class EntityMinecartContainer extends EntityMinecartAbstract implements ITileInventory, ILootable {

    private NonNullList<ItemStack> items;
    private boolean b;
    private MinecraftKey c;
    private long d;

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public List<ItemStack> getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public InventoryHolder getOwner() {
        org.bukkit.entity.Entity cart = getBukkitEntity();
        if(cart instanceof InventoryHolder) return (InventoryHolder) cart;
        return null;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public Location getLocation() {
        return getBukkitEntity().getLocation();
    }
    // CraftBukkit end

    public EntityMinecartContainer(World world) {
        super(world);
        this.items = NonNullList.a(36, ItemStack.a);
        this.b = true;
    }

    public EntityMinecartContainer(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        this.items = NonNullList.a(36, ItemStack.a);
        this.b = true;
    }

    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            InventoryUtils.dropEntity(this.world, this, this);
        }

    }

    public boolean w_() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public ItemStack getItem(int i) {
        this.f((EntityHuman) null);
        return (ItemStack) this.items.get(i);
    }

    public ItemStack splitStack(int i, int j) {
        this.f((EntityHuman) null);
        return ContainerUtil.a(this.items, i, j);
    }

    public ItemStack splitWithoutUpdate(int i) {
        this.f((EntityHuman) null);
        ItemStack itemstack = (ItemStack) this.items.get(i);

        if (itemstack.isEmpty()) {
            return ItemStack.a;
        } else {
            this.items.set(i, ItemStack.a);
            return itemstack;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        this.f((EntityHuman) null);
        this.items.set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

    }

    public void update() {}

    public boolean a(EntityHuman entityhuman) {
        return this.dead ? false : entityhuman.h(this) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    @Nullable
    public Entity c(int i) {
        this.b = false;
        return super.c(i);
    }

    public void die() {
        if (this.b) {
            InventoryUtils.dropEntity(this.world, this, this);
        }

        super.die();
    }

    public void b(boolean flag) {
        this.b = flag;
    }

    public static void b(DataConverterManager dataconvertermanager, Class<?> oclass) {
        EntityMinecartAbstract.a(dataconvertermanager, oclass);
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItemList(oclass, new String[] { "Items"})));
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.c != null) {
            nbttagcompound.setString("LootTable", this.c.toString());
            if (this.d != 0L) {
                nbttagcompound.setLong("LootTableSeed", this.d);
            }
        } else {
            ContainerUtil.a(nbttagcompound, this.items);
        }

    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.a);
        if (nbttagcompound.hasKeyOfType("LootTable", 8)) {
            this.c = new MinecraftKey(nbttagcompound.getString("LootTable"));
            this.d = nbttagcompound.getLong("LootTableSeed");
        } else {
            ContainerUtil.b(nbttagcompound, this.items);
        }

    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (!this.world.isClientSide) {
            entityhuman.openContainer(this);
        }

        return true;
    }

    protected void r() {
        float f = 0.98F;

        if (this.c == null) {
            int i = 15 - Container.b((IInventory) this);

            f += (float) i * 0.001F;
        }

        this.motX *= (double) f;
        this.motY *= 0.0D;
        this.motZ *= (double) f;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public boolean isLocked() {
        return false;
    }

    public void a(ChestLock chestlock) {}

    public ChestLock getLock() {
        return ChestLock.a;
    }

    public void f(@Nullable EntityHuman entityhuman) {
        if (this.c != null) {
            LootTable loottable = this.world.ak().a(this.c);

            this.c = null;
            Random random;

            if (this.d == 0L) {
                random = new Random();
            } else {
                random = new Random(this.d);
            }

            LootTableInfo.a loottableinfo_a = new LootTableInfo.a((WorldServer) this.world);

            if (entityhuman != null) {
                loottableinfo_a.a(entityhuman.dj());
            }

            loottable.a(this, random, loottableinfo_a.a());
        }

    }

    public void clear() {
        this.f((EntityHuman) null);
        this.items.clear();
    }

    public void a(MinecraftKey minecraftkey, long i) {
        this.c = minecraftkey;
        this.d = i;
    }

    public MinecraftKey b() {
        return this.c;
    }
}
