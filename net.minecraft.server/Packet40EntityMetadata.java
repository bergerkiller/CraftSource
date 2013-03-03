package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

public class Packet40EntityMetadata extends Packet {

    public int a;
    private List b;

    public Packet40EntityMetadata() {}

    public Packet40EntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.a = i;
        if (flag) {
            this.b = datawatcher.c();
        } else {
            this.b = datawatcher.b();
        }
    }

    public void a(DataInputStream datainputstream) {
        this.a = datainputstream.readInt();
        this.b = DataWatcher.a(datainputstream);
    }

    public void a(DataOutputStream dataoutputstream) {
        dataoutputstream.writeInt(this.a);
        DataWatcher.a(this.b, dataoutputstream);
    }

    public void handle(Connection connection) {
        connection.a(this);
    }

    public int a() {
        return 5;
    }
}
