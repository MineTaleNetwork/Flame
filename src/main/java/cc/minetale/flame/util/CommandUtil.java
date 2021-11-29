package cc.minetale.flame.util;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.flame.Lang;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Player;

public class CommandUtil {

    public static CommandCondition getRankCondition(Rank rank) {
        return ((sender, s) -> {
            boolean command = s != null;

            if (sender.isConsole())
                return true;

            if (sender.isPlayer()) {
                Player player = sender.asPlayer();

                boolean hasMinimum = Rank.hasMinimumRank(FlamePlayer.fromPlayer(player).getProfile(), rank);

                if(!hasMinimum)
                    if (command)
                        sender.sendMessage(Lang.COMMAND_PERMISSION(rank));


                return hasMinimum;
            }

            return false;
        });
    }


}
