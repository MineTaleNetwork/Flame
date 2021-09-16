package cc.minetale.flame.util;

import cc.minetale.commonlib.modules.rank.Rank;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class RankUtil {

    public static boolean hasMinimumRank(Player player, String rankName) {
        Rank rank = Rank.getRank(rankName);

        if(rank == null)
            return false;

        Integer permissionLevel = player.getTag(Tag.Integer("permission"));

        if(permissionLevel == null)
            return false;

        return permissionLevel <= rank.getWeight();
    }

}
