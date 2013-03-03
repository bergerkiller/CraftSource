package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Packet60Explosion extends Packet {

    public double a;
    public double b;
    public double c;
    public float d;
    public List e;
    private float f;
    private float g;
    private float h;

    public Packet60Explosion() {}

    public Packet60Explosion(double d0, double d1, double d2, float f, List list, Vec3D vec3d) {
        this.a = d0;
        this.b = d1;
        this.c = d2;
        this.d = f;
        this.e = new ArrayList(list);
        if (vec3d != null) {
            this.f = (float) vec3d.c;
            this.g = (float) vec3d.d;
            this.h = (float) vec3d.e;
        }
    }

    public void a(DataInputStream datainputstream) {
        this.a = datainputstream.readDouble();
        this.b = datainputstream.readDouble();
        this.c = datainputstream.readDouble();
        this.d = datainputstream.readFloat();
        int i = datainputstream.readInt();

        this.e = new ArrayList(i);
        int j = (int) this.a;
        int k = (int) this.b;
        int l = (int) this.c;

        for (int i1 = 0; i1 < i; ++i1) {
            int j1 = datainputstream.readByte() + j;
            int k1 = datainputstream.readByte() + k;
            int l1 = datainputstream.readByte() + l;

            this.e.add(new ChunkPosition(j1, k1, l1));
        }

        this.f = datainputstream.readFloat();
        this.g = datainputstream.readFloat();
        this.h = datainputstream.readFloat();
    }

    public void a(DataOutputStream dataoutputstream) {
        dataoutputstream.writeDouble(this.a);
        dataoutputstream.writeDouble(this.b);
        dataoutputstream.writeDouble(this.c);
        dataoutputstream.writeFloat(this.d);
        dataoutputstream.writeInt(this.e.size());
        int i = (int) this.a;
        int j = (int) this.b;
        int k = (int) this.c;
        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            ChunkPosition chunkposition = (ChunkPosition) iterator.next();
            int l = chunkposition.x - i;
            int i1 = chunkposition.y - j;
            int j1 = chunkposition.z - k;

            dataoutputstream.writeByte(l);
            dataoutputstream.writeByte(i1);
            dataoutputstream.writeByte(j1);
        }

        dataoutputstream.writeFloat(this.f);
        dataoutputstream.writeFloat(this.g);
        dataoutputstream.writeFloat(this.h);
    }

    public void handle(Connection connection) {
        connection.a(this);
    }

    public int a() {
        return 32 + this.e.size() * 3 + 3;
    }
}
