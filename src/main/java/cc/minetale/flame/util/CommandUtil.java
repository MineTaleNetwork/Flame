package cc.minetale.flame.util;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class CommandUtil {

    public static void callbackError(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(MC.notificationMessage("Command", Component.text(exception.getMessage(), NamedTextColor.GRAY)));
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
                        sender.sendMessage(Lang.COMMAND_PERMISSION(rank));

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
