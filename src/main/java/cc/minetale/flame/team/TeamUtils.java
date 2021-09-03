package cc.minetale.flame.team;

import cc.minetale.commonlib.modules.rank.Rank;
import org.apache.commons.lang3.StringUtils;

public class TeamUtils {

    public static String getTeamName(Rank rank) {
        String trimmedName = StringUtils.left(rank.getName(), 13);

        return String.format("%03d", rank.getWeight()) + trimmedName;
    }

}
