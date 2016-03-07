package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;

public class PlayerChunk {

    private static final Logger a = LogManager.getLogger();
    private final PlayerChunkMap playerChunkMap;
    public final List<EntityPlayer> c = Lists.newArrayList(); // CraftBukkit - public
    private final ChunkCoordIntPair location;
    private final short[] dirtyBlocks = new short[64];
    public Chunk chunk; // CraftBukkit - public
    private int dirtyCount;
    private int h;
    private long i;
    private boolean done;

    // CraftBukkit start - add fields
    private final HashMap<EntityPlayer, Runnable> players = new HashMap<EntityPlayer, Runnable>();
    private Runnable loadedRunnable = new Runnable() {
        public void run() {
            PlayerChunk.this.chunk = PlayerChunk.this.playerChunkMap.getWorld().getChunkProviderServer().getOrLoadChunkAt(location.x, location.z);
        }
    };
    // CraftBukkit end

    public PlayerChunk(PlayerChunkMap playerchunkmap, int i, int j) {
        this.playerChunkMap = playerchunkmap;
        this.location = new ChunkCoordIntPair(i, j);
        // CraftBukkit start
        this.chunk = playerchunkmap.getWorld().getChunkProviderServer().getChunkAt(i, j, loadedRunnable);
        // CraftBukkit end
    }

    public ChunkCoordIntPair a() {
        return this.location;
    }

    public void a(final EntityPlayer entityplayer) {  // CraftBukkit - added final to argument
        if (this.c.contains(entityplayer)) {
            PlayerChunk.a.debug("Failed to add player. {} already is in chunk {}, {}", new Object[] { entityplayer, Integer.valueOf(this.location.x), Integer.valueOf(this.location.z)});
        } else {
            if (this.c.isEmpty()) {
                this.i = this.playerChunkMap.getWorld().getTime();
            }

            this.c.add(entityplayer);
            // CraftBukkit start - use async chunk io
            // if (this.j) {
            //     this.sendChunk(entityplayer);
            // }
            Runnable playerRunnable;
            if (this.done) {
                playerRunnable = null;
                sendChunk(entityplayer);
            } else {
                playerRunnable = new Runnable() {
                    public void run() {
                        sendChunk(entityplayer);
                    }
                };
                playerChunkMap.getWorld().getChunkProviderServer().getChunkAt(this.location.x, this.location.z, playerRunnable);
            }

            this.players.put(entityplayer, playerRunnable);
            // CraftBukkit end

        }
    }

    public void b(EntityPlayer entityplayer) {
        if (this.c.contains(entityplayer)) {
            // CraftBukkit start - If we haven't loaded yet don't load the chunk just so we can clean it up
            if (!this.done) {
                ChunkIOExecutor.dropQueuedChunkLoad(this.playerChunkMap.getWorld(), this.location.x, this.location.z, this.players.get(entityplayer));
                this.c.remove(entityplayer);
                this.players.remove(entityplayer);

                if (this.c.isEmpty()) {
                    ChunkIOExecutor.dropQueuedChunkLoad(this.playerChunkMap.getWorld(), this.location.x, this.location.z, this.loadedRunnable);
                    this.playerChunkMap.b(this);
                }

                return;
            }
            // CraftBukkit end
            if (this.done) {
                entityplayer.playerConnection.sendPacket(new PacketPlayOutUnloadChunk(this.location.x, this.location.z));
            }

            this.players.remove(entityplayer); // CraftBukkit
            this.c.remove(entityplayer);
            if (this.c.isEmpty()) {
                this.playerChunkMap.b(this);
            }

        }
    }

    public boolean a(boolean flag) {
        if (this.chunk != null || true) { // CraftBukkit
            return done; // CraftBukkit
        } else {
            if (flag) {
                this.chunk = this.playerChunkMap.getWorld().getChunkProviderServer().getChunkAt(this.location.x, this.location.z);
            } else {
                this.chunk = this.playerChunkMap.getWorld().getChunkProviderServer().getOrLoadChunkAt(this.location.x, this.location.z);
            }

            return this.chunk != null;
        }
    }

    public boolean b() {
        if (this.done) {
            return true;
        } else if (this.chunk == null) {
            return false;
        } else if (!this.chunk.isReady()) {
            return false;
        } else {
            this.dirtyCount = 0;
            this.h = 0;
            this.done = true;
            ArrayList arraylist = Lists.newArrayList(this.playerChunkMap.getWorld().getTileEntities(this.location.x * 16, 0, this.location.z * 16, this.location.x * 16 + 16, 256, this.location.z * 16 + 16));
            PacketPlayOutMapChunk packetplayoutmapchunk = new PacketPlayOutMapChunk(this.chunk, true, '\uffff');
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.playerConnection.sendPacket(packetplayoutmapchunk);
                Iterator iterator1 = arraylist.iterator();

                while (iterator1.hasNext()) {
                    TileEntity tileentity = (TileEntity) iterator1.next();
                    Packet packet = tileentity.getUpdatePacket();

                    if (packet != null) {
                        entityplayer.playerConnection.sendPacket(packet);
                    }
                }

                this.playerChunkMap.getWorld().getTracker().a(entityplayer, this.chunk);
            }

            return true;
        }
    }

    public void sendChunk(EntityPlayer entityplayer) {
        if (this.done) {
            entityplayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(this.chunk, true, '\uffff'));
            Iterator iterator = this.playerChunkMap.getWorld().getTileEntities(this.location.x * 16, 0, this.location.z * 16, this.location.x * 16 + 16, 256, this.location.z * 16 + 16).iterator();

            while (iterator.hasNext()) {
                TileEntity tileentity = (TileEntity) iterator.next();
                Packet packet = tileentity.getUpdatePacket();

                if (packet != null) {
                    entityplayer.playerConnection.sendPacket(packet);
                }
            }

            this.playerChunkMap.getWorld().getTracker().a(entityplayer, this.chunk);
        }
    }

    public void c() {
        if (this.chunk != null) {
            this.chunk.c(this.chunk.x() + this.playerChunkMap.getWorld().getTime() - this.i);
        }

        this.i = this.playerChunkMap.getWorld().getTime();
    }

    public void a(int i, int j, int k) {
        if (this.done) {
            if (this.dirtyCount == 0) {
                this.playerChunkMap.a(this);
            }

            this.h |= 1 << (j >> 4);
            if (this.dirtyCount < 64) {
                short short0 = (short) (i << 12 | k << 8 | j);

                for (int l = 0; l < this.dirtyCount; ++l) {
                    if (this.dirtyBlocks[l] == short0) {
                        return;
                    }
                }

                this.dirtyBlocks[this.dirtyCount++] = short0;
            }

        }
    }

    public void a(Packet<?> packet) {
        if (this.done) {
            for (int i = 0; i < this.c.size(); ++i) {
                ((EntityPlayer) this.c.get(i)).playerConnection.sendPacket(packet);
            }

        }
    }

    public void d() {
        if (this.done && this.chunk != null) {
            if (this.dirtyCount != 0) {
                int i;
                int j;
                int k;

                if (this.dirtyCount == 1) {
                    i = (this.dirtyBlocks[0] >> 12 & 15) + this.location.x * 16;
                    j = this.dirtyBlocks[0] & 255;
                    k = (this.dirtyBlocks[0] >> 8 & 15) + this.location.z * 16;
                    BlockPosition blockposition = new BlockPosition(i, j, k);

                    this.a((Packet) (new PacketPlayOutBlockChange(this.playerChunkMap.getWorld(), blockposition)));
                    if (this.playerChunkMap.getWorld().getType(blockposition).getBlock().isTileEntity()) {
                        this.a(this.playerChunkMap.getWorld().getTileEntity(blockposition));
                    }
                } else {
                    int l;

                    if (this.dirtyCount == 64) {
                        i = this.location.x * 16;
                        j = this.location.z * 16;
                        this.a((Packet) (new PacketPlayOutMapChunk(this.chunk, false, this.h)));

                        for (k = 0; k < 16; ++k) {
                            if ((this.h & 1 << k) != 0) {
                                l = k << 4;
                                List list = this.playerChunkMap.getWorld().getTileEntities(i, l, j, i + 16, l + 16, j + 16);

                                for (int i1 = 0; i1 < list.size(); ++i1) {
                                    this.a((TileEntity) list.get(i1));
                                }
                            }
                        }
                    } else {
                        this.a((Packet) (new PacketPlayOutMultiBlockChange(this.dirtyCount, this.dirtyBlocks, this.chunk)));

                        for (i = 0; i < this.dirtyCount; ++i) {
                            j = (this.dirtyBlocks[i] >> 12 & 15) + this.location.x * 16;
                            k = this.dirtyBlocks[i] & 255;
                            l = (this.dirtyBlocks[i] >> 8 & 15) + this.location.z * 16;
                            BlockPosition blockposition1 = new BlockPosition(j, k, l);

                            if (this.playerChunkMap.getWorld().getType(blockposition1).getBlock().isTileEntity()) {
                                this.a(this.playerChunkMap.getWorld().getTileEntity(blockposition1));
                            }
                        }
                    }
                }

                this.dirtyCount = 0;
                this.h = 0;
            }
        }
    }

    private void a(TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.getUpdatePacket();

            if (packet != null) {
                this.a(packet);
            }
        }

    }

    public boolean d(EntityPlayer entityplayer) {
        return this.c.contains(entityplayer);
    }

    public boolean a(Predicate<EntityPlayer> predicate) {
        return Iterables.tryFind(this.c, predicate).isPresent();
    }

    public boolean a(double d0, Predicate<EntityPlayer> predicate) {
        int i = 0;

        for (int j = this.c.size(); i < j; ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.c.get(i);

            if (predicate.apply(entityplayer) && this.location.a(entityplayer) < d0 * d0) {
                return true;
            }
        }

        return false;
    }

    public boolean e() {
        return this.done;
    }

    public Chunk f() {
        return this.chunk;
    }

    public double g() {
        double d0 = Double.MAX_VALUE;
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            double d1 = this.location.a(entityplayer);

            if (d1 < d0) {
                d0 = d1;
            }
        }

        return d0;
    }
}
