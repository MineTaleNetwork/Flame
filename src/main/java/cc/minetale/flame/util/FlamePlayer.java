package cc.minetale.flame.util;

import cc.minetale.commonlib.modules.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class FlamePlayer extends Player {

    @Setter private Profile profile;
    private int rank;

    public FlamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    public void updatePermission() {
        this.rank = this.profile.getGrant().api().getRank().getWeight();
    }

}
