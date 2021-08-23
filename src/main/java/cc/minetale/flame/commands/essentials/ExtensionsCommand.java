package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.commands.RankUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;

import java.util.Collection;
import java.util.stream.Collectors;

public class ExtensionsCommand extends Command {

    public ExtensionsCommand() {
        super("extensions");
        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        if(sender.isConsole()) {
            this.execute(sender);
            return;
        }

        Player player = sender.asPlayer();

        RankUtil.canUseCommand(player, Rank.getRank("Owner"), commandCallback -> {
            if (!commandCallback.isMinimum())
                return;

            this.execute(sender);
        });
    }

    private void execute(CommandSender sender) {
        Collection<Extension> extensions = MinecraftServer.getExtensionManager().getExtensions();

        sender.sendMessage(Component.text("Extensions (" + extensions.size() + "): " + extensions.stream().map(extension -> extension.getOrigin().getName()).collect(Collectors.joining(", "))));
    }

}
