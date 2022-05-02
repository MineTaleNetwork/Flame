package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.util.CommandUtil;
import cc.minetale.sodium.profile.grant.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        for(var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            player.kick(Component.text("The server is shutting down", NamedTextColor.RED));
        }

        MinecraftServer.stopCleanly();
        System.exit(0);
    }

}
