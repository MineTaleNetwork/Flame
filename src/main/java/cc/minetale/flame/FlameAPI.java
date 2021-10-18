package cc.minetale.flame;

import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.team.TeamUtils;
import cc.minetale.mlib.util.ProfileUtil;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public class FlameAPI {

    @Getter @Setter private static boolean chatMuted = false;
    @Getter @Setter private static boolean whitelisted = false;
    @Getter @Setter private static int maxPlayers = 1000;

    public static boolean canStartProcedure(UUID uuid) {
        return GrantProcedure.getByPlayer(uuid) == null && PunishmentProcedure.getByPlayer(uuid) == null;
    }

    public static void refreshPlayers(UUID rank) {
        FlameAPI.refreshCommands();

        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
                if(profile.getGrant().getRankUUID().equals(rank)) {
                    profile.reloadGrant();
                    TeamUtils.updateTeam(profile.getGrant().api().getRank(), player);
                }
            });
        }
    }

    public static void refreshCommands() {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(Player::refreshCommands);
    }

}
