package cc.minetale.flame;

import cc.minetale.flame.procedure.Procedure;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;

public class FlameAPI {

    @Getter @Setter private static boolean chatMuted = false;

    public static boolean canStartProcedure(Player player) {
        return Procedure.getProcedure(player) == null;
    }

}
