package cc.minetale.flame.util;

import cc.minetale.commonlib.rank.Rank;
import net.minestom.server.entity.Player;

public class RankUtil {

    public static boolean hasMinimumRank(Player player, String rankName) {
        Rank rank = Rank.getRank(rankName);

        if(rank == null)
            return false;

        return ((FlamePlayer) player).getRank() <= rank.getWeight();
    }

}
