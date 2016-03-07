package net.minecraft.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start

import org.bukkit.Server;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.craftbukkit.util.LongHashSet;
import org.bukkit.craftbukkit.util.LongObjectHashMap;
import org.bukkit.event.world.ChunkUnloadEvent;
// CraftBukkit end

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger a = LogManager.getLogger();
    public final LongHashSet unloadQueue = new LongHashSet(); // CraftBukkit - LongHashSet
    public final ChunkGenerator chunkGenerator; // CraftBukkit - public
    private final IChunkLoader chunkLoader;
    public LongObjectHashMap<Chunk> chunks = new LongObjectHashMap<Chunk>(); // CraftBukkit
    // private final LongHashMap<Chunk> chunks = new LongHashMap();
    // private final List<Chunk> chunkList = Lists.newArrayList();
    public final WorldServer world;

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, ChunkGenerator chunkgenerator) {
        this.world = worldserver;
        this.chunkLoader = ichunkloader;
        this.chunkGenerator = chunkgenerator;
    }

    public boolean isChunkLoaded(int i, int j) {
        return this.chunks.containsKey(LongHash.toLong(i, j)); // CraftBukkit
    }

    // CraftBukkit start - Change return type to Collection and return the values of our chunk map
    public java.util.Collection a() {
        // return this.chunkList;
        return this.chunks.values();
        // CraftBukkit end
    }

    public void queueUnload(int i, int j) {
        if (this.world.worldProvider.c(i, j)) {
            // CraftBukkit start
            this.unloadQueue.add(i, j);

            Chunk c = chunks.get(LongHash.toLong(i, j));
            if (c != null) {
                c.mustSave = true;
            }
            // CraftBukkit end
        }

    }

    public void b() {
        Iterator iterator = this.chunks.values().iterator(); // CraftBukkit

        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();

            this.queueUnload(chunk.locX, chunk.locZ);
        }

    }

    // CraftBukkit start - Add async variant, provide compatibility
    public Chunk getOrCreateChunkFast(int x, int z) {
        Chunk chunk = chunks.get(LongHash.toLong(x, z));
        return (chunk == null) ? getChunkAt(x, z) : chunk;
    }

    public Chunk getChunkIfLoaded(int x, int z) {
        return chunks.get(LongHash.toLong(x, z));
    }

    public Chunk getLoadedChunkAt(int i, int j) {
        Chunk chunk = chunks.get(LongHash.toLong(i, j)); // CraftBukkit

        this.unloadQueue.remove(i, j); // CraftBukkit
        return chunk;
    }

    public Chunk getOrLoadChunkAt(int i, int j) {
        Chunk chunk = this.getLoadedChunkAt(i, j);

        if (chunk == null) {
            // CraftBukkit start
            ChunkRegionLoader loader = null;

            if (this.chunkLoader instanceof ChunkRegionLoader) {
                loader = (ChunkRegionLoader) this.chunkLoader;
            }
            if (loader != null && loader.chunkExists(world, i, j)) {
                chunk = ChunkIOExecutor.syncChunkLoad(world, loader, this, i, j);
            }
            /* chunk = this.loadChunk(i, j);
            if (chunk != null) {
                this.chunks.put(ChunkCoordIntPair.a(i, j), chunk);
                this.chunkList.add(chunk);
                chunk.addEntities();
                chunk.loadNearby(this, this.chunkGenerator);
            }
            */
            // CraftBukkit end
        }

        return chunk;
    }

    public Chunk getChunkAt(int i, int j) {
        return getChunkAt(i, j, null);
    }

    public Chunk getChunkAt(int i, int j, Runnable runnable) {
        unloadQueue.remove(i, j);
        Chunk chunk = chunks.get(LongHash.toLong(i, j));
        ChunkRegionLoader loader = null;

        if (this.chunkLoader instanceof ChunkRegionLoader) {
            loader = (ChunkRegionLoader) this.chunkLoader;

        }
        // We can only use the queue for already generated chunks
        if (chunk == null && loader != null && loader.chunkExists(world, i, j)) {
            if (runnable != null) {
                ChunkIOExecutor.queueChunkLoad(world, loader, this, i, j, runnable);
                return null;
            } else {
                chunk = ChunkIOExecutor.syncChunkLoad(world, loader, this, i, j);
            }
        } else if (chunk == null) {
            chunk = originalGetChunkAt(i, j);
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) {
            runnable.run();
        }

        return chunk;
    }

    public Chunk originalGetChunkAt(int i, int j) {
        this.unloadQueue.remove(i, j);
        Chunk chunk = this.chunks.get(LongHash.toLong(i, j));
        boolean newChunk = false;
        // CraftBukkit end

        if (chunk == null) {
            world.timings.syncChunkLoadTimer.startTiming(); // Spigot
            long k = ChunkCoordIntPair.a(i, j);

            chunk = this.loadChunk(i, j);
            if (chunk == null) {
                try {
                    chunk = this.chunkGenerator.getOrCreateChunk(i, j);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
                    CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

                    crashreportsystemdetails.a("Location", (Object) String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
                    crashreportsystemdetails.a("Position hash", (Object) Long.valueOf(k));
                    crashreportsystemdetails.a("Generator", (Object) this.chunkGenerator);
                    throw new ReportedException(crashreport);
                }
                newChunk = true; // CraftBukkit
            }

            this.chunks.put(LongHash.toLong(i, j), chunk); // CraftBukkit
            chunk.addEntities();

            // CraftBukkit start
            Server server = world.getServer();
            if (server != null) {
                /*
                 * If it's a new world, the first few chunks are generated inside
                 * the World constructor. We can't reliably alter that, so we have
                 * no way of creating a CraftWorld/CraftServer at that point.
                 */
                server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, newChunk));
            }

            // Update neighbor counts
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                    if (neighbor != null) {
                        neighbor.setNeighborLoaded(-x, -z);
                        chunk.setNeighborLoaded(x, z);
                    }
                }
            }
            // CraftBukkit end
            chunk.loadNearby(this, this.chunkGenerator);
            world.timings.syncChunkLoadTimer.stopTiming(); // Spigot
        }

        return chunk;
    }

    public Chunk loadChunk(int i, int j) {
        try {
            Chunk chunk = this.chunkLoader.a(this.world, i, j);

            if (chunk != null) {
                chunk.setLastSaved(this.world.getTime());
                this.chunkGenerator.recreateStructures(chunk, i, j);
            }

            return chunk;
        } catch (Exception exception) {
            ChunkProviderServer.a.error("Couldn\'t load chunk", exception);
            return null;
        }
    }

    public void saveChunkNOP(Chunk chunk) {
        try {
            this.chunkLoader.b(this.world, chunk);
        } catch (Exception exception) {
            ChunkProviderServer.a.error("Couldn\'t save entities", exception);
        }

    }

    public void saveChunk(Chunk chunk) {
        try {
            chunk.setLastSaved(this.world.getTime());
            this.chunkLoader.a(this.world, chunk);
        } catch (IOException ioexception) {
            ChunkProviderServer.a.error("Couldn\'t save chunk", ioexception);
        } catch (ExceptionWorldConflict exceptionworldconflict) {
            ChunkProviderServer.a.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
        }

    }

    public boolean a(boolean flag) {
        int i = 0;

        // CraftBukkit start
        Iterator iterator = this.chunks.values().iterator();
        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();
            // CraftBukkit end

            if (flag) {
                this.saveChunkNOP(chunk);
            }

            if (chunk.a(flag)) {
                this.saveChunk(chunk);
                chunk.f(false);
                ++i;
                if (i == 24 && !flag && false) { // Spigot
                    return false;
                }
            }
        }

        return true;
    }

    public void c() {
        this.chunkLoader.b();
    }

    public boolean unloadChunks() {
        if (!this.world.savingDisabled) {
            // CraftBukkit start
            Server server = this.world.getServer();
            for (int i = 0; i < 100 && !this.unloadQueue.isEmpty(); ++i) {
                long chunkcoordinates = this.unloadQueue.popFirst();
                Chunk chunk = this.chunks.get(chunkcoordinates);
                if (chunk == null) continue;

                ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
                server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {

                    if (chunk != null) {
                        chunk.removeEntities();
                        this.saveChunk(chunk);
                        this.saveChunkNOP(chunk);
                        this.chunks.remove(chunkcoordinates); // CraftBukkit
                    }

                    // this.unloadQueue.remove(olong);

                    // Update neighbor counts
                    for (int x = -2; x < 3; x++) {
                        for (int z = -2; z < 3; z++) {
                            if (x == 0 && z == 0) {
                                continue;
                            }

                            Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                            if (neighbor != null) {
                                neighbor.setNeighborUnloaded(-x, -z);
                                chunk.setNeighborUnloaded(x, z);
                            }
                        }
                    }
                }
            }
            // CraftBukkit end

            this.chunkLoader.a();
        }

        return false;
    }

    public boolean e() {
        return !this.world.savingDisabled;
    }

    public String getName() {
        // CraftBukkit - this.chunks.count() -> .size()
        return "ServerChunkCache: " + this.chunks.size() + " Drop: " + this.unloadQueue.size();
    }

    public List<BiomeBase.BiomeMeta> a(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.chunkGenerator.getMobsFor(enumcreaturetype, blockposition);
    }

    public BlockPosition a(World world, String s, BlockPosition blockposition) {
        return this.chunkGenerator.findNearestMapFeature(world, s, blockposition);
    }

    public int g() {
        // CraftBukkit - this.chunks.count() -> this.chunks.size()
        return this.chunks.size();
    }

    public boolean e(int i, int j) {
        return this.chunks.containsKey(LongHash.toLong(i, j)); // CraftBukkit
    }
}
