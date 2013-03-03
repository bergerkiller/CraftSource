package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Packet41MobEffect extends Packet {

    public int a;
    public byte b;
    public byte c;
    public short d;

    public Packet41MobEffect() {}

    public Packet41MobEffect(int i, MobEffect mobeffect) {
        this.a = i;
        this.b = (byte) (mobeffect.getEffectId() & 255);
        this.c = (byte) (mobeffect.getAmplifier() & 255);
        this.d = (short) mobeffect.getDuration();
    }

    public void a(DataInputStream datainputstream) {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readByte();
        this.c = datainputstream.readByte();
        this.d = datainputstream.readShort();
    }

    public void a(DataOutputStream dataoutputstream) {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeByte(this.b);
        dataoutputstream.writeByte(this.c);
        dataoutputstream.writeShort(this.d);
    }

    public void handle(Connection connection) {
        connection.a(this);
    }

    public int a() {
        return 8;
    }

    public boolean e() {
        return true;
    }

    public boolean a(Packet packet) {
        Packet41MobEffect packet41mobeffect = (Packet41MobEffect) packet;

        return packet41mobeffect.a == this.a && packet41mobeffect.b == this.b;
    }
}
