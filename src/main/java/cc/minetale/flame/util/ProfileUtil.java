package cc.minetale.flame.util;

import cc.minetale.commonlib.profile.Profile;
import net.minestom.server.MinecraftServer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProfileUtil {

    public static CompletableFuture<Profile> getProfile(String name) {
        var future = new CompletableFuture<Profile>();
        var player = MinecraftServer.getConnectionManager().getPlayer(name);

        if(player != null) {
            future.complete(FlamePlayer.fromPlayer(player).getProfile());
        } else {
            future = Profile.getProfile(name);
        }

        return future;
    }

    public static CompletableFuture<Profile> getProfile(UUID uuid) {
        var player = MinecraftServer.getConnectionManager().getPlayer(uuid);

        if(player != null) {
            return CompletableFuture.completedFuture(FlamePlayer.fromPlayer(player).getProfile());
        } else {
            return Profile.getProfile(uuid);
        }
    }

}
