package cc.minetale.flame.util;

import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Colors;
import cc.minetale.sodium.util.Message;
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

            if (sender instanceof FlamePlayer player) {
                var profile = player.getProfile();
                if(profile == null) { return false; }

                boolean hasMinimum = Rank.hasMinimumRank(profile, rank);

                if(!hasMinimum && command)
                    sender.sendMessage(Message.parse(Language.Command.COMMAND_PERMISSION, Component.text(rank.getName(), Colors.BLUE)));

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
