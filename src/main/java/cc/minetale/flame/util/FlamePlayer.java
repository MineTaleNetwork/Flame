package cc.minetale.flame.util;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Cooldown;
import cc.minetale.commonlib.util.ProfileUtil;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Getter
@Setter
public class FlamePlayer extends Player {

    private Profile profile;
    private Cooldown cooldown;

    public FlamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        this.cooldown = new Cooldown(Duration.of(1, ChronoUnit.SECONDS));
    }

    public static FlamePlayer fromPlayer(Player player) {
        return (FlamePlayer) player;
    }

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\w+$");

    public static CompletableFuture<Profile> getProfile(String name) {
        var player = MinecraftServer.getConnectionManager().getPlayer(name);

        if(!(name.length() >= 3 && name.length() <= 16 && USERNAME_PATTERN.matcher(name).matches())) {
            return CompletableFuture.completedFuture(null);
        }

        if(player != null) {
            return CompletableFuture.completedFuture(FlamePlayer.fromPlayer(player).getProfile());
        }

        return ProfileUtil.getProfile(name);
    }

    public static CompletableFuture<Profile> getProfile(UUID uuid) {
        var player = MinecraftServer.getConnectionManager().getPlayer(uuid);

        if(player != null) {
            return CompletableFuture.completedFuture(FlamePlayer.fromPlayer(player).getProfile());
        } else {
            return ProfileUtil.getProfile(uuid);
        }
    }

}
