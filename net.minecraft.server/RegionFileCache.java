package net.minecraft.server;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache {

    public static final Map<File, RegionFile> a = Maps.newHashMap(); // Spigot - private -> public

    public static synchronized RegionFile a(File file, int i, int j) {
        File file1 = new File(file, "region");
        File file2 = new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
        RegionFile regionfile = (RegionFile) RegionFileCache.a.get(file2);

        if (regionfile != null) {
            return regionfile;
        } else {
            if (!file1.exists()) {
                file1.mkdirs();
            }

            if (RegionFileCache.a.size() >= 256) {
                a();
            }

            RegionFile regionfile1 = new RegionFile(file2);

            RegionFileCache.a.put(file2, regionfile1);
            return regionfile1;
        }
    }

    public static synchronized RegionFile b(File file, int i, int j) {
        File file1 = new File(file, "region");
        File file2 = new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
        RegionFile regionfile = (RegionFile) RegionFileCache.a.get(file2);

        if (regionfile != null) {
            return regionfile;
        } else if (file1.exists() && file2.exists()) {
            if (RegionFileCache.a.size() >= 256) {
                a();
            }

            RegionFile regionfile1 = new RegionFile(file2);

            RegionFileCache.a.put(file2, regionfile1);
            return regionfile1;
        } else {
            return null;
        }
    }

    public static synchronized void a() {
        Iterator iterator = RegionFileCache.a.values().iterator();

        while (iterator.hasNext()) {
            RegionFile regionfile = (RegionFile) iterator.next();

            try {
                if (regionfile != null) {
                    regionfile.c();
                }
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        RegionFileCache.a.clear();
    }

    // CraftBukkit start - call sites hoisted for synchronization
    public static synchronized NBTTagCompound d(File file, int i, int j) throws IOException {
        RegionFile regionfile = a(file, i, j);

        DataInputStream datainputstream = regionfile.a(i & 31, j & 31);

        if (datainputstream == null) {
            return null;
        }

        return NBTCompressedStreamTools.a(datainputstream);
    }

    public static synchronized void e(File file, int i, int j, NBTTagCompound nbttagcompound) throws IOException {
        RegionFile regionfile = a(file, i, j);

        DataOutputStream dataoutputstream = regionfile.b(i & 31, j & 31);
        NBTCompressedStreamTools.a(nbttagcompound, (java.io.DataOutput) dataoutputstream);
        dataoutputstream.close();
    }
    // CraftBukkit end

    public static boolean f(File file, int i, int j) {
        RegionFile regionfile = b(file, i, j);

        return regionfile != null ? regionfile.c(i & 31, j & 31) : false;
    }
}
