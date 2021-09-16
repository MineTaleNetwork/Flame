package cc.minetale.flame.util;

import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.flame.Lang;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class CommandUtil {

    public static CommandCondition getRankCondition(String rankName) {
        return ((sender, s) -> {
            boolean command = s != null;

            if (sender.isConsole())
                return true;

            if (sender.isPlayer()) {
                Player player = sender.asPlayer();

                boolean hasMinimum = RankUtil.hasMinimumRank(player, rankName);

                if(!hasMinimum)
                    if (command)
                        sender.sendMessage(Lang.COMMAND_PERMISSION(rankName));


                return hasMinimum;
            }

            return false;
        });
    }


}
