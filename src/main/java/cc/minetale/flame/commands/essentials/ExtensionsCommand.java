package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.extensions.Extension;

import java.util.Collection;
import java.util.stream.Collectors;

public class ExtensionsCommand extends Command {

    public ExtensionsCommand() {
        super("extensions");

        setCondition(CommandUtil.getRankCondition("Owner"));

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        Collection<Extension> extensions = MinecraftServer.getExtensionManager().getExtensions();

        sender.sendMessage(Component.text("Extensions (" + extensions.size() + "): " + extensions.stream().map(extension -> extension.getOrigin().getName()).collect(Collectors.joining(", "))));
    }

}
