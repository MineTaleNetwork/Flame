package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");

        setCondition(CommandUtil.getRankCondition("Owner"));

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            player.kick(Component.text("The server is shutting down", MC.CC.RED.getTextColor()));
        }

        MinecraftServer.stopCleanly();
        System.exit(0);
    }

}
