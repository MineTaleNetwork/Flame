package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.commands.RankUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");
        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        if(sender.isConsole()) {
            this.execute();
            return;
        }

        Player player = sender.asPlayer();

        RankUtil.canUseCommand(player, Rank.getRank("Owner"), commandCallback -> {
            if (!commandCallback.isMinimum())
                return;

            this.execute();
        });
    }

    private void execute() {
        for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            player.kick(Component.text("The server is shutting down", MC.CC.RED.getTextColor()));
        }

        MinecraftServer.stopCleanly();
    }

}
