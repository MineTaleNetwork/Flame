package cc.minetale.flame.util;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class FlamePlayer extends Player {

    @Setter private Profile profile;

    public FlamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    public static FlamePlayer fromPlayer(Player player) {
        return (FlamePlayer) player;
    }

    public static CompletableFuture<Profile> getProfile(String name) {
        var future = new CompletableFuture<Profile>();
        var player = MinecraftServer.getConnectionManager().getPlayer(name);

        if(player != null) {
            future.complete(FlamePlayer.fromPlayer(player).getProfile());
        } else {
            future = ProfileCache.getProfile(name);
        }

        return future;
    }

    public static CompletableFuture<Profile> getProfile(UUID uuid) {
        var player = MinecraftServer.getConnectionManager().getPlayer(uuid);

        if(player != null) {
            return CompletableFuture.completedFuture(FlamePlayer.fromPlayer(player).getProfile());
        } else {
            return ProfileCache.getProfile(uuid);
        }
    }

}
