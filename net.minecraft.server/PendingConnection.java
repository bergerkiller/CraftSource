package net.minecraft.server;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.crypto.SecretKey;

public class PendingConnection extends Connection {

    private static Random random = new Random();
    private byte[] d;
    private final MinecraftServer server;
    public final NetworkManager networkManager;
    public boolean b;
    private int f;
    private String g;
    private volatile boolean h;
    private String loginKey = "";
    private boolean j;
    private SecretKey k;

    public PendingConnection(MinecraftServer minecraftserver, Socket socket, String s) {
        this.server = minecraftserver;
        this.networkManager = new NetworkManager(minecraftserver.getLogger(), socket, s, this, minecraftserver.H().getPrivate());
        this.networkManager.e = 0;
    }

    public void d() {
        if (this.h) {
            this.e();
        }

        if (this.f++ == 600) {
            this.disconnect("Took too long to log in");
        } else {
            this.networkManager.b();
        }
    }

    public void disconnect(String s) {
        try {
            this.server.getLogger().info("Disconnecting " + this.getName() + ": " + s);
            this.networkManager.queue(new Packet255KickDisconnect(s));
            this.networkManager.d();
            this.b = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void a(Packet2Handshake packet2handshake) {
        if (this.g != null) {
            this.disconnect("Quit repeating yourself!");
        } else {
            this.g = packet2handshake.f();
            if (!this.g.equals(StripColor.a(this.g))) {
                this.disconnect("Invalid username!");
            } else {
                PublicKey publickey = this.server.H().getPublic();

                if (packet2handshake.d() != 78) {
                    if (packet2handshake.d() > 78) {
                        this.disconnect("Outdated server!");
                    } else {
                        this.disconnect("Outdated client!");
                    }
                } else {
                    this.loginKey = this.server.getOnlineMode() ? Long.toString(random.nextLong(), 16) : "-";
                    this.d = new byte[4];
                    random.nextBytes(this.d);
                    this.networkManager.queue(new Packet253KeyRequest(this.loginKey, publickey, this.d));
                }
            }
        }
    }

    public void a(Packet252KeyResponse packet252keyresponse) {
        PrivateKey privatekey = this.server.H().getPrivate();

        this.k = packet252keyresponse.a(privatekey);
        if (!Arrays.equals(this.d, packet252keyresponse.b(privatekey))) {
            this.disconnect("Invalid client reply");
        }

        this.networkManager.queue(new Packet252KeyResponse());
    }

    public void a(Packet205ClientCommand packet205clientcommand) {
        if (packet205clientcommand.a == 0) {
            if (this.j) {
                this.disconnect("Duplicate login");
                return;
            }

            this.j = true;
            if (this.server.getOnlineMode()) {
                (new ThreadLoginVerifier(this)).start();
            } else {
                this.h = true;
            }
        }
    }

    public void a(Packet1Login packet1login) {}

    public void e() {
        String s = this.server.getPlayerList().attemptLogin(this.networkManager.getSocketAddress(), this.g);

        if (s != null) {
            this.disconnect(s);
        } else {
            EntityPlayer entityplayer = this.server.getPlayerList().processLogin(this.g);

            if (entityplayer != null) {
                this.server.getPlayerList().a((INetworkManager) this.networkManager, entityplayer);
            }
        }

        this.b = true;
    }

    public void a(String s, Object[] aobject) {
        this.server.getLogger().info(this.getName() + " lost connection");
        this.b = true;
    }

    public void a(Packet254GetInfo packet254getinfo) {
        try {
            PlayerList playerlist = this.server.getPlayerList();
            String s = null;

            if (packet254getinfo.d()) {
                s = this.server.getMotd() + "\u00A7" + playerlist.getPlayerCount() + "\u00A7" + playerlist.getMaxPlayers();
            } else {
                List list = Arrays.asList(new Serializable[] { Integer.valueOf(1), Integer.valueOf(78), this.server.getVersion(), this.server.getMotd(), Integer.valueOf(playerlist.getPlayerCount()), Integer.valueOf(playerlist.getMaxPlayers())});

                Object object;

                for (Iterator iterator = list.iterator(); iterator.hasNext(); s = s + object.toString().replaceAll(", "")) {
                    object = iterator.next();
                    if (s == null) {
                        s = "\u00A7";
                    } else {
                        s = s + ";
                    }
                }
            }

            InetAddress inetaddress = null;

            if (this.networkManager.getSocket() != null) {
                inetaddress = this.networkManager.getSocket().getInetAddress();
            }

            this.networkManager.queue(new Packet255KickDisconnect(s));
            this.networkManager.d();
            if (inetaddress != null && this.server.ag() instanceof DedicatedServerConnection) {
                ((DedicatedServerConnection) this.server.ag()).a(inetaddress);
            }

            this.b = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void onUnhandledPacket(Packet packet) {
        this.disconnect("Protocol error");
    }

    public String getName() {
        return this.g != null ? this.g + " [" + this.networkManager.getSocketAddress().toString() + "]" : this.networkManager.getSocketAddress().toString();
    }

    public boolean a() {
        return true;
    }

    public boolean c() {
        return this.b;
    }

    static String a(PendingConnection pendingconnection) {
        return pendingconnection.loginKey;
    }

    static MinecraftServer b(PendingConnection pendingconnection) {
        return pendingconnection.server;
    }

    static SecretKey c(PendingConnection pendingconnection) {
        return pendingconnection.k;
    }

    static String d(PendingConnection pendingconnection) {
        return pendingconnection.g;
    }

    static boolean a(PendingConnection pendingconnection, boolean flag) {
        return pendingconnection.h = flag;
    }
}
