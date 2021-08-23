package cc.minetale.flame;

import lombok.Getter;
import lombok.Setter;

public class FlameAPI {

    @Getter @Setter private static boolean chatMuted = false;
    @Getter @Setter private static boolean whitelisted = false;
    @Getter @Setter private static int maxPlayers = 1000;

}
