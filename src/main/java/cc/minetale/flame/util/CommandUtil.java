package cc.minetale.flame.util;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandUtil {

    public static Component getUsage(String command, String... args) {
        return Message.notification("Command",
                Component.text("Usage: /" + command + " " + Arrays.stream(args).map(argument -> "<" + argument + ">").collect(Collectors.joining(" ")), NamedTextColor.GRAY));
    }

    public static Component getJoinedUsage(String command, String... args) {
        return Message.notification("Command",
                Component.text("Usage: /" + command + " <" + String.join("/", args) + ">", NamedTextColor.GRAY));
    }

    public static void callbackError(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(Message.notification("Command", Component.text(exception.getMessage(), NamedTextColor.GRAY)));
    }

    public static CommandCondition getRankCondition(Rank rank) {
        return ((sender, s) -> {
            boolean command = s != null;

            if (sender instanceof ConsoleSender)
                return true;

            if (sender instanceof Player player) {
                boolean hasMinimum = Rank.hasMinimumRank(FlamePlayer.fromPlayer(player).getProfile(), rank);

                if(!hasMinimum)
                    if (command)
                        sender.sendMessage(Message.parse(Language.Command.COMMAND_PERMISSION_ERROR, Component.text(rank.getName(), NamedTextColor.GOLD)));

                return hasMinimum;
            }

            return false;
        });
    }

    public static Player playerFromEntityFinder(CommandSender sender, EntityFinder finder) {
        var entityList = finder.find(sender);

        if (entityList.size() == 0) return null;

        for (var entity : entityList) {
            if (entity instanceof Player player) {
                return player;
            }
        }

        return null;
    }

}
