package cc.minetale.flame.listeners;

import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import net.minestom.server.MinecraftServer;

@PayloadListener
public class PigeonListener implements Listener {

    @PayloadHandler
    public void onProfileUpdate(ProfileUpdatePayload payload) {
        var profile = payload.getProfile();
        var player = MinecraftServer.getConnectionManager().getPlayer(profile.getId());

        if(player != null) {
            var flamePlayer = FlamePlayer.fromPlayer(player);

            flamePlayer.setProfile(profile);
        }
    }

}
