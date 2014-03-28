package net.minecraft.server;

import java.util.ArrayList;
import java.util.List;

public class CommandOp extends CommandAbstract {

    public CommandOp() {}

    public String getCommand() {
        return "op";
    }

    public int a() {
        return 3;
    }

    public String c(ICommandListener icommandlistener) {
        return "commands.op.usage";
    }

    public void execute(ICommandListener icommandlistener, String[] astring) {
        if (astring.length == 1 && astring[0].length() > 0) {
            MinecraftServer.getServer().getPlayerList().addOp(astring[0]);
            a(icommandlistener, this, "commands.op.success", new Object[] { astring[0]});
        } else {
            throw new ExceptionUsage("commands.op.usage", new Object[0]);
        }
    }

    public List tabComplete(ICommandListener icommandlistener, String[] astring) {
        if (astring.length == 1) {
            String s = astring[astring.length - 1];
            ArrayList arraylist = new ArrayList();
            String[] astring1 = MinecraftServer.getServer().getPlayers();
            int i = astring1.length;

            for (int j = 0; j < i; ++j) {
                String s1 = astring1[j];

                if (!MinecraftServer.getServer().getPlayerList().isOp(s1) && a(s, s1)) {
                    arraylist.add(s1);
                }
            }

            return arraylist;
        } else {
            return null;
        }
    }
}
