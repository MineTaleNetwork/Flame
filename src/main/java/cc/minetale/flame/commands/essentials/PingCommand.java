package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");

        setCondition(Conditions::playerOnly);

        setDefaultExecutor(this::defaultExecutor);

        var targets = ArgumentType.Entity("targets")
                .onlyPlayers(true);

        addSyntax(this::onPingOthersCommand, targets);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        executeSelf(sender);
    }

    private void onPingOthersCommand(CommandSender sender, CommandContext context) {
        EntityFinder finder = context.get("targets");

        if (finder.find(sender).size() == 0) {
            sender.sendMessage(MC.Chat.notificationMessage("Ping", Component.text("A player with that name doesn't exist.", MC.CC.GRAY.getTextColor())));
        } else for (Entity entity : ((EntityFinder) context.get("targets")).find(sender)) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player == sender) {
                    executeSelf(sender);
                } else {
                    sender.sendMessage(MC.Chat.notificationMessage("Ping", Component.text(player.getUsername() + "'s ping is " + player.getLatency() + "ms", MC.CC.GRAY.getTextColor())));
                }
            }
        }
    }

    private void executeSelf(CommandSender sender) {
        sender.sendMessage(MC.Chat.notificationMessage("Ping", Component.text("Your ping is " + sender.asPlayer().getLatency() + "ms", MC.CC.GRAY.getTextColor())));
    }

}
