package cc.minetale.flame.team;

import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.flame.Flame;
import cc.minetale.mlib.util.ProfileUtil;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Team;
import org.apache.commons.lang3.StringUtils;

public class TeamUtils {

    public static String getTeamName(Rank rank) {
        String trimmedName = StringUtils.left(rank.getName(), 13);

        return String.format("%03d", rank.getWeight()) + trimmedName;
    }

    public static void updateTeam(Player player) {
        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            Rank rank = profile.api().getActiveGrant().api().getRank();

            Team playerTeam = player.getTeam();
            Team rankTeam = Flame.getFlame().getRankTeams().get(rank.getUuid());

            if(playerTeam != rankTeam) {
                playerTeam.removeMember(player.getUsername());
                rankTeam.addMember(player.getUsername());
                player.setTeam(rankTeam);
            }
        });
    }

    public static void updateTeam(Rank rank, Player player) {
        Team playerTeam = player.getTeam();
        Team rankTeam = Flame.getFlame().getRankTeams().get(rank.getUuid());

        if(playerTeam != rankTeam) {
            playerTeam.removeMember(player.getUsername());
            rankTeam.addMember(player.getUsername());
            player.setTeam(rankTeam);
        }
    }

}
