package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

// CraftBukkit start
import java.util.HashMap;
import java.util.Map;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
// CraftBukkit end

public abstract class Container {

    public NonNullList<ItemStack> b = NonNullList.a();
    public List<Slot> c = Lists.newArrayList();
    public int windowId;
    private int dragType = -1;
    private int g;
    private final Set<Slot> h = Sets.newHashSet();
    protected List<ICrafting> listeners = Lists.newArrayList();
    private final Set<EntityHuman> i = Sets.newHashSet();
    private int tickCount; // Spigot

    // CraftBukkit start
    public boolean checkReachable = true;
    public abstract InventoryView getBukkitView();
    public void transferTo(Container other, org.bukkit.craftbukkit.entity.CraftHumanEntity player) {
        InventoryView source = this.getBukkitView(), destination = other.getBukkitView();
        ((CraftInventory) source.getTopInventory()).getInventory().onClose(player);
        ((CraftInventory) source.getBottomInventory()).getInventory().onClose(player);
        ((CraftInventory) destination.getTopInventory()).getInventory().onOpen(player);
        ((CraftInventory) destination.getBottomInventory()).getInventory().onOpen(player);
    }
    // CraftBukkit end

    public Container() {}

    protected Slot a(Slot slot) {
        slot.rawSlotIndex = this.c.size();
        this.c.add(slot);
        this.b.add(ItemStack.a);
        return slot;
    }

    public void addSlotListener(ICrafting icrafting) {
        if (this.listeners.contains(icrafting)) {
            throw new IllegalArgumentException("Listener already listening");
        } else {
            this.listeners.add(icrafting);
            icrafting.a(this, this.a());
            this.b();
        }
    }

    public NonNullList<ItemStack> a() {
        NonNullList nonnulllist = NonNullList.a();

        for (int i = 0; i < this.c.size(); ++i) {
            nonnulllist.add(((Slot) this.c.get(i)).getItem());
        }

        return nonnulllist;
    }

    public void b() {
        for (int i = 0; i < this.c.size(); ++i) {
            ItemStack itemstack = ((Slot) this.c.get(i)).getItem();
            ItemStack itemstack1 = (ItemStack) this.b.get(i);

            if (!ItemStack.fastMatches(itemstack1, itemstack) || (tickCount % org.spigotmc.SpigotConfig.itemDirtyTicks == 0 && !ItemStack.matches(itemstack1, itemstack))) { // Spigot
                itemstack1 = itemstack.isEmpty() ? ItemStack.a : itemstack.cloneItemStack();
                this.b.set(i, itemstack1);

                for (int j = 0; j < this.listeners.size(); ++j) {
                    ((ICrafting) this.listeners.get(j)).a(this, i, itemstack1);
                }
            }
        }
        tickCount++; // Spigot

    }

    public boolean a(EntityHuman entityhuman, int i) {
        return false;
    }

    @Nullable
    public Slot getSlot(IInventory iinventory, int i) {
        for (int j = 0; j < this.c.size(); ++j) {
            Slot slot = (Slot) this.c.get(j);

            if (slot.a(iinventory, i)) {
                return slot;
            }
        }

        return null;
    }

    public Slot getSlot(int i) {
        return (Slot) this.c.get(i);
    }

    public ItemStack b(EntityHuman entityhuman, int i) {
        Slot slot = (Slot) this.c.get(i);

        return slot != null ? slot.getItem() : ItemStack.a;
    }

    public ItemStack a(int i, int j, InventoryClickType inventoryclicktype, EntityHuman entityhuman) {
        ItemStack itemstack = ItemStack.a;
        PlayerInventory playerinventory = entityhuman.inventory;
        ItemStack itemstack1;
        int k;
        ItemStack itemstack2;
        int l;

        if (inventoryclicktype == InventoryClickType.QUICK_CRAFT) {
            int i1 = this.g;

            this.g = c(j);
            if ((i1 != 1 || this.g != 2) && i1 != this.g) {
                this.d();
            } else if (playerinventory.getCarried().isEmpty()) {
                this.d();
            } else if (this.g == 0) {
                this.dragType = b(j);
                if (a(this.dragType, entityhuman)) {
                    this.g = 1;
                    this.h.clear();
                } else {
                    this.d();
                }
            } else if (this.g == 1) {
                Slot slot = (Slot) this.c.get(i);

                itemstack1 = playerinventory.getCarried();
                if (slot != null && a(slot, itemstack1, true) && slot.isAllowed(itemstack1) && (this.dragType == 2 || itemstack1.getCount() > this.h.size()) && this.b(slot)) {
                    this.h.add(slot);
                }
            } else if (this.g == 2) {
                if (!this.h.isEmpty()) {
                    itemstack2 = playerinventory.getCarried().cloneItemStack();
                    l = playerinventory.getCarried().getCount();
                    Iterator iterator = this.h.iterator();

                    Map<Integer, ItemStack> draggedSlots = new HashMap<Integer, ItemStack>(); // CraftBukkit - Store slots from drag in map (raw slot id -> new stack)
                    while (iterator.hasNext()) {
                        Slot slot1 = (Slot) iterator.next();
                        ItemStack itemstack3 = playerinventory.getCarried();

                        if (slot1 != null && a(slot1, itemstack3, true) && slot1.isAllowed(itemstack3) && (this.dragType == 2 || itemstack3.getCount() >= this.h.size()) && this.b(slot1)) {
                            ItemStack itemstack4 = itemstack2.cloneItemStack();
                            int j1 = slot1.hasItem() ? slot1.getItem().getCount() : 0;

                            a(this.h, this.dragType, itemstack4, j1);
                            k = Math.min(itemstack4.getMaxStackSize(), slot1.getMaxStackSize(itemstack4));
                            if (itemstack4.getCount() > k) {
                                itemstack4.setCount(k);
                            }

                            l -= itemstack4.getCount() - j1;
                            // slot1.set(itemstack4);
                            draggedSlots.put(slot1.rawSlotIndex, itemstack4); // CraftBukkit - Put in map instead of setting
                        }
                    }

                    // CraftBukkit start - InventoryDragEvent
                    InventoryView view = getBukkitView();
                    org.bukkit.inventory.ItemStack newcursor = CraftItemStack.asCraftMirror(itemstack2);
                    newcursor.setAmount(l);
                    Map<Integer, org.bukkit.inventory.ItemStack> eventmap = new HashMap<Integer, org.bukkit.inventory.ItemStack>();
                    for (Map.Entry<Integer, ItemStack> ditem : draggedSlots.entrySet()) {
                        eventmap.put(ditem.getKey(), CraftItemStack.asBukkitCopy(ditem.getValue()));
                    }

                    // It's essential that we set the cursor to the new value here to prevent item duplication if a plugin closes the inventory.
                    ItemStack oldCursor = playerinventory.getCarried();
                    playerinventory.setCarried(CraftItemStack.asNMSCopy(newcursor));

                    InventoryDragEvent event = new InventoryDragEvent(view, (newcursor.getType() != org.bukkit.Material.AIR ? newcursor : null), CraftItemStack.asBukkitCopy(oldCursor), this.dragType == 1, eventmap);
                    entityhuman.world.getServer().getPluginManager().callEvent(event);

                    // Whether or not a change was made to the inventory that requires an update.
                    boolean needsUpdate = event.getResult() != Result.DEFAULT;

                    if (event.getResult() != Result.DENY) {
                        for (Map.Entry<Integer, ItemStack> dslot : draggedSlots.entrySet()) {
                            view.setItem(dslot.getKey(), CraftItemStack.asBukkitCopy(dslot.getValue()));
                        }
                        // The only time the carried item will be set to null is if the inventory is closed by the server.
                        // If the inventory is closed by the server, then the cursor items are dropped.  This is why we change the cursor early.
                        if (playerinventory.getCarried() != null) {
                            playerinventory.setCarried(CraftItemStack.asNMSCopy(event.getCursor()));
                            needsUpdate = true;
                        }
                    } else {
                        playerinventory.setCarried(oldCursor);
                    }

                    if (needsUpdate && entityhuman instanceof EntityPlayer) {
                        ((EntityPlayer) entityhuman).updateInventory(this);
                    }
                    // CraftBukkit end
                }

                this.d();
            } else {
                this.d();
            }
        } else if (this.g != 0) {
            this.d();
        } else {
            Slot slot2;
            int k1;

            if ((inventoryclicktype == InventoryClickType.PICKUP || inventoryclicktype == InventoryClickType.QUICK_MOVE) && (j == 0 || j == 1)) {
                if (i == -999) {
                    if (!playerinventory.getCarried().isEmpty()) {
                        if (j == 0) {
                            // CraftBukkit start
                            ItemStack carried = playerinventory.getCarried();
                            playerinventory.setCarried(ItemStack.a);
                            entityhuman.drop(carried, true);
                            // CraftBukkit start
                        }

                        if (j == 1) {
                            entityhuman.drop(playerinventory.getCarried().cloneAndSubtract(1), true);
                        }
                    }
                } else if (inventoryclicktype == InventoryClickType.QUICK_MOVE) {
                    if (i < 0) {
                        return ItemStack.a;
                    }

                    slot2 = (Slot) this.c.get(i);
                    if (slot2 != null && slot2.isAllowed(entityhuman)) {
                        itemstack2 = this.b(entityhuman, i);
                        if (!itemstack2.isEmpty()) {
                            Item item = itemstack2.getItem();

                            itemstack = itemstack2.cloneItemStack();
                            if (slot2.getItem().getItem() == item) {
                                this.a(i, j, true, entityhuman);
                            }
                        }
                    }
                } else {
                    if (i < 0) {
                        return ItemStack.a;
                    }

                    slot2 = (Slot) this.c.get(i);
                    if (slot2 != null) {
                        itemstack2 = slot2.getItem();
                        itemstack1 = playerinventory.getCarried();
                        if (!itemstack2.isEmpty()) {
                            itemstack = itemstack2.cloneItemStack();
                        }

                        if (itemstack2.isEmpty()) {
                            if (!itemstack1.isEmpty() && slot2.isAllowed(itemstack1)) {
                                k1 = j == 0 ? itemstack1.getCount() : 1;
                                if (k1 > slot2.getMaxStackSize(itemstack1)) {
                                    k1 = slot2.getMaxStackSize(itemstack1);
                                }

                                slot2.set(itemstack1.cloneAndSubtract(k1));
                            }
                        } else if (slot2.isAllowed(entityhuman)) {
                            if (itemstack1.isEmpty()) {
                                if (itemstack2.isEmpty()) {
                                    slot2.set(ItemStack.a);
                                    playerinventory.setCarried(ItemStack.a);
                                } else {
                                    k1 = j == 0 ? itemstack2.getCount() : (itemstack2.getCount() + 1) / 2;
                                    playerinventory.setCarried(slot2.a(k1));
                                    if (itemstack2.isEmpty()) {
                                        slot2.set(ItemStack.a);
                                    }

                                    slot2.a(entityhuman, playerinventory.getCarried());
                                }
                            } else if (slot2.isAllowed(itemstack1)) {
                                if (itemstack2.getItem() == itemstack1.getItem() && itemstack2.getData() == itemstack1.getData() && ItemStack.equals(itemstack2, itemstack1)) {
                                    k1 = j == 0 ? itemstack1.getCount() : 1;
                                    if (k1 > slot2.getMaxStackSize(itemstack1) - itemstack2.getCount()) {
                                        k1 = slot2.getMaxStackSize(itemstack1) - itemstack2.getCount();
                                    }

                                    if (k1 > itemstack1.getMaxStackSize() - itemstack2.getCount()) {
                                        k1 = itemstack1.getMaxStackSize() - itemstack2.getCount();
                                    }

                                    itemstack1.subtract(k1);
                                    itemstack2.add(k1);
                                } else if (itemstack1.getCount() <= slot2.getMaxStackSize(itemstack1)) {
                                    slot2.set(itemstack1);
                                    playerinventory.setCarried(itemstack2);
                                }
                            } else if (itemstack2.getItem() == itemstack1.getItem() && itemstack1.getMaxStackSize() > 1 && (!itemstack2.usesData() || itemstack2.getData() == itemstack1.getData()) && ItemStack.equals(itemstack2, itemstack1) && !itemstack2.isEmpty()) {
                                k1 = itemstack2.getCount();
                                if (k1 + itemstack1.getCount() <= itemstack1.getMaxStackSize()) {
                                    itemstack1.add(k1);
                                    itemstack2 = slot2.a(k1);
                                    if (itemstack2.isEmpty()) {
                                        slot2.set(ItemStack.a);
                                    }

                                    slot2.a(entityhuman, playerinventory.getCarried());
                                }
                            }
                        }

                        slot2.f();
                        // CraftBukkit start - Make sure the client has the right slot contents
                        if (entityhuman instanceof EntityPlayer && slot2.getMaxStackSize() != 64) {
                            ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutSetSlot(this.windowId, slot2.rawSlotIndex, slot2.getItem()));
                            // Updating a crafting inventory makes the client reset the result slot, have to send it again
                            if (this.getBukkitView().getType() == InventoryType.WORKBENCH || this.getBukkitView().getType() == InventoryType.CRAFTING) {
                                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutSetSlot(this.windowId, 0, this.getSlot(0).getItem()));
                            }
                        }
                        // CraftBukkit end
                    }
                }
            } else if (inventoryclicktype == InventoryClickType.SWAP && j >= 0 && j < 9) {
                slot2 = (Slot) this.c.get(i);
                itemstack2 = playerinventory.getItem(j);
                itemstack1 = slot2.getItem();
                if (!itemstack2.isEmpty() || !itemstack1.isEmpty()) {
                    if (itemstack2.isEmpty()) {
                        if (slot2.isAllowed(entityhuman)) {
                            playerinventory.setItem(j, itemstack1);
                            slot2.b(itemstack1.getCount());
                            slot2.set(ItemStack.a);
                            slot2.a(entityhuman, itemstack1);
                        }
                    } else if (itemstack1.isEmpty()) {
                        if (slot2.isAllowed(itemstack2)) {
                            k1 = slot2.getMaxStackSize(itemstack2);
                            if (itemstack2.getCount() > k1) {
                                slot2.set(itemstack2.cloneAndSubtract(k1));
                            } else {
                                slot2.set(itemstack2);
                                playerinventory.setItem(j, ItemStack.a);
                            }
                        }
                    } else if (slot2.isAllowed(entityhuman) && slot2.isAllowed(itemstack2)) {
                        k1 = slot2.getMaxStackSize(itemstack2);
                        if (itemstack2.getCount() > k1) {
                            slot2.set(itemstack2.cloneAndSubtract(k1));
                            slot2.a(entityhuman, itemstack1);
                            if (!playerinventory.pickup(itemstack1)) {
                                entityhuman.drop(itemstack1, true);
                            }
                        } else {
                            slot2.set(itemstack2);
                            playerinventory.setItem(j, itemstack1);
                            slot2.a(entityhuman, itemstack1);
                        }
                    }
                }
            } else if (inventoryclicktype == InventoryClickType.CLONE && entityhuman.abilities.canInstantlyBuild && playerinventory.getCarried().isEmpty() && i >= 0) {
                slot2 = (Slot) this.c.get(i);
                if (slot2 != null && slot2.hasItem()) {
                    itemstack2 = slot2.getItem().cloneItemStack();
                    itemstack2.setCount(itemstack2.getMaxStackSize());
                    playerinventory.setCarried(itemstack2);
                }
            } else if (inventoryclicktype == InventoryClickType.THROW && playerinventory.getCarried().isEmpty() && i >= 0) {
                slot2 = (Slot) this.c.get(i);
                if (slot2 != null && slot2.hasItem() && slot2.isAllowed(entityhuman)) {
                    itemstack2 = slot2.a(j == 0 ? 1 : slot2.getItem().getCount());
                    slot2.a(entityhuman, itemstack2);
                    entityhuman.drop(itemstack2, true);
                }
            } else if (inventoryclicktype == InventoryClickType.PICKUP_ALL && i >= 0) {
                slot2 = (Slot) this.c.get(i);
                itemstack2 = playerinventory.getCarried();
                if (!itemstack2.isEmpty() && (slot2 == null || !slot2.hasItem() || !slot2.isAllowed(entityhuman))) {
                    l = j == 0 ? 0 : this.c.size() - 1;
                    k1 = j == 0 ? 1 : -1;

                    for (int l1 = 0; l1 < 2; ++l1) {
                        for (int i2 = l; i2 >= 0 && i2 < this.c.size() && itemstack2.getCount() < itemstack2.getMaxStackSize(); i2 += k1) {
                            Slot slot3 = (Slot) this.c.get(i2);

                            if (slot3.hasItem() && a(slot3, itemstack2, true) && slot3.isAllowed(entityhuman) && this.a(itemstack2, slot3)) {
                                ItemStack itemstack5 = slot3.getItem();

                                if (l1 != 0 || itemstack5.getCount() != itemstack5.getMaxStackSize()) {
                                    k = Math.min(itemstack2.getMaxStackSize() - itemstack2.getCount(), itemstack5.getCount());
                                    ItemStack itemstack6 = slot3.a(k);

                                    itemstack2.add(k);
                                    if (itemstack6.isEmpty()) {
                                        slot3.set(ItemStack.a);
                                    }

                                    slot3.a(entityhuman, itemstack6);
                                }
                            }
                        }
                    }
                }

                this.b();
            }
        }

        return itemstack;
    }

    public boolean a(ItemStack itemstack, Slot slot) {
        return true;
    }

    protected void a(int i, int j, boolean flag, EntityHuman entityhuman) {
        this.a(i, j, InventoryClickType.QUICK_MOVE, entityhuman);
    }

    public void b(EntityHuman entityhuman) {
        PlayerInventory playerinventory = entityhuman.inventory;

        if (!playerinventory.getCarried().isEmpty()) {
            entityhuman.drop(playerinventory.getCarried(), false);
            playerinventory.setCarried(ItemStack.a);
        }

    }

    public void a(IInventory iinventory) {
        this.b();
    }

    public void setItem(int i, ItemStack itemstack) {
        this.getSlot(i).set(itemstack);
    }

    public boolean c(EntityHuman entityhuman) {
        return !this.i.contains(entityhuman);
    }

    public void a(EntityHuman entityhuman, boolean flag) {
        if (flag) {
            this.i.remove(entityhuman);
        } else {
            this.i.add(entityhuman);
        }

    }

    public abstract boolean a(EntityHuman entityhuman);

    protected boolean a(ItemStack itemstack, int i, int j, boolean flag) {
        boolean flag1 = false;
        int k = i;

        if (flag) {
            k = j - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (itemstack.isStackable()) {
            while (!itemstack.isEmpty()) {
                if (flag) {
                    if (k < i) {
                        break;
                    }
                } else if (k >= j) {
                    break;
                }

                slot = (Slot) this.c.get(k);
                itemstack1 = slot.getItem();
                if (!itemstack1.isEmpty() && itemstack1.getItem() == itemstack.getItem() && (!itemstack.usesData() || itemstack.getData() == itemstack1.getData()) && ItemStack.equals(itemstack, itemstack1)) {
                    int l = itemstack1.getCount() + itemstack.getCount();

                    if (l <= itemstack.getMaxStackSize()) {
                        itemstack.setCount(0);
                        itemstack1.setCount(l);
                        slot.f();
                        flag1 = true;
                    } else if (itemstack1.getCount() < itemstack.getMaxStackSize()) {
                        itemstack.subtract(itemstack.getMaxStackSize() - itemstack1.getCount());
                        itemstack1.setCount(itemstack.getMaxStackSize());
                        slot.f();
                        flag1 = true;
                    }
                }

                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        if (!itemstack.isEmpty()) {
            if (flag) {
                k = j - 1;
            } else {
                k = i;
            }

            while (true) {
                if (flag) {
                    if (k < i) {
                        break;
                    }
                } else if (k >= j) {
                    break;
                }

                slot = (Slot) this.c.get(k);
                itemstack1 = slot.getItem();
                if (itemstack1.isEmpty() && slot.isAllowed(itemstack)) {
                    if (itemstack.getCount() > slot.getMaxStackSize()) {
                        slot.set(itemstack.cloneAndSubtract(slot.getMaxStackSize()));
                    } else {
                        slot.set(itemstack.cloneAndSubtract(itemstack.getCount()));
                    }

                    slot.f();
                    flag1 = true;
                    break;
                }

                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        return flag1;
    }

    public static int b(int i) {
        return i >> 2 & 3;
    }

    public static int c(int i) {
        return i & 3;
    }

    public static boolean a(int i, EntityHuman entityhuman) {
        return i == 0 ? true : (i == 1 ? true : i == 2 && entityhuman.abilities.canInstantlyBuild);
    }

    protected void d() {
        this.g = 0;
        this.h.clear();
    }

    public static boolean a(@Nullable Slot slot, ItemStack itemstack, boolean flag) {
        boolean flag1 = slot == null || !slot.hasItem();

        return !flag1 && itemstack.doMaterialsMatch(slot.getItem()) && ItemStack.equals(slot.getItem(), itemstack) ? slot.getItem().getCount() + (flag ? 0 : itemstack.getCount()) <= itemstack.getMaxStackSize() : flag1;
    }

    public static void a(Set<Slot> set, int i, ItemStack itemstack, int j) {
        switch (i) {
        case 0:
            itemstack.setCount(MathHelper.d((float) itemstack.getCount() / (float) set.size()));
            break;

        case 1:
            itemstack.setCount(1);
            break;

        case 2:
            itemstack.setCount(itemstack.getItem().getMaxStackSize());
        }

        itemstack.add(j);
    }

    public boolean b(Slot slot) {
        return true;
    }

    public static int a(@Nullable TileEntity tileentity) {
        return tileentity instanceof IInventory ? b((IInventory) tileentity) : 0;
    }

    public static int b(@Nullable IInventory iinventory) {
        if (iinventory == null) {
            return 0;
        } else {
            int i = 0;
            float f = 0.0F;

            for (int j = 0; j < iinventory.getSize(); ++j) {
                ItemStack itemstack = iinventory.getItem(j);

                if (!itemstack.isEmpty()) {
                    f += (float) itemstack.getCount() / (float) Math.min(iinventory.getMaxStackSize(), itemstack.getMaxStackSize());
                    ++i;
                }
            }

            f /= (float) iinventory.getSize();
            return MathHelper.d(f * 14.0F) + (i > 0 ? 1 : 0);
        }
    }
}
