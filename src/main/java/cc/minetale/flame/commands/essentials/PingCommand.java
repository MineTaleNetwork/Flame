package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.util.CommandUtil;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");

        setDefaultExecutor(((sender, context) -> pingSelf(sender)));

        addSyntax(((sender, context) -> {
            var player = CommandUtil.playerFromEntityFinder(sender, context.get("target"));

            if(sender instanceof Player executor) {
                if (player != null) {
                    if(player == executor) {
                        pingSelf(sender);
                    } else {
                        sender.sendMessage(Message.notification("Ping",
                                Component.text(player.getUsername() + "'s ping is " + player.getLatency() + "ms", NamedTextColor.GRAY)));
                    }
                } else {
                    sender.sendMessage(Message.notification("Ping",
                            Component.text("A player with that name could not be found.", NamedTextColor.GRAY)));
                }
            }
        }), ArgumentType.Entity("target").onlyPlayers(true).singleEntity(true));
    }

    private void pingSelf(CommandSender sender) {
        if(sender instanceof Player executor) {
            sender.sendMessage(Message.notification("Ping",
                    Component.text("Your ping is " + executor.getLatency() + "ms", NamedTextColor.GRAY)));
        }
    }

}
