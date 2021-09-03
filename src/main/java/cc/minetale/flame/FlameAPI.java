package cc.minetale.flame;

import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;

import java.util.UUID;

public class FlameAPI {

    @Getter @Setter private static boolean chatMuted = false;
    @Getter @Setter private static boolean whitelisted = false;
    @Getter @Setter private static int maxPlayers = 1000;

    public static boolean canStartProcedure(UUID uuid) {
        return GrantProcedure.getByPlayer(uuid) == null && PunishmentProcedure.getByPlayer(uuid) == null;
    }

}
