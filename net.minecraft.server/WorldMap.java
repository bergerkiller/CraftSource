package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

// CraftBukkit start
import java.util.UUID;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.map.CraftMapView;
// CraftBukkit end

public class WorldMap extends PersistentBase {

    public int centerX;
    public int centerZ;
    public byte map;
    public boolean track;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors = new byte[16384];
    public List<WorldMap.WorldMapHumanTracker> i = Lists.newArrayList();
    public final Map<EntityHuman, WorldMap.WorldMapHumanTracker> k = Maps.newHashMap(); // Spigot private -> public
    public Map<UUID, MapIcon> decorations = Maps.newLinkedHashMap(); // Spigot

    // CraftBukkit start
    public final CraftMapView mapView;
    private CraftServer server;
    private UUID uniqueId = null;
    // CraftBukkit end

    public WorldMap(String s) {
        super(s);
        // CraftBukkit start
        mapView = new CraftMapView(this);
        server = (CraftServer) org.bukkit.Bukkit.getServer();
        // CraftBukkit end
    }

    public void a(double d0, double d1, int i) {
        int j = 128 * (1 << i);
        int k = MathHelper.floor((d0 + 64.0D) / (double) j);
        int l = MathHelper.floor((d1 + 64.0D) / (double) j);

        this.centerX = k * j + j / 2 - 64;
        this.centerZ = l * j + j / 2 - 64;
    }

    public void a(NBTTagCompound nbttagcompound) {
        // CraftBukkit start
        byte dimension = nbttagcompound.getByte("dimension");

        if (dimension >= 10) {
            long least = nbttagcompound.getLong("UUIDLeast");
            long most = nbttagcompound.getLong("UUIDMost");

            if (least != 0L && most != 0L) {
                this.uniqueId = new UUID(most, least);

                CraftWorld world = (CraftWorld) server.getWorld(this.uniqueId);
                // Check if the stored world details are correct.
                if (world == null) {
                    /* All Maps which do not have their valid world loaded are set to a dimension which hopefully won't be reached.
                       This is to prevent them being corrupted with the wrong map data. */
                    dimension = 127;
                } else {
                    dimension = (byte) world.getHandle().dimension;
                }
            }
        }

        this.map = dimension;
        // CraftBukkit end
        this.centerX = nbttagcompound.getInt("xCenter");
        this.centerZ = nbttagcompound.getInt("zCenter");
        this.scale = nbttagcompound.getByte("scale");
        this.scale = (byte) MathHelper.clamp(this.scale, 0, 4);
        if (nbttagcompound.hasKeyOfType("trackingPosition", 1)) {
            this.track = nbttagcompound.getBoolean("trackingPosition");
        } else {
            this.track = true;
        }

        this.unlimitedTracking = nbttagcompound.getBoolean("unlimitedTracking");
        short short0 = nbttagcompound.getShort("width");
        short short1 = nbttagcompound.getShort("height");

        if (short0 == 128 && short1 == 128) {
            this.colors = nbttagcompound.getByteArray("colors");
        } else {
            byte[] abyte = nbttagcompound.getByteArray("colors");

            this.colors = new byte[16384];
            int i = (128 - short0) / 2;
            int j = (128 - short1) / 2;

            for (int k = 0; k < short1; ++k) {
                int l = k + j;

                if (l >= 0 || l < 128) {
                    for (int i1 = 0; i1 < short0; ++i1) {
                        int j1 = i1 + i;

                        if (j1 >= 0 || j1 < 128) {
                            this.colors[j1 + l * 128] = abyte[i1 + k * short0];
                        }
                    }
                }
            }
        }

    }

    public NBTTagCompound b(NBTTagCompound nbttagcompound) {
        // CraftBukkit start
        if (this.map >= 10) {
            if (this.uniqueId == null) {
                for (org.bukkit.World world : server.getWorlds()) {
                    CraftWorld cWorld = (CraftWorld) world;
                    if (cWorld.getHandle().dimension == this.map) {
                        this.uniqueId = cWorld.getUID();
                        break;
                    }
                }
            }
            /* Perform a second check to see if a matching world was found, this is a necessary
               change incase Maps are forcefully unlinked from a World and lack a UID.*/
            if (this.uniqueId != null) {
                nbttagcompound.setLong("UUIDLeast", this.uniqueId.getLeastSignificantBits());
                nbttagcompound.setLong("UUIDMost", this.uniqueId.getMostSignificantBits());
            }
        }
        // CraftBukkit end
        nbttagcompound.setByte("dimension", this.map);
        nbttagcompound.setInt("xCenter", this.centerX);
        nbttagcompound.setInt("zCenter", this.centerZ);
        nbttagcompound.setByte("scale", this.scale);
        nbttagcompound.setShort("width", (short) 128);
        nbttagcompound.setShort("height", (short) 128);
        nbttagcompound.setByteArray("colors", this.colors);
        nbttagcompound.setBoolean("trackingPosition", this.track);
        nbttagcompound.setBoolean("unlimitedTracking", this.unlimitedTracking);
        return nbttagcompound;
    }

    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        if (!this.k.containsKey(entityhuman)) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = new WorldMap.WorldMapHumanTracker(entityhuman);

            this.k.put(entityhuman, worldmap_worldmaphumantracker);
            this.i.add(worldmap_worldmaphumantracker);
        }

        if (!entityhuman.inventory.f(itemstack)) {
            this.decorations.remove(entityhuman.getUniqueID()); // Spigot
        }

        for (int i = 0; i < this.i.size(); ++i) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker1 = (WorldMap.WorldMapHumanTracker) this.i.get(i);

            if (!worldmap_worldmaphumantracker1.trackee.dead && (worldmap_worldmaphumantracker1.trackee.inventory.f(itemstack) || itemstack.z())) {
                if (!itemstack.z() && worldmap_worldmaphumantracker1.trackee.dimension == this.map && this.track) {
                    this.a(MapIcon.Type.PLAYER, worldmap_worldmaphumantracker1.trackee.world, worldmap_worldmaphumantracker1.trackee.getUniqueID(), worldmap_worldmaphumantracker1.trackee.locX, worldmap_worldmaphumantracker1.trackee.locZ, (double) worldmap_worldmaphumantracker1.trackee.yaw); // Spigot
                }
            } else {
                this.k.remove(worldmap_worldmaphumantracker1.trackee);
                this.i.remove(worldmap_worldmaphumantracker1);
            }
        }

        if (itemstack.z() && this.track) {
            EntityItemFrame entityitemframe = itemstack.A();
            BlockPosition blockposition = entityitemframe.getBlockPosition();

            this.a(MapIcon.Type.FRAME, entityhuman.world, UUID.nameUUIDFromBytes(("frame-" + entityitemframe.getId()).getBytes(Charsets.US_ASCII)), (double) blockposition.getX(), (double) blockposition.getZ(), (double) (entityitemframe.direction.get2DRotationValue() * 90)); // Spigot
        }

        if (itemstack.hasTag() && itemstack.getTag().hasKeyOfType("Decorations", 9)) {
            NBTTagList nbttaglist = itemstack.getTag().getList("Decorations", 10);

            for (int j = 0; j < nbttaglist.size(); ++j) {
                NBTTagCompound nbttagcompound = nbttaglist.get(j);

                // Spigot - start
                UUID uuid = UUID.nameUUIDFromBytes(nbttagcompound.getString("id").getBytes(Charsets.US_ASCII));
                if (!this.decorations.containsKey(uuid)) {
                    this.a(MapIcon.Type.a(nbttagcompound.getByte("type")), entityhuman.world, uuid, nbttagcompound.getDouble("x"), nbttagcompound.getDouble("z"), nbttagcompound.getDouble("rot"));
                    // Spigot - end
                }
            }
        }

    }

    public static void a(ItemStack itemstack, BlockPosition blockposition, String s, MapIcon.Type mapicon_type) {
        NBTTagList nbttaglist;

        if (itemstack.hasTag() && itemstack.getTag().hasKeyOfType("Decorations", 9)) {
            nbttaglist = itemstack.getTag().getList("Decorations", 10);
        } else {
            nbttaglist = new NBTTagList();
            itemstack.a("Decorations", (NBTBase) nbttaglist);
        }

        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setByte("type", mapicon_type.a());
        nbttagcompound.setString("id", s);
        nbttagcompound.setDouble("x", (double) blockposition.getX());
        nbttagcompound.setDouble("z", (double) blockposition.getZ());
        nbttagcompound.setDouble("rot", 180.0D);
        nbttaglist.add(nbttagcompound);
        if (mapicon_type.c()) {
            NBTTagCompound nbttagcompound1 = itemstack.c("display");

            nbttagcompound1.setInt("MapColor", mapicon_type.d());
        }

    }

    private void a(MapIcon.Type mapicon_type, World world, UUID s, double d0, double d1, double d2) { // Spigot; string->uuid
        int i = 1 << this.scale;
        float f = (float) (d0 - (double) this.centerX) / (float) i;
        float f1 = (float) (d1 - (double) this.centerZ) / (float) i;
        byte b0 = (byte) ((int) ((double) (f * 2.0F) + 0.5D));
        byte b1 = (byte) ((int) ((double) (f1 * 2.0F) + 0.5D));
        boolean flag = true;
        byte b2;

        if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
            d2 += d2 < 0.0D ? -8.0D : 8.0D;
            b2 = (byte) ((int) (d2 * 16.0D / 360.0D));
            if (this.map < 0) {
                int j = (int) (world.getWorldData().getDayTime() / 10L);

                b2 = (byte) (j * j * 34187121 + j * 121 >> 15 & 15);
            }
        } else {
            if (mapicon_type != MapIcon.Type.PLAYER) {
                this.decorations.remove(s);
                return;
            }

            boolean flag1 = true;

            if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
                mapicon_type = MapIcon.Type.PLAYER_OFF_MAP;
            } else {
                if (!this.unlimitedTracking) {
                    this.decorations.remove(s);
                    return;
                }

                mapicon_type = MapIcon.Type.PLAYER_OFF_LIMITS;
            }

            b2 = 0;
            if (f <= -63.0F) {
                b0 = -128;
            }

            if (f1 <= -63.0F) {
                b1 = -128;
            }

            if (f >= 63.0F) {
                b0 = 127;
            }

            if (f1 >= 63.0F) {
                b1 = 127;
            }
        }

        this.decorations.put(s, new MapIcon(mapicon_type, b0, b1, b2));
    }

    @Nullable
    public Packet<?> a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) this.k.get(entityhuman);

        return worldmap_worldmaphumantracker == null ? null : worldmap_worldmaphumantracker.a(itemstack);
    }

    public void flagDirty(int i, int j) {
        super.c();
        Iterator iterator = this.i.iterator();

        while (iterator.hasNext()) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) iterator.next();

            worldmap_worldmaphumantracker.a(i, j);
        }

    }

    public WorldMap.WorldMapHumanTracker a(EntityHuman entityhuman) {
        WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) this.k.get(entityhuman);

        if (worldmap_worldmaphumantracker == null) {
            worldmap_worldmaphumantracker = new WorldMap.WorldMapHumanTracker(entityhuman);
            this.k.put(entityhuman, worldmap_worldmaphumantracker);
            this.i.add(worldmap_worldmaphumantracker);
        }

        return worldmap_worldmaphumantracker;
    }

    public class WorldMapHumanTracker {

        public final EntityHuman trackee;
        private boolean d = true;
        private int e;
        private int f;
        private int g = 127;
        private int h = 127;
        private int i;
        public int b;

        public WorldMapHumanTracker(EntityHuman entityhuman) {
            this.trackee = entityhuman;
        }

        @Nullable
        public Packet<?> a(ItemStack itemstack) {
            // CraftBukkit start
            org.bukkit.craftbukkit.map.RenderData render = WorldMap.this.mapView.render((org.bukkit.craftbukkit.entity.CraftPlayer) this.trackee.getBukkitEntity()); // CraftBukkit

            java.util.Collection<MapIcon> icons = new java.util.ArrayList<MapIcon>();

            for ( org.bukkit.map.MapCursor cursor : render.cursors) {

                if (cursor.isVisible()) {
                    icons.add(new MapIcon(MapIcon.Type.a(cursor.getRawType()), cursor.getX(), cursor.getY(), cursor.getDirection()));
                }
            }

            if (this.d) {
                this.d = false;
                // PAIL: this.e
                return new PacketPlayOutMap(itemstack.getData(), WorldMap.this.scale, WorldMap.this.track, icons, render.buffer, this.e, this.f, this.g + 1 - this.e, this.h + 1 - this.f);
            } else {
                return this.i++ % 5 == 0 ? new PacketPlayOutMap(itemstack.getData(), WorldMap.this.scale, WorldMap.this.track, icons, render.buffer, 0, 0, 0, 0) : null;
            }
            // CraftBukkit end
        }

        public void a(int i, int j) {
            if (this.d) {
                this.e = Math.min(this.e, i);
                this.f = Math.min(this.f, j);
                this.g = Math.max(this.g, i);
                this.h = Math.max(this.h, j);
            } else {
                this.d = true;
                this.e = i;
                this.f = j;
                this.g = i;
                this.h = j;
            }

        }
    }
}
