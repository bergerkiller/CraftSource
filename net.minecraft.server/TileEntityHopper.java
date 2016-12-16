package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
// CraftBukkit end

public class TileEntityHopper extends TileEntityLootable implements IHopper, ITickable {

    private NonNullList<ItemStack> items;
    private int f;
    private long g;

    // CraftBukkit start - add fields and methods
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

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public TileEntityHopper() {
        this.items = NonNullList.a(5, ItemStack.a);
        this.f = -1;
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, (DataInspector) (new DataInspectorItemList(TileEntityHopper.class, new String[] { "Items"})));
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.a);
        if (!this.c(nbttagcompound)) {
            ContainerUtil.b(nbttagcompound, this.items);
        }

        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.o = nbttagcompound.getString("CustomName");
        }

        this.f = nbttagcompound.getInt("TransferCooldown");
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.d(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.items);
        }

        nbttagcompound.setInt("TransferCooldown", this.f);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.o);
        }

        return nbttagcompound;
    }

    public int getSize() {
        return this.items.size();
    }

    public ItemStack splitStack(int i, int j) {
        this.d((EntityHuman) null);
        ItemStack itemstack = ContainerUtil.a(this.q(), i, j);

        return itemstack;
    }

    public void setItem(int i, ItemStack itemstack) {
        this.d((EntityHuman) null);
        this.q().set(i, itemstack);
        if (itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.o : "container.hopper";
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public void F_() {
        if (this.world != null && !this.world.isClientSide) {
            --this.f;
            this.g = this.world.getTime();
            if (!this.J()) {
                this.setCooldown(0);
                this.o();
            }

        }
    }

    private boolean o() {
        if (this.world != null && !this.world.isClientSide) {
            if (!this.J() && BlockHopper.f(this.v())) {
                boolean flag = false;

                if (!this.p()) {
                    flag = this.s();
                }

                if (!this.r()) {
                    flag = a((IHopper) this) || flag;
                }

                if (flag) {
                    this.setCooldown(world.spigotConfig.hopperTransfer); // Spigot
                    this.update();
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private boolean p() {
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

    public boolean w_() {
        return this.p();
    }

    private boolean r() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (!itemstack.isEmpty() && itemstack.getCount() == itemstack.getMaxStackSize());

        return false;
    }

    private boolean s() {
        IInventory iinventory = this.I();

        if (iinventory == null) {
            return false;
        } else {
            EnumDirection enumdirection = BlockHopper.e(this.v()).opposite();

            if (this.a(iinventory, enumdirection)) {
                return false;
            } else {
                for (int i = 0; i < this.getSize(); ++i) {
                    if (!this.getItem(i).isEmpty()) {
                        ItemStack itemstack = this.getItem(i).cloneItemStack();
                        // ItemStack itemstack1 = addItem(this, iinventory, this.splitStack(i, 1), enumdirection);

                        // CraftBukkit start - Call event when pushing items into other inventories
                        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(this.splitStack(i, world.spigotConfig.hopperAmount)); // Spigot

                        Inventory destinationInventory;
                        // Have to special case large chests as they work oddly
                        if (iinventory instanceof InventoryLargeChest) {
                            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
                        } else {
                            destinationInventory = iinventory.getOwner().getInventory();
                        }

                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.getOwner().getInventory(), oitemstack.clone(), destinationInventory, true);
                        this.getWorld().getServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            this.setItem(i, itemstack);
                            this.setCooldown(world.spigotConfig.hopperTransfer); // Spigot
                            return false;
                        }
                        int origCount = event.getItem().getAmount(); // Spigot
                        ItemStack itemstack1 = addItem(this, iinventory, CraftItemStack.asNMSCopy(event.getItem()), enumdirection);

                        if (itemstack1.isEmpty()) {
                            if (event.getItem().equals(oitemstack)) {
                                iinventory.update();
                            } else {
                                this.setItem(i, itemstack);
                            }
                            // CraftBukkit end
                            return true;
                        }

                        itemstack.subtract(origCount - itemstack1.getCount()); // Spigot
                        this.setItem(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private boolean a(IInventory iinventory, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];
                ItemStack itemstack = iworldinventory.getItem(k);

                if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                    return false;
                }
            }
        } else {
            int l = iinventory.getSize();

            for (int i1 = 0; i1 < l; ++i1) {
                ItemStack itemstack1 = iinventory.getItem(i1);

                if (itemstack1.isEmpty() || itemstack1.getCount() != itemstack1.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean b(IInventory iinventory, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!iworldinventory.getItem(k).isEmpty()) {
                    return false;
                }
            }
        } else {
            int l = iinventory.getSize();

            for (int i1 = 0; i1 < l; ++i1) {
                if (!iinventory.getItem(i1).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean a(IHopper ihopper) {
        IInventory iinventory = b(ihopper);

        if (iinventory != null) {
            EnumDirection enumdirection = EnumDirection.DOWN;

            if (b(iinventory, enumdirection)) {
                return false;
            }

            if (iinventory instanceof IWorldInventory) {
                IWorldInventory iworldinventory = (IWorldInventory) iinventory;
                int[] aint = iworldinventory.getSlotsForFace(enumdirection);
                int[] aint1 = aint;
                int i = aint.length;

                for (int j = 0; j < i; ++j) {
                    int k = aint1[j];

                    if (a(ihopper, iinventory, k, enumdirection)) {
                        return true;
                    }
                }
            } else {
                int l = iinventory.getSize();

                for (int i1 = 0; i1 < l; ++i1) {
                    if (a(ihopper, iinventory, i1, enumdirection)) {
                        return true;
                    }
                }
            }
        } else {
            Iterator iterator = a(ihopper.getWorld(), ihopper.E(), ihopper.F(), ihopper.G()).iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (a((IInventory) null, ihopper, entityitem)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean a(IHopper ihopper, IInventory iinventory, int i, EnumDirection enumdirection) {
        ItemStack itemstack = iinventory.getItem(i);

        if (!itemstack.isEmpty() && b(iinventory, itemstack, i, enumdirection)) {
            ItemStack itemstack1 = itemstack.cloneItemStack();
            // ItemStack itemstack2 = addItem(iinventory, ihopper, iinventory.splitStack(i, 1), (EnumDirection) null);
            // CraftBukkit start - Call event on collection of items from inventories into the hopper
            CraftItemStack oitemstack = CraftItemStack.asCraftMirror(iinventory.splitStack(i, ihopper.getWorld().spigotConfig.hopperAmount)); // Spigot

            Inventory sourceInventory;
            // Have to special case large chests as they work oddly
            if (iinventory instanceof InventoryLargeChest) {
                sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
            } else {
                sourceInventory = iinventory.getOwner().getInventory();
            }

            InventoryMoveItemEvent event = new InventoryMoveItemEvent(sourceInventory, oitemstack.clone(), ihopper.getOwner().getInventory(), false);

            ihopper.getWorld().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                iinventory.setItem(i, itemstack1);

                if (ihopper instanceof TileEntityHopper) {
                    ((TileEntityHopper) ihopper).setCooldown(ihopper.getWorld().spigotConfig.hopperTransfer); // Spigot
                } else if (ihopper instanceof EntityMinecartHopper) {
                    ((EntityMinecartHopper) ihopper).setCooldown(ihopper.getWorld().spigotConfig.hopperTransfer / 2); // Spigot
                }
                return false;
            }
            int origCount = event.getItem().getAmount(); // Spigot
            ItemStack itemstack2 = addItem(iinventory, ihopper, CraftItemStack.asNMSCopy(event.getItem()), null);

            if (itemstack2.isEmpty()) {
                if (event.getItem().equals(oitemstack)) {
                    iinventory.update();
                } else {
                    iinventory.setItem(i, itemstack1);
                }
                // CraftBukkit end
                return true;
            }

            itemstack1.subtract(origCount - itemstack2.getCount()); // Spigot
            iinventory.setItem(i, itemstack1);
        }

        return false;
    }

    public static boolean a(IInventory iinventory, IInventory iinventory1, EntityItem entityitem) {
        boolean flag = false;

        if (entityitem == null) {
            return false;
        } else {
            // CraftBukkit start
            InventoryPickupItemEvent event = new InventoryPickupItemEvent(iinventory1.getOwner().getInventory(), (org.bukkit.entity.Item) entityitem.getBukkitEntity());
            entityitem.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end
            ItemStack itemstack = entityitem.getItemStack().cloneItemStack();
            ItemStack itemstack1 = addItem(iinventory, iinventory1, itemstack, (EnumDirection) null);

            if (itemstack1.isEmpty()) {
                flag = true;
                entityitem.die();
            } else {
                entityitem.setItemStack(itemstack1);
            }

            return flag;
        }
    }

    public static ItemStack addItem(IInventory iinventory, IInventory iinventory1, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        if (iinventory1 instanceof IWorldInventory && enumdirection != null) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory1;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);

            for (int i = 0; i < aint.length && !itemstack.isEmpty(); ++i) {
                itemstack = a(iinventory, iinventory1, itemstack, aint[i], enumdirection);
            }
        } else {
            int j = iinventory1.getSize();

            for (int k = 0; k < j && !itemstack.isEmpty(); ++k) {
                itemstack = a(iinventory, iinventory1, itemstack, k, enumdirection);
            }
        }

        return itemstack;
    }

    private static boolean a(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return !iinventory.b(i, itemstack) ? false : !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canPlaceItemThroughFace(i, itemstack, enumdirection);
    }

    private static boolean b(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canTakeItemThroughFace(i, itemstack, enumdirection);
    }

    private static ItemStack a(IInventory iinventory, IInventory iinventory1, ItemStack itemstack, int i, EnumDirection enumdirection) {
        ItemStack itemstack1 = iinventory1.getItem(i);

        if (a(iinventory1, itemstack, i, enumdirection)) {
            boolean flag = false;
            boolean flag1 = iinventory1.w_();

            if (itemstack1.isEmpty()) {
                iinventory1.setItem(i, itemstack);
                itemstack = ItemStack.a;
                flag = true;
            } else if (a(itemstack1, itemstack)) {
                int j = itemstack.getMaxStackSize() - itemstack1.getCount();
                int k = Math.min(itemstack.getCount(), j);

                itemstack.subtract(k);
                itemstack1.add(k);
                flag = k > 0;
            }

            if (flag) {
                if (flag1 && iinventory1 instanceof TileEntityHopper) {
                    TileEntityHopper tileentityhopper = (TileEntityHopper) iinventory1;

                    if (!tileentityhopper.K()) {
                        byte b0 = 0;

                        if (iinventory != null && iinventory instanceof TileEntityHopper) {
                            TileEntityHopper tileentityhopper1 = (TileEntityHopper) iinventory;

                            if (tileentityhopper.g >= tileentityhopper1.g) {
                                b0 = 1;
                            }
                        }

                        tileentityhopper.setCooldown(tileentityhopper.world.spigotConfig.hopperTransfer - b0); // Spigot
                    }
                }

                iinventory1.update();
            }
        }

        return itemstack;
    }

    private IInventory I() {
        EnumDirection enumdirection = BlockHopper.e(this.v());

        return b(this.getWorld(), this.E() + (double) enumdirection.getAdjacentX(), this.F() + (double) enumdirection.getAdjacentY(), this.G() + (double) enumdirection.getAdjacentZ());
    }

    public static IInventory b(IHopper ihopper) {
        return b(ihopper.getWorld(), ihopper.E(), ihopper.F() + 1.0D, ihopper.G());
    }

    public static List<EntityItem> a(World world, double d0, double d1, double d2) {
        return world.a(EntityItem.class, new AxisAlignedBB(d0 - 0.5D, d1, d2 - 0.5D, d0 + 0.5D, d1 + 1.5D, d2 + 0.5D), IEntitySelector.a);
    }

    public static IInventory b(World world, double d0, double d1, double d2) {
        Object object = null;
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        if ( !world.isLoaded( blockposition ) ) return null; // Spigot
        Block block = world.getType(blockposition).getBlock();

        if (block.isTileEntity()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof IInventory) {
                object = (IInventory) tileentity;
                if (object instanceof TileEntityChest && block instanceof BlockChest) {
                    object = ((BlockChest) block).a(world, blockposition, true);
                }
            }
        }

        if (object == null) {
            List list = world.getEntities((Entity) null, new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D), IEntitySelector.c);

            if (!list.isEmpty()) {
                object = (IInventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return (IInventory) object;
    }

    private static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getItem() != itemstack1.getItem() ? false : (itemstack.getData() != itemstack1.getData() ? false : (itemstack.getCount() > itemstack.getMaxStackSize() ? false : ItemStack.equals(itemstack, itemstack1)));
    }

    public double E() {
        return (double) this.position.getX() + 0.5D;
    }

    public double F() {
        return (double) this.position.getY() + 0.5D;
    }

    public double G() {
        return (double) this.position.getZ() + 0.5D;
    }

    private void setCooldown(int i) {
        this.f = i;
    }

    private boolean J() {
        return this.f > 0;
    }

    private boolean K() {
        return this.f > 8;
    }

    public String getContainerName() {
        return "minecraft:hopper";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        this.d(entityhuman);
        return new ContainerHopper(playerinventory, this, entityhuman);
    }

    protected NonNullList<ItemStack> q() {
        return this.items;
    }
}
